import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Config specific to Particle Swarm Optimization (PSO).
 */
public class SwarmConfiguration extends Configuration {
    public static final String PSO_PATH = "data/configuration/pso/";
    public static final int NUM_CONFIGURATIONS = 25; 
    private int minimumVelocity;
    private int maximumVelocity;
    private double inertia;
    private String configuration;
    private int numParticles;
    private double c1;
    private double c2;

    /**
     * Constructor
     * @param fileName - containing config data
     */
    public SwarmConfiguration(String fileName){
        super();
        loadConfig(fileName);
    }

    public int getNumConfigurations(){
        return 0;
    }

    public String toString(){
        return "PSO" + " | #" + Configuration.MAX_ITERATIONS + " | ";
    }

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

            this.minimumVelocity = Integer.parseInt(list.get(0).get(1));
            this.maximumVelocity = Integer.parseInt(list.get(1).get(1));
            this.inertia = Double.parseDouble(list.get(2).get(1));
            this.configuration = list.get(3).get(1);
            this.numParticles = Integer.parseInt(list.get(4).get(1));
            this.c1 = Double.parseDouble(list.get(5).get(1));
            this.c2 = Double.parseDouble(list.get(6).get(1));
            sc.close();
        } 
        catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}