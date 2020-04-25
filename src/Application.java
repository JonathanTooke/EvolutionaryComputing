import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Class controlling logic for main application loop supporting
 * Genetic Alorithms (GA), Simulated Annealing (SA), and 
 * Particle Swarm Optimization (PSO).
 */
public class Application {
    /**
     * Process command line arguments and launch application loop.
     * @param args 
     *  - args[0] = "-configuration" ... run simulation for specific file type args[1]
     *      - args[1] = [ga/sa/pso]_default_[fileNumber].json
     *  - args[0] = "-search_best_configuration" ... run simulation for configurations of all type args[1]
     *      - args[1] = [ga/sa/pso]
     */
    public static void main(String... args) {                
        if (args.length != 2){
            throw new IllegalArgumentException("Expected 2 arguments, but received" + args.length);
        } 
        else if (args[0].equals("-configuration")) {
            buildConfiguration(args[1]);
        }
        else if (args[0].equals("-search_best_configuration")){
            searchBestConfiguration(args[1]);
        }
        else{
            throw new RuntimeException("Invalid flag supplied as argument to application.");
        }
    }

    /**
     * Search for the best configuration for a given configuration type.
     */
    private static void searchBestConfiguration(String configurationType){
        ArrayList<Report> configurationReports = new ArrayList<>();
        int numIterations = 0;
        switch(configurationType){
            case("ga"):
                numIterations = PopulationConfiguration.NUM_CONFIGURATIONS;
                break;
            case("pso"):
                numIterations = SwarmConfiguration.NUM_CONFIGURATIONS;
                break;
            case("sa"):
                numIterations = SimulatedAnnealingConfiguration.NUM_CONFIGURATIONS;
                break;
            default:
                throw new RuntimeException("Invalid configuration type supplied as argument to application.");
        }
    
        for(int i = 0; i < numIterations; i++){
            String fileNumber = (i + 1) + "";
            if(i < 9)
                fileNumber = "0" + fileNumber;
            configurationReports.add(buildConfiguration(configurationType + "_default_" + fileNumber + ".json"));
        }

        Collections.sort(configurationReports);
        configurationReports.get(0).saveJson("data/results/best_configurations/", configurationType);
    }

    /**
     * Builds a configuration for a specified file name, runs it, and saves it.
     * @param fileName - the configuration to be run.
     * @return report - the report that is generated.
     */
    private static Report buildConfiguration(String fileName){
        Configuration config;
        SimulationManager simulationManager;
        String configurationType;
        if(fileName.matches("^ga.*")){
            config = new PopulationConfiguration(fileName);
            simulationManager = new Population((PopulationConfiguration)config);
            configurationType = "ga";
        }
        else if(fileName.matches("^pso.*")){
            config = new SwarmConfiguration(fileName);
            simulationManager = new Swarm((SwarmConfiguration)config);
            configurationType = "pso";
        }
        else if(fileName.matches("^sa.*")){
            config = new SimulatedAnnealingConfiguration(fileName);
            simulationManager = new SimulatedAnnealing((SimulatedAnnealingConfiguration)config);
            configurationType = "sa";
        }
        else{
            throw new RuntimeException("Invalid configuration file name supplied.");
        }

        Report report = runConfiguration(fileName, config, simulationManager);
        report.save("data/results/" + configurationType + "/report_" + fileName.substring(0, fileName.length() - 5) + "_" + generateDateString() + ".txt");
        return report;
    }

    /**
     * Runs a specific configuration and generates the report.
     * @param fileName - the filename of the configuration to be run.
     * @param config - the configuration to be run.
     * @param simulationManager - the simulation manager.
     * @return report - generated report.
     */
    private static Report runConfiguration(String fileName, Configuration config, SimulationManager simulationManager){
        Report report = new Report(fileName, config);
        long startTime = System.currentTimeMillis();

        for(int i = 0; i < Configuration.MAX_ITERATIONS; i++){
            Knapsack fittestKnapsack = simulationManager.execute();
            if(fittestKnapsack == null)
                break;
            report.addIteration(fittestKnapsack);
            if(i % 10 == 0)
                System.out.println(simulationManager.getSummaryStats());
        }

        long completeTime = System.currentTimeMillis() - startTime;
        report.setCompleteTime(completeTime);

        return report;
    }

    /**
     * Generate date string in format yyyymmdd
     * @return String - date string
     */
    private static String generateDateString(){
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year  = localDate.getYear();
        int month = localDate.getMonthValue();
        int day   = localDate.getDayOfMonth();
        return "" + year + month + day;
    }
}