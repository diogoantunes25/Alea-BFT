FROM ubuntu:focal as NGRUNNER

WORKDIR /DumboNG

COPY . .

RUN apt update && \
	apt install -y make bison flex libgmp-dev libmpc-dev libssl-dev zlib1g-dev wget git libffi-dev psmisc vim

# Install python
RUN wget https://www.python.org/ftp/python/3.8.18/Python-3.8.18.tgz && \
	tar xfv Python-3.8.18.tgz && \
	cd Python-3.8.18 && \
	./configure && \
	make && \
	make install && \
	cd ..

# Install pbc
RUN wget https://crypto.stanford.edu/pbc/files/pbc-0.5.14.tar.gz && \
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

# Install charm
RUN git clone https://github.com/JHUISI/charm.git && \
	cd charm && \
	./configure.sh && \
	make && \
	make install && \
	cd ..

RUN python3 -m pip install --upgrade pip && \
	pip3 install gevent setuptools gevent numpy ecdsa pysocks gmpy2 zfec gipc pycrypto coincurve pycryptodome
