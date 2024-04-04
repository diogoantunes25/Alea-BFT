#! /bin/bash

function help() {
	echo "Generate keys: <n> <f>"

}

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib
export LIBRARY_PATH=$LIBRARY_PATH:/usr/local/lib
export PYTHONPATH=/alea/Dumbo_NG

mkdir "keys-$1"
python3 Dumbo_NG/crypto/threshenc/generate_keys.py $1 $2
python3 Dumbo_NG/crypto/ecdsa/generate_keys_ecdsa.py $1
python3 Dumbo_NG/crypto/threshsig/generate_keys.py $1 $2
python3 Dumbo_NG/crypto/threshsig/generate_keys.py $1 $(($1 - $2))
