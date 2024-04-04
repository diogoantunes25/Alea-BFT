#! /bin/bash

main() {
	echo "Running dumbo.sh"
	cd Dumbo_NG

	echo "dumbong" >> $6

	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib
	export LIBRARY_PATH=$LIBRARY_PATH:/usr/local/lib

	echo "Hosts 1"
	cat ../hosts.config
	cp ../hosts.config .
	cp ../hosts.config hosts1_config

	echo "Hosts 2"
	cat ./hosts.config

	ping -c 5 replica0
	ping -c 5 replica1
	ping -c 5 replica2
	ping -c 5 replica3

	if [[ $# -gt 6 ]]; then
		echo "python3 run_socket_node.py --sid 'sidA' --id $1 --N $2 --f $3 --B $4 --S 100 --P "ng" --D True --O True --C 0 --M True"
		python3 run_socket_node.py --sid 'sidA' --id $1 --N $2 --f $3 --B $4 --S 100 --P "ng" --D True --O True --C 0 --M True &>> $6
	else
		echo "python3 run_socket_node.py --sid 'sidA' --id $1 --N $2 --f $3 --B $4 --S 100 --P "ng" --D True --O True --C 0"
		python3 run_socket_node.py --sid 'sidA' --id $1 --N $2 --f $3 --B $4 --S 100 --P "ng" --D True --O True --C 0 &>> $6
	fi
}

main $@ &>> $5
