import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;

/**
 * Config specific to Genetic Algorithms (GA).
 */
public class GAConfiguration extends Configuration {
    
    public static final int POPULATION_SIZE = 2048; //must be even
    public static final int TOURNAMENT_SIZE = 3;
    public static final int CONCEPTION_ATTEMPTS = 10;
    public static final int MUTATION_ATTEMPTS = 10;
    public static final double ELITISM_RATIO = 0.01;
    private String selectionMethod;
    private String configuration;
    private double mutationRatio;
    private double crossoverRatio;
    private String crossoverMethod;
    private String mutationMethod;

    /**
     * Constructor
     * @param fileName - containing config data
     */
    public GAConfiguration(String fileName){
        super();
        loadConfig(fileName);
    }

    /**
     * Parse the JSON file and load the necessary config data
     * @param fileName - containing config data
     */
    @Override
    protected void loadConfig (String fileName){
        fileName = "data/configuration/ga/" + fileName;
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

            this.selectionMethod = list.get(0).get(1);
            this.configuration = list.get(1).get(1);
            this.mutationRatio = Double.parseDouble(list.get(2).get(1));
            this.crossoverRatio = Double.parseDouble(list.get(3).get(1));
            this.crossoverMethod = list.get(4).get(1);
            this.mutationMethod = list.get(5).get(1);
            sc.close();
        } 
        catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String getSelectionMethod() {
        return this.selectionMethod;
    }

    public String getConfiguration() {
        return this.configuration;
    }

    public double getMutationRatio() {
        return this.mutationRatio;
    }

    public double getCrossoverRatio() {
        return this.crossoverRatio;
    }

    public String getCrossoverMethod() {
        return this.crossoverMethod;
    }

    public String getMutationMethod() {
        return this.mutationMethod;
    }
}