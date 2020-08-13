import java.util.ArrayList;
import java.util.Collections;
import java.util.IntSummaryStatistics;

/**
 * Class used to represent and evolve a population of Particles for a given PSO
 * Configuration.
 */
public class Swarm extends SimulationManager{
    private ArrayList<Boolean> globalBestPosition;
    private ArrayList<Particle> swarm;
    private int globalBestValue;
    private SwarmConfiguration config;

    /**
     * Constructor.
     * Initialize a random swarm.
     * @param config - PSO configuration for this swarm.
     */
    public Swarm(SwarmConfiguration config) {
        this.config = config;
        this.swarm = initializeSwarm();
        Collections.sort(this.swarm);
        this.globalBestPosition = Knapsack.copyBoolArrayList(swarm.get(0).getKnapsackSelection());
    }

    /**
     * Execute an iteration of the PSO algorithm
     * @return Particle - the optimal particle.
     */
    public Particle execute(){
        //1. Find new global best
        if(swarm.get(0).getFitness() > globalBestValue){
            this.globalBestPosition = Knapsack.copyBoolArrayList(swarm.get(0).getKnapsackSelection());
            this.globalBestValue = swarm.get(0).getFitness();
        }
        //2. Update particle best values
        for (var particle : swarm) {
            particle.updateIndividualBestValue();
            particle.updateVelocity(this.globalBestPosition, this.config);
            particle.updatePosition();
        }

        //3. Update current fitness for each particle 
        updateSwarmFitness();

        //4. Sort swarm by fitness for the next iteration
        Collections.sort(this.swarm);

        return new Particle(globalBestPosition).withFitnessCalculated();
    }

    /**
     * Intiialize the particle swarm.
     * @return ArrayList<Particle> - the intial random swarm.
     */
    private ArrayList<Particle> initializeSwarm(){
        ArrayList<Particle> initialSwarm = new ArrayList<>();
        for (int i = 0; i < config.getNumParticles(); i++){
            initialSwarm.add(new Particle().withRandomPositions().withFitnessCalculated().withVelocitiesInitialized(this.config));
        }
        return initialSwarm;
    }

    /**
     * Update the fitness value associated with each particle.
     */
    private void updateSwarmFitness(){
        for(var particle : swarm){
            particle.updateFitness();
        }
    }

    /**
     * View summary stats for a given population state.
     * @return IntSummaryStatistics - summary statistics.
     */
    public IntSummaryStatistics getSummaryStats(){
        return this.swarm.stream().mapToInt((x) -> x.getFitness()).summaryStatistics();
    }

}