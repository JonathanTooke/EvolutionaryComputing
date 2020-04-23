/**
 * Class used to store and access an individual KnapsackItem.
 * Representation of a gene in GA terms.
 */
public class KnapsackItem implements Cloneable, Comparable<KnapsackItem>{
    private int number;
    private int weight;
    private int value;

    /**
     * Constructor
     * @param number - knapsack item number
     * @param weight - weight of item
     * @param value - value of item
     */
    public KnapsackItem(int number, int weight, int value) {
        this.number = number;
        this.weight = weight;
        this.value = value;
    }

    /**
     * Constructor. Build a knapsack item given its number.
     * @param number - knapsack item number
     */
    public KnapsackItem(int number){
        this(number, 
            Configuration.KNAPSACK_ITEM_SELECTION.get(number - 1).getWeight(),
            Configuration.KNAPSACK_ITEM_SELECTION.get(number - 1).getValue()
        );
    }

    /**
     * Deep copy KnapsackItem
     * @return - KnapsackItem
     */
    @Override
    public KnapsackItem clone() {
        return new KnapsackItem(this.number, this.weight, this.value);
    }
    
    @Override
    public int compareTo(KnapsackItem other){
        return Integer.compare(other.number, this.number);
    }

    /**
     * Check to see whether one KnapsackItem has the same
     * number (key) as another KnapsackItem for equality.
     * @return - boolean
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null)
            return false;

        if (getClass() != other.getClass())
            return false;

        KnapsackItem otherItem = (KnapsackItem) other;
        return this.number == otherItem.getNumber();
    } 

    @Override
    public int hashCode() {
        return number;
    }

    /**
     * Getter
     * @return number
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * Getter
     * @return weight
     */
    public int getWeight() {
        return this.weight;
    }

    /**
     * Getter
     * @return value
     */
    public int getValue() {
        return this.value;
    }
} 