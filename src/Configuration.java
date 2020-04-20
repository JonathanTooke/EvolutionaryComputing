public class Configuration {
    private final int numItems = 150; 
    private final int maxCapacity = 822;
    private final String KnapsackPath = "data/knapsack/knapsack_instance.csv";

    public String getKnapsackPath() {
        return this.KnapsackPath;
    }

    public int getNumItems() {
        return this.numItems;
    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }
}
