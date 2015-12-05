PACKAGEDIR=ParkingEscape
FILES=$(PACKAGEDIR)/*.java

all:
	rm -f $(PACKAGEDIR)/*.class
	javac $(FILES)
