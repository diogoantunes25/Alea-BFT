# Use another base image
FROM maven:3.8.6-amazoncorretto-11 as builder

WORKDIR /alea

COPY alea-java/bls bls

RUN cp /usr/lib/jvm/java-11-amazon-corretto/include/*.h ./bls/mcl/include && \
	cp /usr/lib/jvm/java-11-amazon-corretto/include/linux/*.h ./bls/mcl/include

RUN yum install -y make gcc-c++ gmp-devel.x86_64 python3 && \
	cd bls && \
	make -C ./mcl lib/libmcl.a && \
	cd ffi/java && \
	make test && \
	cd ../../../ && \
	cp ./bls/lib/libblsjava.so /usr/lib
	
COPY alea-java/pom.xml .
COPY alea-java/client/pom.xml client/pom.xml
COPY alea-java/replica/pom.xml replica/pom.xml
COPY alea-java/protocols/pom.xml protocols/pom.xml
COPY alea-java/contract/pom.xml contract/pom.xml
COPY alea-java/utils/pom.xml utils/pom.xml

RUN cd contract && mvn -B dependency:go-offline && cd ..

COPY alea-java/client client
COPY alea-java/replica replica
COPY alea-java/protocols protocols
COPY alea-java/contract contract
COPY alea-java/utils utils

RUN mvn clean install

RUN cd replica && mvn exec:java -Dexec.args="-g 61"

FROM maven:3.8.6-amazoncorretto-17 as runner

WORKDIR /alea

COPY Dumbo_NG Dumbo_NG

RUN yum update -y && \
		yum install -y gcc make bison flex gmp-devel mpfr-devel git openssl-devel libffi-devel wget vim

RUN wget https://www.python.org/ftp/python/3.8.18/Python-3.8.18.tgz && \
		tar xfv Python-3.8.18.tgz && \
		cd Python-3.8.18 && \
		./configure && \
		make && \
		make install && \
		cd ..

RUN cd Dumbo_NG && \
		wget https://crypto.stanford.edu/pbc/files/pbc-0.5.14.tar.gz && \
		tar xfv pbc-0.5.14.tar.gz && \
		cd pbc-0.5.14 && \
		./configure && \
		make && \
		make install && \
		ldconfig /usr/local/lib && \
		echo 'export LIBRARY_PATH=$LIBRARY_PATH:/usr/local/lib' > /root/.profile && \
		echo 'export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib' >> /root/.profile && \
		export LIBRARY_PATH=$LIBRARY_PATH:/usr/local/lib && \
		export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib && \
		cd ..

RUN git clone https://github.com/JHUISI/charm.git && \
		cd charm && \
		./configure.sh && \
		make && \
		make install && \
		cd ..

RUN python3 -m pip install --upgrade pip && \
		pip3 install gevent setuptools gevent numpy ecdsa pysocks gmpy2 zfec gipc pycrypto coincurve pycryptodome

# install deps and useful utils
RUN yum install -y jq iproute-tc iproute gmp-devel.x86_64 gcc-c++ gmp-devel.x86_64 iputils

RUN mkdir /tmp/results
RUN mkdir /tmp/logs

COPY alea-java/bls bls
COPY alea-java/gf-complete gf-complete
COPY alea-java/jerasure jerasure

COPY --from=builder ./alea/replica/target/replica-1.0-SNAPSHOT.jar /alea/replica.jar
COPY --from=builder ./alea/client/target/client-1.0-SNAPSHOT.jar /alea/client.jar
COPY --from=builder /tmp/bls.keys /tmp/bls.keys

COPY alea-java/install_libs.sh /alea/install_libs.sh
RUN /alea/install_libs.sh

COPY java_bench.py "/alea/java_bench.py"
COPY mir_bench.py "/alea/mir_bench.py"

COPY bench "/alea/bench"
RUN chmod a+x "/alea/bench"

COPY dumbo.sh "/alea"
RUN chmod u+x "/alea/dumbo.sh"

WORKDIR /alea

ENTRYPOINT ["/alea/bench"]
