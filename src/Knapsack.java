import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used to represent a knapsack with a given selection of Knapsack Itenms.
 */
public class Knapsack implements Comparable<Knapsack>{
    protected ArrayList<Boolean> knapsackSelection;
    protected int fitness;

    /**
     * Default constructor.
     */
    public Knapsack(){}

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
    protected int calculateWeight(){
        return mapFromBinaryRepresentation(this.knapsackSelection).stream().mapToInt(k -> k.getWeight()).sum();
    }

    public boolean isValid(){
        return this.fitness > 1 && this.knapsackSelection.size() == Configuration.NUM_ITEMS;
    }

    ////////////////////////
    //// Helper Methods ////
    ////////////////////////
    
    /**
     * Generate a random knapsackSelection within the maximum knapsack capacity.
     * @return List<Boolean> - binary list representing knapsacks selected.
     */
    protected ArrayList<Boolean> generateRandomItems(){

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
    protected ArrayList<Boolean> generateFalseList(int length){
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
    protected ArrayList<Boolean> copyBoolArrayList(ArrayList<Boolean> array){
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
    protected List<KnapsackItem> mapFromBinaryRepresentation(ArrayList<Boolean> binaryKnapsackItems){
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

    /**
     * String representation of Knapsack customized for generatin a Report.
     * @param includeArray
     * @return
     */
    public String toReportString(boolean includeArray){
        StringBuilder sack = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.##"); 
        String sQuality = df.format((double)calculateFitness()/Configuration.BEST_KNOWN_OPTIMUM*100) + "%";

        sack.append(String.format("%0$-10s", calculateWeight()) + String.format("%0$-10s", calculateFitness()) + String.format("%0$-8s", sQuality));
        if(includeArray){
            sack.append("        [");
            for(boolean present : this.knapsackSelection){
                if(present)
                    sack.append(1);
                else
                    sack.append(0);
            }
            sack.append("]");
        }
        return new String(sack);
    }

    /////////////////////////////
    //// Getters and Setters ////
    /////////////////////////////
    
    public ArrayList<Boolean> getKnapsackSelection() {
        return this.knapsackSelection;
    }

    public int getFitness(){
        return this.fitness;
    }

    public void setFitness(int fitness){
        this.fitness = fitness;
    }
}