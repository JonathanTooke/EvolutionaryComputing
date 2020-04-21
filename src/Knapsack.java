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
    
    /**
     * Default constructor
     */
    public Knapsack() {}

    /**
     * Constructor with KnapsackItems supplied
     * @param knapsackItems
     */
    public Knapsack(List<KnapsackItem> knapsackItems) {
        this.knapsackItems = knapsackItems;
    }

    /**
     * Generate a random list of Knapsack Items within the maximum capacity.
     * @return List<KnapsackItem> - list of KnapsackItems
     */
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
            return 50;
        }
        return value;
    }

    /**
     * One and Two Point Crossover Operations
     * @param other - the other Knapsack to perform the crossover with.
     * @param crossoverType - Either 1PX or 2PX i.e. 1 or 2 point.
     * @return
     */
    public List<Knapsack> doCrossover(Knapsack other, String crossoverType){

        List<Knapsack> children = new ArrayList<>();
        int minLengthSack = Math.min(this.knapsackItems.size(), other.getKnapsackItems().size());

        //Allow for multiple crossover attempts to better the chance of a valid crossover.
        //Note that this can be disabled by setting GAConfiguration.CONCEPTION_ATTEMPTS = 1.
        for(int i = 0; i < GAConfiguration.CONCEPTION_ATTEMPTS; i++){
            int crossPoint1 = crossoverType.equals("1PX") ? 0 : Configuration.RANDOM_GENERATOR.nextInt(minLengthSack);
            int crossPoint2 = Configuration.RANDOM_GENERATOR.nextInt(minLengthSack) - crossPoint1;

            List<KnapsackItem> c1 = new ArrayList<>();
            List<KnapsackItem> c2 = new ArrayList<>();

            c1.addAll(this.knapsackItems.subList(0, crossPoint1));
            c1.addAll(other.getKnapsackItems().subList(crossPoint1, crossPoint2));
            c1.addAll(this.knapsackItems.subList(crossPoint2, this.knapsackItems.size()));
            Knapsack child1 = new Knapsack(c1);

            c2.addAll(other.getKnapsackItems().subList(0, crossPoint1));
            c2.addAll(this.knapsackItems.subList(crossPoint1, crossPoint2));
            c2.addAll(other.getKnapsackItems().subList(crossPoint2, other.getKnapsackItems().size()));
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
        //If the crossover failed to generate a valid child, return the parents.
        if(children.size() == 0){
            children.add(other);
            children.add(this);
        }
        else if(children.size() == 1){
            children.add(this);
        }
        return children;
    }
    
    public void bitFlipMutation(){
        for(int i = 0; i < GAConfiguration.MUTATION_ATTEMPTS; i++){
            int itemToMutate = Configuration.RANDOM_GENERATOR.nextInt(Configuration.KNAPSACK_ITEM_SELECTION.size());
            boolean found = false;
            for(var item : this.knapsackItems){
                if (item.getNumber() == itemToMutate){
                    this.knapsackItems.remove(item);
                    found = true;
                    break;
                }
            }
            int sumWeight = this.calculateFitness();
            if(!found && Configuration.KNAPSACK_ITEM_SELECTION.get(itemToMutate).getWeight() + sumWeight < Configuration.MAX_CAPACITY){
                this.knapsackItems.add(Configuration.KNAPSACK_ITEM_SELECTION.get(itemToMutate));
                break;
            }
        }
    }



    public boolean isValid(){
        return calculateFitness() > 50;
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

    public void setFitness(int fitness){
        this.fitness = fitness;
    }

    @Override
    public int compareTo(Knapsack other){
        return Integer.compare(other.getFitness(), this.fitness);
    }
}