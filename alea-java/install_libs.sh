#! /bin/sh

install_bls() {
	# Copy java header files
	cp /usr/lib/jvm/java-17-amazon-corretto/include/*.h ./bls/mcl/include && \
	cp /usr/lib/jvm/java-17-amazon-corretto/include/linux/*.h ./bls/mcl/include

	# Compile
	cd bls && \
	make -C ./mcl lib/libmcl.a && \
	cd ffi/java && \
	make test && \
	cd ../../../ && \
	cp ./bls/lib/libblsjava.so /usr/lib
}

install_autoconf() {
	# Install automake and autoreconf (needed for Jeasure, yum does not provided
	# required version

	yum install -y wget perl perl-Data-Dumper && \
	wget https://ftp.gnu.org/gnu/m4/m4-1.4.19.tar.gz && \
	wget https://ftp.gnu.org/gnu/autoconf/autoconf-2.71.tar.gz && \
	tar xfv m4-1.4.19.tar.gz && \
	tar xfv autoconf-2.71.tar.gz && \
	cd m4-1.4.19 && \
	./configure && make && make install && \
	cd ../autoconf-2.71 && \
	./configure && make && make install && \
	cd ..
}

install_gfcomplete() {
	yum -y install automake libtool

	cd gf-complete && \
	./autogen.sh && \
	./configure && \
	make && \
	make install && \
	cd ..

	cp /usr/local/lib/libgf_complete* /usr/lib
}

install_jerasure() {

	cd jerasure && \
	autoreconf --force --install -I m4 && \
	    ./configure && \
	    make && \
	    make install && \

	cd ..

	cp /usr/local/lib/libJerasure* /usr/lib

	yum install -y execstack

	execstack -c /usr/lib/libJerasure.so
}

main() {
	echo "Running in bash"
	install_bls
	install_autoconf
	install_gfcomplete
	install_jerasure
}

which bash

if test $? -eq 0; then
	# Bash exists
	main $@
	exit 0
else
	# Install bash and rerun
	apk add bash	
	bash $0
	exit 0
fi
