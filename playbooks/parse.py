import sys, os

def check_args():
    if len(sys.argv) < 3:
        print("usage: parse <infolder> <outfolder>")
        exit(2)


check_args()
infolder = sys.argv[1]
outfolder = sys.argv[2]

infiles = list(os.listdir(infolder))

print(f"{infolder} to {outfolder}")

slot_info = {}
pending = {}

def update(slot, lat, bid):
    if slot not in slot_info[bid]:
        slot_info[bid][slot] = {"txs": 0, "lat": 0}

    slot_info[bid][slot]["txs"] += 1
    slot_info[bid][slot]["lat"] += lat

def register_bid(bid):
    if bid not in slot_info:
        slot_info[bid] = {}
        pending[bid] = {}


for infile in infiles:
    with open(os.path.join(infolder, infile), "r") as infh:

        dumbong = False

        bid = eval(infile.split(".")[0][len("bench"):])
        register_bid(bid)

        outfile = infile.split(".")[0] + ".summary"
        print(f"Summarizing {infile} into {outfile}")
        with open(os.path.join(outfolder, outfile), "w") as outfh:
            for line in infh:
                if line.strip() == "dumbong":
                    dumbong = True
                    continue

                if not dumbong:
                    if line[0] == "#": continue # ignore comments

                    tokens = line.split(",")

                    if tokens[0] == "init":
                        outfh.write(line)
                        continue
                    elif tokens[0] in ["bclat", "balat"]:
                        outfh.write(f"{tokens[0]},{tokens[2]}")
                        continue

                    t = eval(tokens[1])
                    if tokens[0] == "in":
                        i = eval(tokens[2])
                        pending[bid][i] = t
                    elif tokens[0] == "out":
                        slot = int(t / 1000)
                        i = eval(tokens[2])
                        update(slot, t - pending[bid][i], bid)
                        del pending[bid][i]
                else:
                    s = "node"
                    if line[:len(s)] != s:
                        # outfh.write(f"IGNORE: {line}")
                        continue
                    parts = line.split(",")
                    l = float(parts[2].split(':')[1].strip())
                    t = float(parts[3].split(':')[1].strip())
                    # print(f"latency: {l} and throughput: {t}")
                    outfh.write(f"{l}, {t}\n")

            if not dumbong:
                # Write slots
                if len(slot_info[bid]):
                    a = min(slot_info[bid].keys())
                    b = max(slot_info[bid].keys())
                    for slot in range(a, b+1):
                        if slot in slot_info[bid]:
                        # for slot in sorted(slot_info[bid].keys()):
                            outfh.write(f"slot,{slot},{slot_info[bid][slot]['txs']},{slot_info[bid][slot]['lat']}\n")
                        else:
                            outfh.write(f"slot,{slot},{0},{0}\n")
