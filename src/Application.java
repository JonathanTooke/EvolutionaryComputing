import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Application {
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
            for (int i = 0; i < 1; i++){
                String fileNumber = (i + 1) + "";
                if(i < 10)
                    fileNumber = "0" + fileNumber;
                configurationReports.add(runConfiguration(configurationType + "_default_" + fileNumber + ".json"));
            }
        }
        else if(configurationType.equals("sa")){
            ;
        }
        else if(configurationType.equals("pso")){
            ;
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
        return report;
    }

    /**
     * Run a Genetic Algorithm (GA) Simulation for the knapsack problem.
     * @param fileName - Name of file to be accessed for config.
     * @return Nothing.
     */ 
    private static Report runGAConfiguration(String fileName) {
        GAConfiguration config = new GAConfiguration(fileName);
        Population population = new Population(config);
        Report report = new Report(fileName, config);
        long startTime = System.currentTimeMillis();

        for(int i = 0; i < Configuration.MAX_ITERATIONS; i++){
            report.addIteration(population.getFittestKnapsack());

            if(i % 100 == 0)
                System.out.println(population.getSummaryStats());

            population.evolve();
        }
        long completeTime = System.currentTimeMillis() - startTime;
        report.setCompleteTime(completeTime);
        return report;
    }
}