/**
 * Class used to store and access an individual KnapsackItem.
 * Representation of a gene in GA terms.
 */
public class KnapsackItem implements Cloneable{
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
    /**
     * toString method
     * @return - string representation of the KnapsackItem
     */
    @Override
    public String toString() {
        return "Number: " + this.number + ", " + "Weight: " + this.weight + ", " + "Value: " + this.value;
    }

    @Override
    public KnapsackItem clone() {
        return new KnapsackItem(this.number, this.weight, this.value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null)
            return false;

        if (getClass() != other.getClass())
            return false;

        KnapsackItem otherItem = (KnapsackItem) other;
        return this.number == otherItem.getNumber() ? true : false;
    } 
} 