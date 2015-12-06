PACKAGEDIR=ParkingEscape
FILES=$(PACKAGEDIR)/*.java

all:
	clear
	rm -f $(PACKAGEDIR)/*.class
	javac $(FILES)
