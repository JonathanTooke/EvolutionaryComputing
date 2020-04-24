import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Config specific to Simulated Annealing (SA).
 */
public class SimulatedAnnealingConfiguration extends Configuration {
    public static final String PSO_PATH = "data/configuration/sa/";
    public static final int NUM_CONFIGURATIONS = 25; 
    private double initialTemperature;
    private double coolingRate;

    /**
     * Constructor
     * @param fileName - containing config data
     */
    public SimulatedAnnealingConfiguration(String fileName){
        super();
        loadConfig(fileName);
    }

    /**
     * String representation of configuration for report.
     */
    public String toString(){
        return "SA" + " | #" + Configuration.MAX_ITERATIONS + " | " + "initial_temp: " + this.initialTemperature + " | " + "cooling_rate " + this.coolingRate;
    }

    /**
     * Parse the JSON file and load the necessary config data
     * @param fileName - containing config data
     */
    @Override
    protected void loadConfig(String fileName){
        fileName = PSO_PATH + fileName;
        File file = new File(fileName);
        try {
            Scanner sc = new Scanner(file); 
            String line = sc.nextLine();
            List<List<String>> list = Arrays.asList(line.split(","))
                .stream()
                .map(s -> Arrays.asList(s.split(":"))
                    .stream()
                    .map(n -> n.replaceAll("[^a-zA-Z0-9_.]", ""))
                    .collect(Collectors.toList())
                )
                .collect(Collectors.toList());

            this.initialTemperature = Double.parseDouble(list.get(0).get(1));
            this.coolingRate = Double.parseDouble(list.get(2).get(1));
            sc.close();
        } 
        catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public int getNumConfigurations(){
        return SimulatedAnnealingConfiguration.NUM_CONFIGURATIONS;
    }

    public double getInitialTemperature() {
        return this.initialTemperature;
    }

    public double getCoolingRate() {
        return this.coolingRate;
    }
    

}