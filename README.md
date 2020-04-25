# Evolutionary Computing Assignment

Implementation of Gentic (GA), Particle Swarm Optimization (PSO), and Simulated Annealing (SA) algorithms from scratch to solve the [knapsack problem](https://en.wikipedia.org/wiki/Knapsack_problem) (a type of combinatorial optimization).

## Running the program
1. Navigate to the directory containing the Makefile. i.e. this directory
2. Type make (in terminal) to compile the java code to class files.
3. Navigate to the bin directory and run the code directly with java Application -[arg1] [arg2] where args can be:
    * -search_best_configuration [ga|pso|sa] (to run all available confiurations against the algorithm type) OR
    * -configuration [filename] (for a specific configuration)
4. Alteratively, the arguments attached to "make run" in the Makefile can be changed and the code can be run with the "make run" command directly.

## Custom Configurations

A custom configuration ahs been created for PSO and for SA to demonstrate the capabilities of the algorithm given different constraints. In the case of PSO it was by increasing the swarm size, and the case of SA it was decreasing the cooling rate. These configurations provide better solutions than the default ones, but are not considered in the search_best_configuration argument.

## Inheritance Hierarchy Explained
* SimulationManager is the parent to Population (GA), SimulatedAnnealing (SA), Swarm (PSO). These classes manage the simulation for their respective algorithms and are invoked from the Application.
* Knapsack is the parent to Chromosome (GA), SACandidate (SA), Particle (PSO). These classes manage the individual solutions in the set of solutions for a given algorithm.
* Configuration has a similar inheritance hierarchy, provided individual configuration to each algorithm type. All parameters necessary for the algorithm, but not contained in the json configuration files, can be adjusted here.

     