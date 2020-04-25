import java.util.ArrayList;

/**
 * Candidate Solution for Simulated Annealing algorithm
 */
public class SACandidate extends Knapsack{

    /**
     * Default constructor
     */
    public SACandidate(){}

    /**
     * Constructor.
     * @param knapsackSelection - populate initial selected knapsack items
     */
    public SACandidate(ArrayList<Boolean> knapsackSelection) {
        super();
        this.knapsackSelection = Knapsack.copyBoolArrayList(knapsackSelection);
    }

    /**
     * For chaining with constructor to include a randomly
     * selected set of knapsack items.
     * @return this
     */
    public SACandidate withRandomlySelectedItems() {
        this.knapsackSelection = generateRandomItems();
        return this;
    }

    /**
     * For chaining with constructor to include a fitness 
     * calculation.
     * @return this
     */
    public SACandidate withFitnessCalculated() {
        this.fitness = calculateFitness();
        return this;
    }

    public int calculateEnergy(){
        return calculateFitness();
    }
}