public class KnapsackItem {
    private int number;
    private int weight;
    private int value;

    public KnapsackItem(int number, int weight, int value) {
        this.number = number;
        this.weight = weight;
        this.value = value;
    }

    public int getNumber() {
        return this.number;
    }
    public int getWeight() {
        return this.weight;
    }
    public int getValue() {
        return this.value;
    }
    public String toString() {
        return "Number: " + this.number + "\n" + "Weight: " + this.weight + "\n" + "Value: " + this.value;
    }
}