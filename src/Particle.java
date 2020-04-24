import java.util.ArrayList;

public class Particle extends Knapsack {
/**
     * Default constructor.
     */
    public Particle() {
        super();
    }

    /**
     * Copy constructor.
     * @param knapsack
     */
    public Particle(Knapsack knapsack){
        super();
        this.knapsackSelection = new ArrayList<Boolean>();
        for(var item : knapsack.getKnapsackSelection()){
            this.knapsackSelection.add(item);
        }
    }

    /**
     * Constructor.
     * @param knapsackSelection
     */
    public Particle(ArrayList<Boolean> knapsackSelection) {
        super();
        this.knapsackSelection = knapsackSelection;
    }
}