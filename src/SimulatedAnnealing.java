import java.util.ArrayList;
import java.util.IntSummaryStatistics;

/**
 * Simulated Annealing Simulation Manager
 */
public class SimulatedAnnealing extends SimulationManager{
    private final double coolingRate;
    private SACandidate bestCandidate;
    private SACandidate candidate;
    private double temperature;

    /**
     * Constructor.
     * Initialize a random confiuration.
     * @param config - SA configuration for this SA Instance.
     */
    public SimulatedAnnealing(SimulatedAnnealingConfiguration config) {
        this.temperature = config.getInitialTemperature();
        this.coolingRate = config.getCoolingRate();
        this.candidate = new SACandidate().withRandomlySelectedItems().withFitnessCalculated();
        this.bestCandidate = candidate;
    } 

    /**
     * Execute an iteration of Simulated Annealing.
     */
    public Knapsack execute(){
        if(this.temperature < 1)
            return null;

        int currentEnergy = this.candidate.calculateEnergy();

        SACandidate newCandidate = getNewSolution();
        int newCandidateEnergy = newCandidate.calculateEnergy();
        
        if(acceptanceProbability(currentEnergy, newCandidateEnergy, this.temperature) > Configuration.RANDOM_GENERATOR.nextDouble()){
            candidate = newCandidate;
        }

        if(newCandidate.calculateEnergy() > this.bestCandidate.calculateEnergy()){
            this.bestCandidate = newCandidate;
            this.bestCandidate.updateFitness();
        }

        this.temperature *= (1 - this.coolingRate);
    
        return this.bestCandidate;
    }

    /**
     * Probability of accepting the new candidate as the current candidate
     * @return 1 if the new candidate is better, otherwise a decimal as calculated below
     */
    private double acceptanceProbability(int currentEnergy, int newEnergy, double temperature) {
        if (newEnergy < currentEnergy) {
            return 1;
        }
        return Math.exp((currentEnergy - newEnergy) / temperature);
    }


    /**
     * Remove or add an item to the knapsack until it is valid.
     * @return SACandidate - new solution.
     */
    private SACandidate getNewSolution(){
        while(true){
            ArrayList<Boolean> newSelection = Knapsack.copyBoolArrayList(this.candidate.getKnapsackSelection());
            int itemToMutate = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            newSelection.set(itemToMutate, !newSelection.get(itemToMutate));

            SACandidate mutatedKnapsack = new SACandidate(newSelection).withFitnessCalculated();
            if(mutatedKnapsack.isValid())
                return mutatedKnapsack;
        }
    }

    /**
     * View summary stats for a given population state.
     * @return IntSummaryStatistics - summary statistics.
     * Note: This doesn't work well for SA. Here, count is the temperature, min is the candidate fitness,
     * max is the bestCandidate fitness and sum=0.
     */
    public IntSummaryStatistics getSummaryStats(){
        return new IntSummaryStatistics((int)this.temperature, this.candidate.calculateFitness(), this.bestCandidate.calculateFitness(), 0);
    }
}