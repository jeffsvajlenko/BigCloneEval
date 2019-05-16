all:
	mkdir bin/
	javac -d bin/ -cp src/:libs/* src/**/*.java
clean:
	rm -rf bin/
