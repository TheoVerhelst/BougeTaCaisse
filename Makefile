FILES=*.java

all:
	rm -f *.class
	javac $(FILES)
