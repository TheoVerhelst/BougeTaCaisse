PACKAGE=ParkingEscape
JAVAFILES=$(wildcard $(PACKAGE)/*.java)
CLASSFILES=$(JAVAFILES:.java=.class)
MAINCLASS=Main
TESTFILE=Parking.txt
OUT1=out1.txt
OUT2=out2.txt

all: clear test

clear:
	clear

$(CLASSFILES):%.class: %.java
	javac $< -Xlint

clean:
	rm -f $(CLASSFILES)

test: $(CLASSFILES)
	java $(PACKAGE).$(MAINCLASS) $(TESTFILE) $(OUT1) $(OUT2)
