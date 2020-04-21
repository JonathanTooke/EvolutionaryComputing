public class Application {
    public static void main(String... args) {                
        if (args.length != 2){
            throw new IllegalArgumentException("Expected 2 arguments, but received" + args.length);
        } 
        else if (args[0].equals("-configuration")) {
            runGAConfiguration(args[1]);
        }
    }

    /**
     * Run a Genetic Algorithm (GA) Simulation for the knapsack problem.
     * @param fileName - Name of file to be accessed for config.
     * @return Nothing.
     */ 
    private static void runGAConfiguration(String fileName) {
        GAConfiguration config = new GAConfiguration(fileName);
        Population population = new Population(config);
        Knapsack bestKnapsack = population.getBestKnapsack();
        for(int i = 0; i < Configuration.MAX_ITERATIONS; i++){
            bestKnapsack = population.getBestKnapsack();
            if(i % 100 == 0){
                System.out.println(bestKnapsack.getFitness());
            }
            population.evolve();
            bestKnapsack = population.getBestKnapsack();

        }
    }

}