import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used to represent store a knapsack for a given selection.
 * Representation of a chromosome in GA terms. 
 */
public class Knapsack implements Comparable<Knapsack>{
    private ArrayList<Boolean> knapsackSelection;
    private int fitness;
    private double rwsValue;
    
    /**
     * Default constructor.
     */
    public Knapsack() {}


    /**
     * Copy constructor.
     * @param knapsack
     */
    public Knapsack(Knapsack knapsack){
        this.knapsackSelection = new ArrayList<Boolean>();
        for(var item : knapsack.getKnapsackSelection()){
            this.knapsackSelection.add(item);
        }
    }

    /**
     * Constructor.
     * @param knapsackSelection
     */
    public Knapsack(ArrayList<Boolean> knapsackSelection) {
        this.knapsackSelection = knapsackSelection;
    }

    public Knapsack withRandomKnapsackItems() {
        this.knapsackSelection = generateRandomItems();
        return this;
    }

    public Knapsack withFitnessCalculated() {
        this.fitness = calculateFitness();
        return this;
    }

    /** 
     * Fitness function for determining the fitness of this knapsack.
     * @return 1 for an invalid knapsack, sum of values of individual items selected for a valid knapsack.
     */
    public int calculateFitness(){
        if(calculateWeight() > Configuration.MAX_CAPACITY)
            return 1;
        return mapFromBinaryRepresentation(this.knapsackSelection).stream().mapToInt(k -> k.getValue()).sum();
    }

    /**
     * Determine the weight of all of the selected items in the knapsack.
     * @return sum of knapsack item weight in knapsack.
     */
    private int calculateWeight(){
        return mapFromBinaryRepresentation(this.knapsackSelection).stream().mapToInt(k -> k.getWeight()).sum();
    }

    public boolean isValid(){
        return this.fitness > 1 && this.knapsackSelection.size() == Configuration.NUM_ITEMS;
    }

    ///////////////////////////
    //// Genetic Operators ////
    ///////////////////////////
    
    /**
     * One and Two Point Crossover Operations
     * @param other - the other Knapsack to perform the crossover with.
     * @param crossoverType - Either 1PX or 2PX i.e. 1 or 2 point.
     * @return
     */
    public List<Knapsack> doCrossover(Knapsack other, String crossoverType){
        int geneSize = this.knapsackSelection.size();

        ArrayList<Knapsack> children = new ArrayList<>();

        //Allow for multiple crossover attempts to better the chance of a valid crossover.
        //Note that this can be disabled by setting GAConfiguration.CONCEPTION_ATTEMPTS = 1.
        for(int i = 0; i < GAConfiguration.CONCEPTION_ATTEMPTS; i++){

            //Set first crossover point to 0 if 1PX Crossover.
            int crossPoint1 = crossoverType.equals("1PX") ? 0 : Configuration.RANDOM_GENERATOR.nextInt(geneSize);
            int crossPoint2 = Configuration.RANDOM_GENERATOR.nextInt(geneSize - crossPoint1) + crossPoint1;

            ArrayList<Boolean> c1 = new ArrayList<>();
            ArrayList<Boolean> c2 = new ArrayList<>();

            c1.addAll(this.knapsackSelection.subList(0, crossPoint1));
            c1.addAll(other.getKnapsackSelection().subList(crossPoint1, crossPoint2));
            c1.addAll(this.knapsackSelection.subList(crossPoint2, geneSize));
            Knapsack child1 = new Knapsack(c1).withFitnessCalculated();

            c2.addAll(other.getKnapsackSelection().subList(0, crossPoint1));
            c2.addAll(this.knapsackSelection.subList(crossPoint1, crossPoint2));
            c2.addAll(other.getKnapsackSelection().subList(crossPoint2, geneSize));
            Knapsack child2 = new Knapsack(c2).withFitnessCalculated();
            
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
        for(int i = 0; i < GAConfiguration.MUTATION_ATTEMPTS; i++){
            ArrayList<Boolean> newSelection = copyBoolArrayList(this.knapsackSelection);
            int itemToMutate = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            newSelection.set(itemToMutate, !newSelection.get(itemToMutate));

            Knapsack mutatedKnapsack = new Knapsack(newSelection).withFitnessCalculated();
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
            ArrayList<Boolean> newSelection = copyBoolArrayList(this.knapsackSelection);

            int allele1 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            int allele2 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            Collections.swap(newSelection, allele1, allele2);

            Knapsack mutatedKnapsack = new Knapsack(newSelection).withFitnessCalculated();
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
            ArrayList<Boolean> newSelection = copyBoolArrayList(this.knapsackSelection);

            int allele1 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            int allele2 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            Collections.reverse(newSelection.subList(Math.min(allele1, allele2), Math.max(allele1, allele2)));

            Knapsack mutatedKnapsack = new Knapsack(newSelection).withFitnessCalculated();
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
        for(int i = 0; i < GAConfiguration.MUTATION_ATTEMPTS; i++){
            ArrayList<Boolean> newSelection = copyBoolArrayList(this.knapsackSelection);

            int allele1 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            int allele2 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            boolean value = newSelection.get(allele2);

            newSelection.remove(allele2);
            newSelection.add(allele1 + 1, value);

            Knapsack mutatedKnapsack = new Knapsack(newSelection).withFitnessCalculated();
            if(mutatedKnapsack.isValid())
                return mutatedKnapsack;
        }
        return this;
    }

    /**
     * Implementation of Displacement Mutation. 
     * @return Knapsack - mutated child.
     */
    public Knapsack doDisplacementMutation(){
        for(int i = 0; i < GAConfiguration.MUTATION_ATTEMPTS; i++){
            ArrayList<Boolean> newSelection = copyBoolArrayList(this.knapsackSelection);

            int allele1 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            int allele2 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());

            int leftAllele = Math.min(allele1, allele2);
            int rightAllele = Math.max(allele1, allele2);

            var selectionSublist = new ArrayList<Boolean>(newSelection.subList(leftAllele, rightAllele));
            for(int j = leftAllele; j < rightAllele + 1; j++){
                newSelection.remove(leftAllele);
            }

            int index = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size()+1);
            newSelection.addAll(index, selectionSublist);

            Knapsack mutatedKnapsack = new Knapsack(newSelection).withFitnessCalculated();
            if(mutatedKnapsack.isValid())
                return mutatedKnapsack;
        }
        return this;
    }

    ////////////////////////
    //// Helper Methods ////
    ////////////////////////
    
    /**
     * Generate a random knapsackSelection within the maximum knapsack capacity.
     * @return List<Boolean> - binary list representing knapsacks selected.
     */
    private ArrayList<Boolean> generateRandomItems(){

        //Generate binary representation
        ArrayList<Boolean> itemsSelected = generateFalseList(Configuration.NUM_ITEMS);

        //Clone KnapsackItems for selection
        List<KnapsackItem> baseKnapsackItems = Configuration.KNAPSACK_ITEM_SELECTION.stream()
            .map(i -> i.clone())
            .collect(Collectors.toList());

        //Iteratively add items to the knapsack
        int weight = 0;
        while(weight < Configuration.MAX_CAPACITY){
            KnapsackItem nextItem = baseKnapsackItems.get(Configuration.RANDOM_GENERATOR.nextInt(baseKnapsackItems.size()));
            weight += nextItem.getWeight();

            if(weight > Configuration.MAX_CAPACITY)
                break;
            
            //Add new item, Note -1 for 0 indexing
            itemsSelected.set(nextItem.getNumber() - 1, true);
            baseKnapsackItems.remove(nextItem);
            
            //Give some chance of not filling the sack to its full capacity to increase genetic diversity.
            double probabilityOfExit = Configuration.RANDOM_GENERATOR.nextDouble();
            if(probabilityOfExit < 0.01)
                break;
        }
        return itemsSelected;
    }

    /**
     * Populate an Arraylist with False items
     * @param length - number of items to populate.
     */
    private ArrayList<Boolean> generateFalseList(int length){
        ArrayList<Boolean> elements = new ArrayList<>();
        for(int i = 0; i < length; i++){
            elements.add(false);
        }
        return elements;
    }

    /**
     * Deep Copy ArrayList
     * @return - ArrayList<Boolean> 
     */
    private ArrayList<Boolean> copyBoolArrayList(ArrayList<Boolean> array){
        ArrayList<Boolean> boolArray = new ArrayList<>();
        for(var item : array){
            boolArray.add(item);
        }
        return boolArray;
    }

    /**
     * Map from Binary representation to List<KnapsackItem> representation.
     * @param binaryKnapsackItems
     * @return
     */
    private List<KnapsackItem> mapFromBinaryRepresentation(ArrayList<Boolean> binaryKnapsackItems){
        assert(binaryKnapsackItems.size() == Configuration.NUM_ITEMS);
        ArrayList<KnapsackItem> knapsackItems = new ArrayList<>();
        for(int i = 0; i < binaryKnapsackItems.size(); i++){
            if(binaryKnapsackItems.get(i)){
                knapsackItems.add(new KnapsackItem(i + 1));
            }
        }
        return knapsackItems;
    }

    /**
     * Compare by fitness value where larger fitness > smaller fitness.
     * @return
     */
    @Override
    public int compareTo(Knapsack other){
        return Integer.compare(other.getFitness(), this.fitness);
    }

    @Override
    public String toString(){
        StringBuilder sack = new StringBuilder();
        sack.append(calculateWeight() + " ".repeat(5) + calculateFitness() + " ".repeat(5) + (double)calculateFitness()/Configuration.BEST_KNOWN_OPTIMUM*100 + "%" + "[");
        for(boolean present : this.knapsackSelection){
            if(present)
                sack.append(1);
            else
                sack.append(0);
        }
        sack.append("]");
        return new String(sack);
    }

    /////////////////////////////
    //// Getters and Setters ////
    /////////////////////////////
    
    public ArrayList<Boolean> getKnapsackSelection() {
        return this.knapsackSelection;
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
}