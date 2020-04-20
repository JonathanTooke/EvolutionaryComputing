import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used to represent store a knapsack for a given selection.
 * Representation of a chromosome in GA terms. 
 */
public class Knapsack{
    private List<KnapsackItem> knapsackItems;
    private int fitness;
    private double rwsValue;
    GAConfiguration config;

    public Knapsack(GAConfiguration config) {
        this.config = config;
    }

    public List<KnapsackItem> generateRandom(){
        int weight = 0;
        List<KnapsackItem> itemsSelected = new ArrayList<>();
        List<KnapsackItem> baseKnapsackItems = Configuration.KNAPSACK_ITEM_SELECTION.stream()
                                                .map(i -> i.clone())
                                                .collect(Collectors.toList());
        while(weight < Configuration.MAX_CAPACITY){
            KnapsackItem nextItem = baseKnapsackItems.get(Configuration.RANDOM_GENERATOR.nextInt(baseKnapsackItems.size()));
            if(weight + nextItem.getWeight() > Configuration.MAX_CAPACITY){
                break;
            }
            baseKnapsackItems.remove(nextItem);
            itemsSelected.add(nextItem);
            weight += nextItem.getWeight();
        }
        return itemsSelected;
    }

    /** 
     * Fitness function for determining the fitness of this knapsack.
     * @return
     */
    public int calculateFitness(){
        int weight = 0, value = 0;
        for (var knapsackItem : knapsackItems){
            weight += knapsackItem.getWeight();
            value += knapsackItem.getValue();
        }
        if(weight > Configuration.MAX_CAPACITY){
            return Integer.MAX_VALUE;
        }
        return value;
    }

    public List<KnapsackItem> getKnapsackItems() {
        return this.knapsackItems;
    }

    public Knapsack withRandomKnapsackItems() {
        this.knapsackItems = generateRandom();
        return this;
    }

    public Knapsack withFitnessCalculated() {
        this.fitness = calculateFitness();
        return this;
    }

    public double getRwsValue() {
        return this.rwsValue;
    }

    public void setRwsValue(double rwsValue) {
        this.rwsValue = rwsValue;
    }

    public int getFitness(){
        return this.fitness;
    }

    // @Override
    // public int compareTo(Knapsack other){
    //     return Double.compare(this.rwsValue, other.getRwsValue());
    // }
}