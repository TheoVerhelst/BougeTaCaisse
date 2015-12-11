PACKAGE=ParkingEscape
FILES=$(PACKAGE)/*.java
MAINCLASS=Main
TESTFILE=Parking.txt

all: clear build

clear:
	clear

build: clean
	javac $(FILES) -Xlint

clean:
	rm -f $(PACKAGE)/*.class

test:
	java $(PACKAGE).$(MAINCLASS) $(TESTFILE)
