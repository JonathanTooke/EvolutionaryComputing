import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * General configuration applicable to all algorithms.
 */
public abstract class Configuration {
    public static final int MAX_CAPACITY = 822;
    public static final String KNAPSACK_PATH = "data/knapsack/knapsack_instance.csv";
    public static final int MAX_ITERATIONS = 1000;
    public static final int BEST_KNOWN_OPTIMUM = 997;
    public static final List<KnapsackItem> KNAPSACK_ITEM_SELECTION = loadKnapsackItemSelection(Configuration.KNAPSACK_PATH);
    public static final MersenneTwister RANDOM_GENERATOR = new MersenneTwister(System.currentTimeMillis());

    /**
     * Load the full knapsack dataset from a csv file. 
     * Data provided in format: #;weight;value
     * @param fileName - Name of file storing the knapsack data.
     * @return List<KnapsackItem> storing the list of KnapsackItems.
     */
    private static List<KnapsackItem> loadKnapsackItemSelection(String fileName){
        List<KnapsackItem> items = new ArrayList<>();
        File file = new File(fileName);
        try {
            Scanner sc = new Scanner(file); 
            sc.nextLine();
            while (sc.hasNextLine()){
                List<Integer> data = Arrays.asList(sc.nextLine().split(";")).stream()
                                                                    .map(s -> Integer.valueOf(s))
                                                                    .collect(Collectors.toList());
                KnapsackItem item = new KnapsackItem(data.get(0), data.get(1), data.get(2)); 
                items.add(item);
            }
            sc.close();
        } 
        catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return items;
    }

    /**
     * Implemented by each algorithm's config child to load 
     * the relevent JSON config.
     * @param fileName
     */
    protected abstract void loadConfig(String fileName);
}
