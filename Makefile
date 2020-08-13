JAVAC = /usr/bin/javac
.SUFFIXES: .java .class

SRCDIR=src
BINDIR=bin
CLASSES=MersenneTwister.class Configuration.class KnapsackItem.class Chromosome.class Knapsack.class Population.class PopulationConfiguration.class Application.class Report.class Swarm.class Particle.class SwarmConfiguration.class SimulatedAnnealing.class SimulationManager.class SimulatedAnnealingConfiguration.class SACandidate.class

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR):$(SRCDIR): $<

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

SRC_FILES=$(SRC:%.java=$(SRCDIR)/%.java)

default: $(CLASS_FILES)

run:
	java -cp $(BINDIR) Application -configuration pso_custom_01.json

clean:
	rm $(BINDIR)/*.class