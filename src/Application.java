public class Application {
    public static void main(String... args) {
        double currentBestFitness = Double.MAX_VALUE;
        Application application = new Application();
                
        if (args.length != 2){
            throw new IllegalArgumentException("Expected 2 arguments, but received" + args.length);
        } 
        else if (args[0].equals("-configuration")) {
            application.runGAConfiguration(args[1]);
        }
    }

    /**
     * Run a Genetic Algorithm (GA) Simulation for the knapsack problem.
     * @param fileName - Name of file to be accessed for config.
     * @return Nothing.
     */ 
    private void runGAConfiguration(String fileName) {
        long runtimeStart = System.currentTimeMillis();
        GAConfiguration config = new GAConfiguration(fileName);
        Population pop = new Population(config);
    }
}






    // private void searchBestConfiguration(String algorithmType) {
    //     int num_configs = algorithmType.equals("ga") ? 28 : 25;
    //     for (int i = 0; i < num_configs; i++) {
    //         runConfiguration(algorithmType + "_default_" + (i + 1) + ".json");
    //     }
    // }