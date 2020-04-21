import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Class used to represent store a knapsack for a given selection.
 * Representation of a chromosome in GA terms. 
 */
public class Knapsack implements Comparable<Knapsack>{
    private List<KnapsackItem> knapsackItems;
    private int fitness;
    private double rwsValue;

    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
    
    public Knapsack() {}

    public Knapsack(List<KnapsackItem> knapsackItems) {
        this.knapsackItems = knapsackItems;
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

    public List<Knapsack> doCrossover(Knapsack other, String crossoverType){

        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory is bytes: " + memory);
        System.out.println("Used memory is megabytes: "
                + bytesToMegabytes(memory));
        List<Knapsack> children = new ArrayList<>();

        int minLengthSack = Math.min(this.knapsackItems.size(), other.getKnapsackItems().size());
        for (int attempt = 0; attempt < GAConfiguration.CONCEPTION_ATTEMPTS; attempt++){

            int crossPoint1 = crossoverType.equals("1PX") ? 0 : Configuration.RANDOM_GENERATOR.nextInt(minLengthSack);
            int crossPoint2 = Configuration.RANDOM_GENERATOR.nextInt(minLengthSack) - crossPoint1;

            List<KnapsackItem> c1, c2 = new ArrayList<>();

            c1 = this.knapsackItems.subList(0, crossPoint1);
            c1.addAll(other.getKnapsackItems().subList(crossPoint1, crossPoint2));
            c1.addAll(this.knapsackItems.subList(crossPoint2, this.knapsackItems.size()));

            c2 = other.getKnapsackItems().subList(0, crossPoint1);
            c2.addAll(this.knapsackItems.subList(crossPoint1, crossPoint2));
            c2.addAll(other.getKnapsackItems().subList(crossPoint2, other.getKnapsackItems().size()));

            Knapsack child1 = new Knapsack(c1);
            Knapsack child2 = new Knapsack(c2);
            
            if(child1.isValid() && children.size() < 2){
                children.add(child1);
            }
            if(child2.isValid() && children.size() < 2){
                children.add(child2);
            }
            if(children.size() == 2){
                break;
            }
        }

        if(children.size() == 0){
            children.add(other);
            children.add(this);
        }
        else if(children.size() == 1){
            children.add(this);
        }
        return children;
    }
    
    // Should maybe reject invalid mutation?
    public void bitFlipMutation(){
        int itemToMutate = Configuration.RANDOM_GENERATOR.nextInt(Configuration.KNAPSACK_ITEM_SELECTION.size());
        boolean found = false;
        for(var item : this.knapsackItems){
            if (item.getNumber() == itemToMutate){
                this.knapsackItems.remove(item);
                found = true;
                break;
            }
        }
        if(!found){
            this.knapsackItems.add(Configuration.KNAPSACK_ITEM_SELECTION.get(itemToMutate));
        }
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
            return 1;
        }
        return value;
    }

    public boolean isValid(){
        return calculateFitness() > 1;
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

    @Override
    public int compareTo(Knapsack other){
        return Integer.compare(this.fitness, other.getFitness());
    }
}