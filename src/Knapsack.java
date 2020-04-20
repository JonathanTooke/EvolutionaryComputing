import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used to represent load and store the list of possible KnapsackItems.
 */
public class Knapsack {
    private KnapsackItem[] knapsackItems;

    /**
     * Constructor
     * @param fileName - directory for knapsack data.
     */
    public Knapsack(String fileName) {
        this.knapsackItems = loadKnapsack(fileName);
    }

    /**
     * Load the knapsack data from a csv file. 
     * Data provided in format: #;weight;value
     * @param fileName - Name of file storing the knapsack data.
     * @return KnapsackItem[] storing the list of knapsack items.
     */
    private KnapsackItem[] loadKnapsack(String fileName){
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

        return items.stream().toArray(i -> new KnapsackItem[i]);
    }

    /**
     * Getter
     * @return KnapsackItem[]
     */
    public KnapsackItem[] getKnapsackItems() {
        return this.knapsackItems;
    }
}