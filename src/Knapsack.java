import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used to represent store a knapsack for a given selection.
 * Representation of a chromosome in GA terms. 
 */
public class Knapsack implements Comparable<Knapsack>{
    private ArrayList<KnapsackItem> knapsackItems;
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
    public Knapsack(ArrayList<KnapsackItem> knapsackItems) {
        this.knapsackItems = knapsackItems;
    }

    /**
     * Generate a random list of Knapsack Items within the maximum capacity.
     * @return List<KnapsackItem> - list of KnapsackItems
     */
    public ArrayList<KnapsackItem> generateRandom(){
        int weight = 0;
        ArrayList<KnapsackItem> itemsSelected = new ArrayList<>();
        List<KnapsackItem> baseKnapsackItems = Configuration.KNAPSACK_ITEM_SELECTION.stream()
            .map(i -> i.clone())
            .collect(Collectors.toList());
        while(weight < Configuration.MAX_CAPACITY ){
            KnapsackItem nextItem = baseKnapsackItems.get(Configuration.RANDOM_GENERATOR.nextInt(baseKnapsackItems.size()));
            if(weight + nextItem.getWeight() > Configuration.MAX_CAPACITY){
                break;
            }
            baseKnapsackItems.remove(nextItem);
            itemsSelected.add(nextItem);
            weight += nextItem.getWeight();
            //Give some chance of not fulling the sack to its full capacity.
            double probabilityOfExit = Configuration.RANDOM_GENERATOR.nextDouble();
            if(probabilityOfExit < 0.01){
                break;
            }
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
            return 1;
        }
        return value;
    }

    public int calculateWeight(){
        return this.knapsackItems.stream().mapToInt(k -> k.getWeight()).sum();
    }

    /**
     * One and Two Point Crossover Operations
     * @param other - the other Knapsack to perform the crossover with.
     * @param crossoverType - Either 1PX or 2PX i.e. 1 or 2 point.
     * @return
     */
    public ArrayList<Knapsack> doCrossover(Knapsack other, String crossoverType){
        ArrayList<Boolean> binaryKnapsackItems = mapToBinaryRepresentation(this.knapsackItems);
        ArrayList<Boolean> otherBinaryKnapsackItems = mapToBinaryRepresentation(other.getKnapsackItems());
        assert(binaryKnapsackItems.size() == otherBinaryKnapsackItems.size());
        int geneSize = binaryKnapsackItems.size();

        ArrayList<Knapsack> children = new ArrayList<>();

        //Allow for multiple crossover attempts to better the chance of a valid crossover.
        //Note that this can be disabled by setting GAConfiguration.CONCEPTION_ATTEMPTS = 1.
        for(int i = 0; i < GAConfiguration.CONCEPTION_ATTEMPTS; i++){

            int crossPoint1 = crossoverType.equals("1PX") ? 0 : Configuration.RANDOM_GENERATOR.nextInt(geneSize);
            int crossPoint2 = Configuration.RANDOM_GENERATOR.nextInt(geneSize - crossPoint1) + crossPoint1;

            ArrayList<Boolean> c1 = new ArrayList<>();
            ArrayList<Boolean> c2 = new ArrayList<>();

            c1.addAll(binaryKnapsackItems.subList(0, crossPoint1));
            c1.addAll(otherBinaryKnapsackItems.subList(crossPoint1, crossPoint2));
            c1.addAll(binaryKnapsackItems.subList(crossPoint2, geneSize));
            Knapsack child1 = new Knapsack(mapFromBinaryRepresentation(c1));

            c2.addAll(otherBinaryKnapsackItems.subList(0, crossPoint1));
            c2.addAll(binaryKnapsackItems.subList(crossPoint1, crossPoint2));
            c2.addAll(otherBinaryKnapsackItems.subList(crossPoint2, geneSize));
            Knapsack child2 = new Knapsack(mapFromBinaryRepresentation(c2));
            
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
    
    /**
     * Implementation of Bit Flip Mutation.
     * Note that the mutation will be attempted GAConfiguration.MUTATION_ATTEMPTS times.
     * If it is unsuccesful in producing a valid child on every attempt, it will return the original child.
     * @return Knapsack - mutated child.
     */
    public Knapsack doBitFlipMutation(){
        ArrayList<Boolean> binaryKnapsackItems = mapToBinaryRepresentation(this.knapsackItems);
        for(int i = 0; i < GAConfiguration.MUTATION_ATTEMPTS; i++){
            int itemToMutate = Configuration.RANDOM_GENERATOR.nextInt(binaryKnapsackItems.size());
            binaryKnapsackItems.set(itemToMutate, !binaryKnapsackItems.get(itemToMutate));

            Knapsack mutatedKnapsack = new Knapsack(mapFromBinaryRepresentation(binaryKnapsackItems));
            if(mutatedKnapsack.isValid())
                return mutatedKnapsack;
        }
        return this;
    }

    /**
     * Implementation of Exchange Mutation. 
     * @return Knapsack - mutated child.
     */
    public Knapsack doExchangeMutation(){
        for(int i = 0; i < GAConfiguration.MUTATION_ATTEMPTS; i++){
            ArrayList<Boolean> binaryKnapsackItems = mapToBinaryRepresentation(this.knapsackItems);

            int allele1 = Configuration.RANDOM_GENERATOR.nextInt(binaryKnapsackItems.size());
            int allele2 = Configuration.RANDOM_GENERATOR.nextInt(binaryKnapsackItems.size());
            Collections.swap(binaryKnapsackItems, allele1, allele2);

            Knapsack mutatedKnapsack = new Knapsack(mapFromBinaryRepresentation(binaryKnapsackItems));
            if(mutatedKnapsack.isValid())
                return mutatedKnapsack;
        }
        return this;
    }

    /**
     * Implementation of Inversion Mutation. 
     * @return Knapsack - mutated child.
     */
    public Knapsack doInversionMutation(){
        for(int i = 0; i < GAConfiguration.MUTATION_ATTEMPTS; i++){
            ArrayList<Boolean> binaryKnapsackItems = mapToBinaryRepresentation(this.knapsackItems);

            int allele1 = Configuration.RANDOM_GENERATOR.nextInt(binaryKnapsackItems.size());
            int allele2 = Configuration.RANDOM_GENERATOR.nextInt(binaryKnapsackItems.size());
            Collections.reverse(binaryKnapsackItems.subList(Math.min(allele1, allele2), Math.max(allele1, allele2)));

            Knapsack mutatedKnapsack = new Knapsack(mapFromBinaryRepresentation(binaryKnapsackItems));
            if(mutatedKnapsack.isValid())
                return mutatedKnapsack;
        }
        return this;
    }

    /**
     * Implementation of Insertion Mutation. 
     * @return Knapsack - mutated child.
     */
    public Knapsack doInsertionMutation(){
        // int allele1 = Configuration.RANDOM_GENERATOR.nextInt(this.knapsackItems.size());
        // int allele2 = Configuration.RANDOM_GENERATOR.nextInt(this.knapsackItems.size());
        // int newIndex = Configuration.RANDOM_GENERATOR.nextInt(this.knapsackItems.size());
        return this;
    }

    /**
     * Implementation of Displacement Mutation. 
     * @return Knapsack - mutated child.
     */
    public Knapsack doDisplacementMutation(){
        return this;
    }

    public boolean isValid(){
        return calculateFitness() != 1 
            && this.knapsackItems.stream().distinct().collect(Collectors.toList()).size() == this.knapsackItems.size();
    }

    public ArrayList<KnapsackItem> getKnapsackItems() {
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
        return Integer.compare(this.fitness, other.getFitness());
    }

    private ArrayList<Boolean> mapToBinaryRepresentation(ArrayList<KnapsackItem> knapsackItems){
        ArrayList<Boolean> binaryKnapsackItems = new ArrayList<>();
        for(int i = 0; i < Configuration.NUM_ITEMS; i++){
            binaryKnapsackItems.add(false);
        }

        List<Integer> numbers = knapsackItems.stream()
            .map(sack -> sack.getNumber())
            .collect(Collectors.toList());

        for(int number : numbers){
            binaryKnapsackItems.set((number - 1), true);
        }
        assert(binaryKnapsackItems.size() == Configuration.NUM_ITEMS);
        assert(new Knapsack(mapFromBinaryRepresentation(binaryKnapsackItems)).isValid());
        return binaryKnapsackItems;
    }

    private ArrayList<KnapsackItem> mapFromBinaryRepresentation(ArrayList<Boolean> binaryKnapsackItems){
        assert(binaryKnapsackItems.size() == Configuration.NUM_ITEMS);
        ArrayList<KnapsackItem> knapsackItems = new ArrayList<>();
        for(int i = 0; i < binaryKnapsackItems.size(); i++){
            if(binaryKnapsackItems.get(i)){
                knapsackItems.add(new KnapsackItem(i + 1));
            }
        }
        return knapsackItems;
    }
}