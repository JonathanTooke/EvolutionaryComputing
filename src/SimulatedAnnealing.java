import java.util.IntSummaryStatistics;

public class SimulatedAnnealing {
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
    }

    public Knapsack execute(){
        return null;
    }

    public double acceptanceProbability(double energy, double newEnergy, double temperature) {
        if (newEnergy < energy) {
            return 1;
        }
        return Math.exp((energy - newEnergy) / temperature);
    }
}