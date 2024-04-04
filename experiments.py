#! /usr/bin/env python3

import json, ansible_runner, os, json, sys, shutil, time, random, re
from ansible_runner.interface import run as arun
import subprocess as sp

skip_build = True
skip_push = True
docker_only = False

ANSIBLE_VERBOSE = True
ANSIBLE_FORKS = 20

MAX_DURATION = 3 * 60 # in seconds

CORES_PER_CLIENT = 4
CORES_PER_REPLICA = 4

AVAIL_OPTS = ["txtShuffle", "delayBA", "delayBC", "earlyBA", "quickSigV"]

OVERLAY_NETWORK = "alea-net"
PARTIES_FILE = "/tmp/parties.json"

DUMP_FILE = "experiments.dump"
PARSE_FILE = "parse.py"

DEBUG = False
DOCKER_OWNER = "diogoantunes25"
#DOCKER_OWNER = "abread"
DOCKER_IMAGE = f"{DOCKER_OWNER}/bft_benchmarks" if not DEBUG else f"{DOCKER_OWNER}/bft_benchmarks_debug"

_nonce = None

def nonce():
    global _nonce

    if _nonce is None:
        _nonce = random.randrange(100000)

    return _nonce

def check_args():
    """
    Cheks sanity of arguments passed to this script
    """

    if len(sys.argv) < 2:
        print("usage: experiments <settings file>")
        exit(1)

def undo_ssh_alias(host):
    cmd = f"ssh -G {host}"
    p = sp.run(cmd.split(" "), stdout = sp.PIPE)
    props = p.stdout.decode("utf-8").split("\n")
    for s in props:
        a, b = s.split(" ")
        if a == "hostname": return b

    print(f"Can't reverse the ssh alias '{host}'")
    exit(2)

def gen_inventory(n_replicas, n_clients, hosts):
    """
    Generates the inventory that distributes n_replicas and n_clients
    in hosts.
    Returns the a map from the replica/client id to the ip where it will run.
    """

    inv = open(f"./playbooks/inventory{nonce()}.ini", "w")

    assignment = {"replica": {}, "client": {}}

    inv.write("[hosts]\n")
    for i, host in enumerate(hosts):
        cores = hosts[host]
        replicas = []
        while cores >= CORES_PER_REPLICA and n_replicas > 0:
            cores -= CORES_PER_REPLICA
            n_replicas -= 1
            replicas += [n_replicas]
            assignment["replica"][n_replicas] = host

        clients = []
        while cores >= CORES_PER_CLIENT and n_clients > 0:
            cores -= CORES_PER_CLIENT
            n_clients -= 1
            clients += [n_clients]
            assignment["client"][n_clients] = host

        ip = undo_ssh_alias(host)
        # Record host and the clients/replicas that will run there
        inv.write(f"host{i} ip='{ip}' ansible_host='{host}' clients={str(clients).replace(' ', '')} replicas={str(replicas).replace(' ', '')}\n")

    if n_clients > 0 or n_replicas > 0:
        raise Exception(f"Not enough cores ({n_clients} client and {n_replicas} replicas with no machine)")

    inv.close()

    return assignment

def load_settings(settings_file):
    return json.load(open(settings_file, "r"))

def provision(assignment: dict[str, dict], cleanup=True):
    """
    Provision machines by installing required software and copying required files.
    """

    gen_parties_file(len(assignment['replica']), len(assignment['client']))
    vars = provision_vars_dict(cleanup)
    run_playbook("provision", vars)

def filter_configs(configs):
    all_configs = list(enumerate(configs))

    if len(sys.argv) <= 2 or len(sys.argv[2]) == 0:
        # no filter
        return all_configs

    filter_str = sys.argv[2]
    selected_configs = set()
    for el in filter_str.split(','):
        if '-' in el:
            first, last = el.split('-')
            first = 0 if first == "" else int(first)
            last = len(all_configs) if last == "" else int(last)
            if first > last:
                print(f"Invalid range: {first}-{last}, {first} must be less or equal than {last}")
                sys.exit(1)

            for i in range(first, last+1):
                selected_configs.add(i)
        else:
            selected_configs.add(int(el))

    return list(filter(lambda pair: pair[0] in selected_configs, all_configs))

def get_settings():
    with open(sys.argv[1], "r") as fh:
        return json.load(fh)

def get_all_configs():
    """
    Returns the list of all configurations to run, the required number of
    replica machines and client machines.
    """

    configs = get_settings()["configs"]

    all_configs = []
    ns_replicas = []
    ns_clients = []
    for e in configs:
        for n in e["n_replicas"]:
            ns_replicas += e["n_replicas"]
            ns_clients += [e["n_clients"]]
            for protocol in [p for p in e["protocol"] if p != "ng"]:
                for batch in e["batch"]:
                    if "load" in e:
                        for load in e["load"]:
                            for rep in range(int(e["reps"])):
                                all_configs.append({
                                    "n": n,
                                    "n_clients": e["n_clients"],
                                    "protocol": protocol,
                                    "lat_avg": e["lat_avg"],
                                    "lat_stddev": e["lat_stddev"],
                                    "load": load,
                                    "batch": batch,
                                    "rep": rep,
                                    "fault": e["fault"] if "fault" in e else "free",
                                    "optimizations": e["optimizations"]
                                        if "optimizations" in e else AVAIL_OPTS
                                })
                    else:
                        for rep in range(int(e["reps"])):
                            all_configs.append({
                                "n": n,
                                "n_clients": e["n_clients"],
                                "protocol": protocol,
                                "lat_avg": e["lat_avg"],
                                "lat_stddev": e["lat_stddev"],
                                "batch": batch,
                                "fault": e["fault"] if "fault" in e else "free",
                                "rep": rep,
                                "optimizations": e["optimizations"]
                                    if "optimizations" in e else AVAIL_OPTS
                            })
            if "ng" in e["protocol"]:
                for batch in e["batch"]:
                    for rep in range(int(e["reps"])):
                        all_configs.append({
                            "n": n,
                            "n_clients": e["n_clients"],
                            "protocol": "ng",
                            "lat_avg": e["lat_avg"],
                            "lat_stddev": e["lat_stddev"],
                            "load": 1,
                            "batch": batch,
                            "rep": rep,
                            "fault": e["fault"] if "fault" in e else "free",
                            "optimizations": e["optimizations"]
                                if "optimizations" in e else AVAIL_OPTS
                        })

    return all_configs, max(ns_replicas), max(ns_clients)

def provision_vars_dict(cleanup):
    vars = {}
    vars["home_dir"] = "${HOME}"
    vars["alea_image_name"] = f"{DOCKER_IMAGE}"
    vars["parties_file"] = f"{PARTIES_FILE}"
    vars["overlay_net"] = f"{OVERLAY_NETWORK}"
    vars["cleanup"] = cleanup

    return vars

def run_vars_dict(lat_avg, lat_stddev, n, n_clients, bid):
    vars = {}
    vars["home_dir"] = "${HOME}"
    vars["alea_image_name"] = f"{DOCKER_IMAGE}"
    vars["overlay_net"] = f"{OVERLAY_NETWORK}"
    vars["parties_file"] = f"{PARTIES_FILE}"

    vars["lat_avg"] = f"{lat_avg}"
    vars["lat_stddev"] = f"{lat_stddev}"

    vars["cores_replica"] = f"{CORES_PER_REPLICA}"
    vars["cores_client"] = f"{CORES_PER_CLIENT}"

    vars["n"] = n
    vars["n_clients"] = n_clients

    vars["bench_id"] = f"{bid}"

    return vars

def stats_vars_dict(cleanup):
    vars = {}

    vars["home_dir"] = "${HOME}"
    vars["alea_image_name"] = f"{DOCKER_IMAGE}"
    vars["parse_file"] = f"{PARSE_FILE}"
    vars["cleanup"] = cleanup

    return vars

def gen_config_files(configs, max_replicas, max_clients):
    # Folder setup
    if os.path.exists("/tmp/alea_settings"):
        shutil.rmtree("/tmp/alea_settings")

    os.mkdir("/tmp/alea_settings")
    os.mkdir("/tmp/alea_settings/clients")
    os.mkdir("/tmp/alea_settings/replicas")

    for i in range(max_clients):
        os.mkdir(f"/tmp/alea_settings/clients/{i}")

    for i in range(max_replicas):
        os.mkdir(f"/tmp/alea_settings/replicas/{i}")

    # Generate a file per host per config
    for i, config in enumerate(configs):
        n_replicas = config["n"]
        n_clients = config["n_clients"]
        for j in range(n_replicas):
            with open(f"/tmp/alea_settings/replicas/{j}/setting{i}.json", "w") as fh:
                c = config | {"id": j, "type": "replica"}
                json.dump(c, fh)

        for j in range(n_clients):
            with open(f"/tmp/alea_settings/clients/{j}/setting{i}.json", "w") as fh:
                c = config | {"id": j, "type": "client"}
                json.dump(c, fh)

def gen_parties_file(n_replicas, n_clients):
    """
    Generates the parties file (for the replicas and clients to use) from the
    assignment of hosts to parties.
    """
    with open(PARTIES_FILE, "w") as fh:
        c = {
                "replicas": [f"replica{i}" for i in range(n_replicas)],
                "clients": [f"client{i}" for i in range(n_clients)]
            }

        json.dump(c, fh)

def wait_host(host, time_left):
    if time_left < 0:
        time_left = 0.5

    def check(p):
        if p.returncode < 0:
            print(f"Pushing failed. Dumping to {DUMP_FILE}")
            exit(2)

    # print(f"{host}...", end = "", flush = True)
    end = time.monotonic() + time_left
    done = False
    while not done and time.monotonic() < end:
        p = sp.run(["ssh", host, "sudo docker ps"], stderr = sp.DEVNULL, stdout = sp.PIPE, timeout=max((end-time.monotonic())/2, 2))
        check(p)
        count = len(p.stdout.decode('utf-8').strip().split('\n')) - 1
        done = (count == 0)
    # print("done ", end = "", flush = True)

def wait_machines(machines):
    # print("Waiting for end... ", end = "", flush = True)

    end = time.monotonic() + MAX_DURATION
    for machine in machines:
        if time.monotonic() < end:
            wait_host(machine, end - time.monotonic())
        else:
            print(f"Skipping {machine} - already timed out")
    # print("Done all.")
    pass

def launch(i, config):
    """
    Launches the machines required for a given configuration
    Waits for end of execution
    """

    vars = run_vars_dict(config["lat_avg"], config["lat_stddev"], config["n"], config["n_clients"], i)
    run_playbook("start", vars)

def get_hosts():
    """
    Retrieve list with hostnames for hosts being used
    """
    settings = get_settings()
    return list(settings["hosts"].keys())

def rename_summaries(configs):
    """
    Change name from bench{id}.summary to something more descriptive
    """

    def get_name(config):
        n = config["n"]
        protocol = config["protocol"]
        batch = config["batch"]
        load = config["load"]
        lat_avg = config["lat_avg"]
        lat_stddev = config["lat_stddev"]
        rep = config["rep"]
        crash = config["fault"]

        return f"{n}-{protocol}-{batch}-{load}-{lat_avg}-{lat_stddev}-{rep}-{crash}"

    last_run = len(os.listdir("./summaries")) - 1
    base = f"./summaries/run_{last_run}"

    print(f"Renaming summaries for run {last_run}")

    for rfolder in os.listdir(os.path.join(base, "replicas")):
        print(f"Renaming replica {rfolder}")
        for i, config in enumerate(configs):
            f = os.path.join(base, "replicas", rfolder)
            old_name = os.path.join(f, f"bench{i}.summary")
            if os.path.exists(old_name):
                new_name = os.path.join(f, f"{get_name(config)}.csv")
                os.rename(old_name, new_name)

def stats(ass, configs, cleanup=True):
    """
    Given the results, computes the desired stats
    """
    vars = stats_vars_dict(cleanup)
    run_playbook("stats", vars)

    print(ass)
    for host in get_hosts():
        for i in ass["replica"]:
            if ass["replica"][i] == host:
                p = sp.run(["ssh", host, f"python3 /tmp/{PARSE_FILE} /tmp/alea_results/replicas/{i} /tmp/alea_summaries/replicas/{i}"], stderr = sp.STDOUT)

                if p.returncode < 0:
                    print(f"Computing stats failed. Dumping to {DUMP_FILE}")
                    exit(2)

    run_playbook("end", vars)
    rename_summaries(configs)

def run_playbook(name, vars, quiet = not ANSIBLE_VERBOSE):
    playbook = os.path.abspath(f"./playbooks/{name}.yml")
    inventory = os.path.abspath(f"./playbooks/inventory{nonce()}.ini")
    runner = arun(playbook = playbook, inventory = inventory, quiet = quiet, forks = ANSIBLE_FORKS, extravars = vars)
    if len(runner.stats["failures"]) > 0:
        with open(DUMP_FILE, "w") as fh:
            fh.write(str(runner) + "\n")
            print(f"Start failed - exiting. Dumped runner object to {DUMP_FILE}.")
            exit(2)

def docker_build_push():
    build_cmd = f"sudo docker build --tag {DOCKER_IMAGE} ."
    push_cmd = f"sudo docker push {DOCKER_IMAGE}"

    if not skip_build:
        p1 = sp.run(build_cmd.split(" "), stderr = sp.STDOUT)
        if p1.returncode < 0:
            print(f"Build failed. Dumping to {DUMP_FILE}")
            exit(2)
    else:
        print("WARNING: Skipping build")

    if not skip_push:
        p2 = sp.run(push_cmd.split(" "), stderr = sp.STDOUT)
        if p2.returncode < 0:
            print(f"Pushing failed. Dumping to {DUMP_FILE}")
            exit(2)
    else:
        print("WARNING: Skipping push")

    if docker_only: exit(0)

def clean():
    # TODO: delete inventory
    pass

def main():
    check_args()

    all_configs, n_replicas, n_clients = get_all_configs()
    print(f"Generating the config files...")
    gen_config_files(all_configs, n_replicas, n_clients)

    total_exps = len(all_configs)
    configs = filter_configs(all_configs)
    print(f"Running {len(configs)} configs out of {total_exps}")

    docker_build_push()
    print("Done with docker")

    print(f"Provisioning...", end = "", flush = True)
    settings = get_settings()
    assignment = gen_inventory(n_replicas, n_clients, settings["hosts"])
    provision(assignment, cleanup=(len(configs) == total_exps))
    print("Done")

    i = 1
    for id, config in configs:
        print(f"Running {i}/{len(configs)} (id={id} out of {total_exps})...", end = "", flush = True)
        s = time.time()

        launch(id, config)
        print(f"Launched exp. id={id}...", end = "", flush = True)
        wait_machines(get_hosts())

        print(f"Exp. id={id} took {int(time.time() - s)} seconds")
        i += 1

    stats(assignment, all_configs, cleanup=(len(configs) == total_exps))
    clean()

if __name__ == "__main__":
    main()
