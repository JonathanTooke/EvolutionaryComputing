import java.util.ArrayList;

public class Particle extends Knapsack {

    /**
     * Default Constructor.
     */
    public Particle(){
        super();
    }

    /**
     * Constructor.
     * @param knapsackSelection - populate initial selected knapsack items
     */
    public Particle(ArrayList<Boolean> knapsackSelection) {
        this.knapsackSelection = knapsackSelection;
    }

    /**
     * Copy constructor.
     * @param knapsack
     */
    public Particle(Particle particle){
        this.knapsackSelection = new ArrayList<Boolean>();
        for(var item : particle.getKnapsackSelection()){
            this.knapsackSelection.add(item);
        }
    }

    /**
     * For chaining with constructor to include a randomly
     * selected set of knapsack items.
     * @return this
     */
    public Particle withRandomKnapsackItems() {
        this.knapsackSelection = generateRandomItems();
        return this;
    }

    /**
     * For chaining with constructor to include a fitness 
     * calculation.
     * @return this
     */
    public Particle withFitnessCalculated() {
        this.fitness = calculateFitness();
        return this;
    }
}