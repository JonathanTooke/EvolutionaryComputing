public class Population {
    private Chromosome[] population;
    private Knapsack knapsack;

    /**
     * Constructor
     * @param knapsack - a Knapsack containing the possible KnapsackItems.
     */
    public Population(Knapsack knapsack) {
        this.knapsack = knapsack;
    }

    public void evolve() {
    }
}