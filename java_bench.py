import os, subprocess, sys, json, time, socket
from subprocess import Popen
import subprocess as sp

MIN_MEM = "5G"
MAX_MEM = "10G"

CLIENT_JAR = "/alea/client.jar"
REPLICA_JAR = "/alea/replica.jar"

LOG_FOLDER = "/tmp/logs"
SETTINGS_FOLDER = "/tmp/settings"
RESULTS_FOLDER = "/tmp/results"
PARTIES_FILE = "/tmp/parties.json"

logged = False

def log(thing):
    global logged

    if not logged:
        with open(get_log_file(), "w") as fh:
            fh.write(thing)
        logged = True
    else:
        with open(get_log_file(), "a") as fh:
            fh.write(thing)

def get_bench_id():
    """
    Reads program arguments and gets the id for the
    current benchmarking execution
    """

    if "BENCH_ID" in os.environ:
        return eval(os.environ["BENCH_ID"])

    raise Exception("Unknown benchmark id")

def get_log_file():
    """
    Returns the log file name for the current execution
    """
    return f"{LOG_FOLDER}/bench{get_bench_id()}.log"

def get_results_file():
    """
    Returns the results file name for the current execution
    """
    return f"{RESULTS_FOLDER}/bench{get_bench_id()}.results"

def java_cmd_tokens():
    tokens = ["java", "-Djna.library.path=/usr/lib"]
    tokens.append(f"-Xmx{MAX_MEM}")
    tokens.append(f"-Xms{MIN_MEM}")
    tokens.append("-jar")

    return tokens

def ng_cmd_tokens(N, f, B, i):
    tokens = ["./dumbo.sh", str(i), str(N), str(f), str(B), get_log_file(), get_results_file()]
    return tokens

def check(p):
    if p.returncode < 0:
        log(f"Process returned non zero error code")
        exit(2)

def client_run(setting):
    protocol = setting["protocol"]
    if protocol != "ng":
        tokens = java_cmd_tokens()
        tokens.append(f"client.jar")
        tokens.append(f"{setting['load']}")
        tokens.append(f"{setting['protocol']}")
        tokens.append(f"{setting['id']}")
        tokens.append(f"{setting['n']}")
        tokens.append(f"{PARTIES_FILE}")
        tokens.append(f"{get_results_file()}")

        log(str(tokens) + "\n")
        log("Running java" + "\n")
        try:
            p = sp.run(tokens, stderr = sp.STDOUT, stdout = sp.PIPE)
        finally:
            log(p.stdout.decode('utf-8'))
        # FIXME: Pipe might fill up, might need a timer to kill this

def replica_run(setting):
    protocol = setting["protocol"]
    if protocol != "ng":
        tokens = java_cmd_tokens()
        tokens.append(f"replica.jar")
        tokens.append(f"{setting['batch']}")
        tokens.append(f"{setting['protocol']}")
        tokens.append(f"{setting['id']}")
        tokens.append(f"{setting['n']}")
        tokens.append(f"{setting['n_clients']}")
        tokens.append(f"{PARTIES_FILE}")
        tokens.append(f"{get_results_file()}")
        tokens.append(f"{setting['fault']}")

        log(str(tokens) + "\n")
        log("Running java" + "\n")
        p = sp.run(tokens, stderr = sp.STDOUT, stdout = sp.PIPE)
        log(p.stdout.decode('utf-8'))

    else:
        n = setting['n']
        f = int((n - 1) / 3)
        b = setting['batch']
        i = setting['id']
        gen_ng_hosts(int(n))
        tokens = ng_cmd_tokens(n, f, b, i)

        if setting['fault'] == "crash" and i < (n-1)/3:
            tokens.append("true")

        log(str(tokens) + "\n")
        with open(get_log_file(), "a") as fh:
            p = sp.Popen(tokens, stderr = sp.STDOUT, stdout = sp.PIPE, start_new_session=True)
            try:
                p.wait(timeout=90)
            except subprocess.TimeoutExpired:
                print(f'Timeout for {cmd} ({timeout_s}s) expired', file=sys.stderr)
                print('Terminating the whole process group...', file=sys.stderr)
                os.killpg(os.getpgid(p.pid), signal.SIGTERM)

        log("Dumbo Done\n")
        log(p.stdout.decode('utf-8'))

    # FIXME: Pipe might fill up, might need a timer to kill this

def gen_ng_hosts(n):
    with open("hosts.config", "w") as fh:
        for i in range(n):
            done = False
            while not done:
                try:
                    ip = socket.gethostbyname(f'replica{i}')
                    fh.write(f"{i} {ip} {ip} {10000 + i * 200}\n")
                    log(f"{i} {ip} {ip} {10000 + i * 200}\n")
                    done = True
                    time.sleep(1)
                except Exception as e:
                    log(str(e))

def is_client(settings):
    return settings["type"] == "client"

def run(settings):
    if is_client(settings):
        client_run(settings)
    else:
        replica_run(settings)
