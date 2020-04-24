import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Class controlling logic for main application loop supporting
 * Genetic Alorithms (GA), Simmulated Annealing (SA), and 
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
            runConfiguration(args[1]);
        }
        else if (args[0].equals("-search_best_configuration")){
            searchBestConfiguration(args[1]);
        }
        else{
            throw new RuntimeException("Invalid flag supplied as argument to application.");
        }
    }

    private static void searchBestConfiguration(String configurationType){
        ArrayList<Report> configurationReports = new ArrayList<>();
        if(configurationType.equals("ga")){
            for (int i = 0; i < PopulationConfiguration.NUM_CONFIGURATIONS; i++){
                String fileNumber = (i + 1) + "";
                if(i < 9)
                    fileNumber = "0" + fileNumber;
                configurationReports.add(runConfiguration(configurationType + "_default_" + fileNumber + ".json"));
            }
        }
        else if(configurationType.equals("sa")){
            ;
        }
        else if(configurationType.equals("pso")){
            for (int i = 0; i < SwarmConfiguration.NUM_CONFIGURATIONS; i++){
                String fileNumber = (i + 1) + "";
                if(i < 9)
                    fileNumber = "0" + fileNumber;
                configurationReports.add(runConfiguration(configurationType + "_default_" + fileNumber + ".json"));
            }
        }
        else{
            throw new RuntimeException("Invalid configuration type supplied as argument to application.");
        }
        Collections.sort(configurationReports);
        configurationReports.get(0).saveJson("data/results/best_configurations/", configurationType);
    }

    private static Report runConfiguration(String fileName){
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year  = localDate.getYear();
        int month = localDate.getMonthValue();
        int day   = localDate.getDayOfMonth();
        Report report = null;

        if(fileName.matches("^ga.*")){
            report = runGAConfiguration(fileName);
            report.save("data/results/ga/report_"+ fileName.substring(0, fileName.length() - 5) + "_" + year + month + day + ".txt");
        }
        else if(fileName.matches("^pso.*")){
            report = runPSOConfiguration(fileName);
            report.save("data/results/pso/report_"+ fileName.substring(0, fileName.length() - 5) + "_" + year + month + day + ".txt");
        }
        else if(fileName.matches("^sa.*")){
            report = runSAConfiguration(fileName);
            report.save("data/results/sa/report_"+ fileName.substring(0, fileName.length() - 5) + "_" + year + month + day + ".txt");
        }
        else{
            throw new RuntimeException("Invalid configuration file name supplied.");
        }
        return report;
    }

    /**
     * Run a Genetic Algorithm (GA) Simulation for the knapsack problem.
     * @param fileName - Name of file to be accessed for config.
     * @return Nothing.
     */ 
    private static Report runGAConfiguration(String fileName) {
        PopulationConfiguration config = new PopulationConfiguration(fileName);
        Population population = new Population(config);
        Report report = new Report(fileName, config);
        long startTime = System.currentTimeMillis();

        for(int i = 0; i < Configuration.MAX_ITERATIONS; i++){
            Knapsack fittestKnapsack = population.evolve();
            report.addIteration(fittestKnapsack);
            if(i % 100 == 0)
                System.out.println(population.getSummaryStats());
        }
        long completeTime = System.currentTimeMillis() - startTime;
        report.setCompleteTime(completeTime);
        return report;
    }

    private static Report runSAConfiguration(String fileName){
        SimulatedAnnealingConfiguration config = new SimulatedAnnealingConfiguration(fileName);
        SimulatedAnnealing sa = new SimulatedAnnealing(config);
        Report report = new Report(fileName, config);
        long startTime = System.currentTimeMillis();

        for(int i = 0; i < Configuration.MAX_ITERATIONS; i++){
            Knapsack fittestKnapsack = sa.execute();
            report.addIteration(fittestKnapsack);
        }
        long completeTime = System.currentTimeMillis() - startTime;
        report.setCompleteTime(completeTime);
        return report;
    }

    private static Report runPSOConfiguration(String fileName){
        SwarmConfiguration config = new SwarmConfiguration(fileName);
        Swarm swarm = new Swarm(config);
        Report report = new Report(fileName, config);
        long startTime = System.currentTimeMillis();

        for(int i = 0; i < Configuration.MAX_ITERATIONS; i++){
            Knapsack fittestKnapsack = swarm.execute();
            report.addIteration(fittestKnapsack);
            if(i % 100 == 0)
                System.out.println(swarm.getSummaryStats());
        }
        long completeTime = System.currentTimeMillis() - startTime;
        report.setCompleteTime(completeTime);
        return report;
    }
}