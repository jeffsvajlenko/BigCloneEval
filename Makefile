all: version
	mkdir -p bin/
	javac -d bin/ -cp src/:libs/* src/**/*.java

version: VERSION = $(shell git log -1 --pretty=format:"%h")
version:
	# if sed is not available, provide a fallback
	cp src/util/Version.java.template src/util.Version.java
	-sed "s/%%VERSION%%/$(VERSION)/g" src/util/Version.java.template > src/util/Version.java

clean:
	rm -rf bin/
