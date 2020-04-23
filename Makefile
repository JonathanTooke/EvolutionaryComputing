JAVAC = /usr/bin/javac
.SUFFIXES: .java .class

SRCDIR=src
BINDIR=bin
CLASSES=MersenneTwister.class Configuration.class KnapsackItem.class GAConfiguration.class Knapsack.class Population.class Application.class Report.class

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR):$(SRCDIR): $<

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

SRC_FILES=$(SRC:%.java=$(SRCDIR)/%.java)

default: $(CLASS_FILES)

run:
	java -cp $(BINDIR) Application -configuration ga_default_14.json

clean:
	rm $(BINDIR)/*.class