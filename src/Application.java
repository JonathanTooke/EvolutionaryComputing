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
        long startTime = System.currentTimeMillis();
        Population population = new Population(config);
        Report report = new Report(fileName, config);
        for(int i = 0; i < Configuration.MAX_ITERATIONS; i++){
            report.addIteration(i, population.getFittestKnapsack());
            if(i % 100 == 0){
                System.out.println(population.getSummaryStats());
            }
            population.evolve();
        }
        long completeTime = System.currentTimeMillis() - startTime;
        report.save(completeTime + "");
    }
}