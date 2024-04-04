#! /bin/bash

# NOTE: Intended to be run inside a docker container

function help {
	echo "usage: test-setting lat_avg lat_stddev n type id protocol batch load"
}

function init() {
	echo "==== SETTING UP ENVIRONMENT ===="
	mkdir /tmp/logs
	mkdir /tmp/results
}

function tc_init() {
	echo "==== APPLYING DELAY ===="
	echo "Delay average: $1"
	echo "Delay standar deviation: $2"
	tc qdisc add dev eth0 root netem delay $1ms $2ms
	# launch_latency_monitor
	# test_latency
}

function launch_latency_monitor() {
	echo "==== LAUNCHING LATENCY MONITOR ===="
	echo "Collecting samples every second"

	while true
	do
		for replica in $(cat /tmp/setup-6-hosts.json | jq -r '.replicas[]');
		do
			ping -c 1 $replica \
				| grep icmp_seq \
				| awk '{print $8}' \
				| awk --assign=rid=$replica -F '=' '{print "Latency to " rid " is " $2 " ms"}'
		done &
	done &

}

function test_latency() {
	echo "==== TESTING LATENCY ===="
	echo "Note: tc shaping might have not been applied in peer. Delay might exist in a signle direction"

	for replica in $(cat /tmp/setup-6-hosts.json | jq -r '.replicas[]');
	do
		echo "Testing latency to $replica (3 pings)";
		echo "(running ping -c 3 $replica)"
		ping -c 3 $replica
	done
}

function alea() {
	echo "==== RUNNING BENCH ===="
	echo "n: $1"
	echo "type: $2"
	echo "id: $3"
	echo "protocol: $4"
	echo "batch: $5"
	echo "transaction shuffling: $6"
	echo "delay BA: $7"
	echo "delay BC: $8"
	echo "early BA: $9"
	echo "quick signature verification: ${10}"
	echo "fault: ${11}"
	echo "rep: ${12}"
	if [[ $# -eq 13 ]]; then
		echo "load: ${13}"
	fi
	echo ""
	echo "hosts_file:"
	cat /tmp/setup-6-hosts.json

	if [[ "$4" == "mir-"* ]]; then
		mirbench "$@"
	else
		javabench "$@"
	fi
}

function mirbench() {
	echo "==== RUNNING MIR ===="
	local id="$3"
	local protocol="${4#mir-}" # remove "mir-" prefix
	local batchsize="1"
	local load="$5" # may be empty

	N="$(cat /tmp/setup-6-hosts.mir.json | jq '.validators | length')"
	RES_PREFIX="/tmp/results/benchmark-$protocol-$N"


	if [[ "$type" == "replica" ]]; then
		/alea/mirbench node -p "$protocol" -o "$RES_PREFIX-replica$id" -b "$batchsize" --statPeriod 1s -m /tmp/setup-6-hosts.mir.json -i "$id"
	else
		if [[ -n "$load" ]]; then
			/alea/mirbench client -t rr -T 500s -r "$load" -i "$id" -m /tmp/setup-6-hosts.mir.json -o "$RES_PREFIX-client$id-$load"
		else
			/alea/mirbench latclient -t rr -i "$id" -m /tmp/setup-6-hosts.mir.json -o "$RES_PREFIX-latclient$id"
		fi
	fi
}

function javabench() {
	echo "==== RUNNING JAVA ===="

	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/lib

	# Stress (throughput) test
	if [[ $# -eq 13 ]]; then
		java -Djna.library.path=/usr/lib -Xmx10G -Xms5G -jar /alea/setups.jar -n $1 -type $2 -id $3 -protocol $4 -stressTest -load ${13} -batch $5 -txtShuffle $6 -delayBA $7 -delayBC $8 -earlyBA $9 -quickSigV ${10} -fault ${11} -rep ${12}

	# Latency test
	else
		java -Djna.library.path=/usr/lib -Xmx10G -Xms5G -jar /alea/setups.jar -n $1 -type $2 -id $3 -protocol $4 -batch $5 -txtShuffle $6 -delayBA $7 -delayBC $8 -earlyBA $9 -quickSigV ${10} -fault ${11} -rep ${12}
	fi
}


LOG_DIR=/tmp/logs/$4$5.log

function main() {
	echo "==== STARTED BENCHMARK ===="
	echo "arguments: $@"

	init
	tc_init $1 $2
	shift 2
	alea $@


	echo "==== ENDED BENCHMARK ===="
}

main $@ &>> $LOG_DIR
