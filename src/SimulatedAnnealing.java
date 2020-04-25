import java.util.ArrayList;
import java.util.IntSummaryStatistics;

public class SimulatedAnnealing extends SimulationManager{
    private SimulatedAnnealingConfiguration config;
    private Knapsack knapsack;
    private double temperature;
    private double coolingRate;

    /**
     * Constructor.
     * Initialize a random swarm.
     * @param config - SA configuration for this SA Instance.
     */
    public SimulatedAnnealing(SimulatedAnnealingConfiguration config) {
        this.config = config;
        this.temperature = config.getInitialTemperature();
        this.coolingRate = config.getCoolingRate();
        this.knapsack = new Knapsack();
        this.knapsack.setRandomlyGeneratedItems();
    }

    public Knapsack execute(){
        


        temperature = temperature*coolingRate;
        return null;
    }

    public double acceptanceProbability(double energy, double newEnergy, double temperature) {
        if (newEnergy < energy) {
            return 1;
        }
        return Math.exp((energy - newEnergy) / temperature);
    }

    private ArrayList<Boolean> getNewSolution(){
        int worst = -1;
        int best = -1;
        ArrayList<KnapsackItem> sackItems = (ArrayList<KnapsackItem>) this.knapsack.mapFromBinaryRepresentation(this.knapsack.getKnapsackSelection());
        for(int i = 0; i < this.knapsack.getKnapsackSelection().size(); i++){

        }
        return null;
    }

    
    /**
     * View summary stats for a given population state.
     * @return IntSummaryStatistics - summary statistics.
     */
    public IntSummaryStatistics getSummaryStats(){
        return null;
    }
}