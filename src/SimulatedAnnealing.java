import java.util.ArrayList;
import java.util.IntSummaryStatistics;

public class SimulatedAnnealing extends Knapsack{
    private SimulatedAnnealingConfiguration config;
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
        this.knapsackSelection = generateRandomItems();
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
        ArrayList<KnapsackItem> sackItems = (ArrayList<KnapsackItem>) mapFromBinaryRepresentation(this.knapsackSelection);
        for(int i = 0; i < this.knapsackSelection.size(); i++){

        }
        return null;
    }
}