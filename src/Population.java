import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class used to represent and evolve a population of knapsacks.
 */
public class Population {
    private GAConfiguration config;
    private List<Knapsack> population;

    /**
     * Constructor.
     * Initialize a random population.
     * @param config - GA configuration for this population.
     */
    public Population(GAConfiguration config) {
        this.config = config;
        this.population = initializePopulation();
    }

    /**
     * Build and return a randomly generated population of valid knapsacks.
     * @return List<Knapsack>.
     */
    private List<Knapsack> initializePopulation() {
        List<Knapsack> initialPopulation = new ArrayList<>();
        for (int i = 0; i < GAConfiguration.POPULATION_SIZE; i++){
            initialPopulation.add(new Knapsack().withRandomKnapsackItems().withFitnessCalculated());
        }
        return initialPopulation;
    }

    /**
     * Main event loop for the population. 
     * Handles the process of evolving one population to the next.
     */
    public void evolve() {
        //1. Update Fitness Values
        for(var sack : this.population){
            sack.setFitness(sack.calculateFitness());
        }

        //2. Extract The Elite 
        List<Knapsack> elite = extractElite(this.population, GAConfiguration.ELITISM_RATIO);

        //3. Select Parents
        this.population = selectParents(this.population, this.config.getSelectionMethod());

        //4. Offspring Production
        this.population = createOffspring(this.population, this.config.getCrossoverMethod(), this.config.getCrossoverRatio());

        //5. Offspring Mutation
        this.population = mutateOffspring(this.population, this.config.getMutationMethod(), this.config.getMutationRatio());

        //6. Merge elite back in.
        this.population = mergeElite(elite, this.population);

        //Debugging, can be enabled with the java -enableassertions flag.
        assert(this.population.containsAll(elite));
        assert(this.population.size() == GAConfiguration.POPULATION_SIZE);
        assert(countInvalidChildren() == 0);
    }

    ///////////////////////
    /// Elitism Helpers ///
    ///////////////////////

    /**
     * Extract the elite from the population given an elitism ratio.
     * @param population - parent population.
     * @param elitismRatio - ratio of elite to be extracted.
     * @return List<Knapsack>
     */
    private List<Knapsack> extractElite(List<Knapsack> population, double elitismRatio){
        Collections.sort(population);
        int num_elite = (int)(population.size() * elitismRatio);

        List<Knapsack> elite = new ArrayList<>();
        elite.addAll(population.subList(0, num_elite));
        return elite;
    }

    /**
     * Ensure that the fittest individuals from the previous generation remain in the new
     * generation in accordance with the elitism ratio. Requires randomly removing some children
     * to maintain population size.
     * @param elite - elite individuials from the previous generation.
     * @param population - new generation.
     * @return List<Knapsack> - new population with elite members meged in.
     */
    private List<Knapsack> mergeElite(List<Knapsack> elite, List<Knapsack> population){
        List<Knapsack> newPopulation = new ArrayList<>();
        for(var individual : elite){
            if(!population.contains(individual)){
                newPopulation.add(individual);
            }
        }
        int individualsToAdd = population.size() - newPopulation.size();
        for (int i = 0; i < individualsToAdd; i++){
            int rand = Configuration.RANDOM_GENERATOR.nextInt(population.size());
            newPopulation.add(population.get(rand));
            population.remove(population.get(rand));
        }
        return newPopulation;
    }

    /////////////////////////
    /// Parent Selection  ///
    /////////////////////////

    /**
     * Select the parents based on selection method.
     * @return - List<Knapsack> seleceted parents.
     */
    private List<Knapsack> selectParents(List<Knapsack> population, String selectionMethod){
        if(selectionMethod.equals("RWS")){
            return rouletteWheelSelect(population);
        }
        else if(selectionMethod.equals("TS")){
            return tournamentSelect(population);
        }
        else{
            throw new RuntimeException("Unknown selection method");
        }
    }

    /**
     * Implementation of Roulette Wheel Selection.
     * @return List<Knapsack> - selected parents.
     */
    private List<Knapsack> rouletteWheelSelect(List<Knapsack> population){
        List<Knapsack> newPopulation = new ArrayList<>();
        int totalFitness = population
            .stream()
            .mapToInt(Knapsack::getFitness)
            .sum();

        double probabilitySum = 0;
        for (var sack : population){
            double probability = sack.getFitness()/(double)totalFitness;
            sack.setRwsValue(probabilitySum + probability);
            probabilitySum += probability;
        }

        for(int i = 0; i < population.size(); i++){
            double random = Configuration.RANDOM_GENERATOR.nextDouble();
            for (int j = 0; j < population.size(); j++){
                //Can assume sorted in order of RWS.
                if (random <= population.get(j).getRwsValue()){ 
                    newPopulation.add(population.get(j));
                    break;
                }
            } 
        }
        return newPopulation;
    }

    /**
     * Implementation of Tournament Selection.
     * @return List<Knapsack> - selected parents.
     */
    private List<Knapsack> tournamentSelect(List<Knapsack> population){
        List<Knapsack> newPopulation = new ArrayList<>();
        Collections.sort(population);

        for(int i = 0; i < population.size(); i++){
            int bestCandidate = 0;
            for(int j = 0; j < GAConfiguration.TOURNAMENT_SIZE; j++){
                int candidate = Configuration.RANDOM_GENERATOR.nextInt(population.size());
                bestCandidate = candidate > bestCandidate ? candidate : bestCandidate;
            }
            newPopulation.add(population.get(bestCandidate));
        }
        return newPopulation;
    }

    //////////////////////////////
    //// Offspring Production ////
    //////////////////////////////

    /**
     * Create offspring by considering crossover probability and method.
     * @return List<Knapsack> - new offspring
     */
    private List<Knapsack> createOffspring(List<Knapsack> population, String crossoverMethod, double crossoverRatio){
        ArrayList<Knapsack> tempPop = new ArrayList<>();
        ArrayList<Knapsack> children = new ArrayList<>();

        Knapsack parent1, parent2;

        tempPop.addAll(population);
        int childrenSize = 0;
        while(childrenSize < population.size()){
            int rand1 = Configuration.RANDOM_GENERATOR.nextInt(tempPop.size());
            parent1 = tempPop.get(rand1);
            tempPop.remove(parent1);

            int rand2 = Configuration.RANDOM_GENERATOR.nextInt(tempPop.size());
            parent2 = tempPop.get(rand2);
            tempPop.remove(parent2);

            double crossoverProbability = Configuration.RANDOM_GENERATOR.nextDouble(); 
            if (crossoverProbability < crossoverRatio) {
                children.addAll(parent1.doCrossover(parent2, crossoverMethod));
            }
            else{
                children.add(parent1);
                children.add(parent2);
            }
            childrenSize += 2;
        } 
        return children;
    }

    ////////////////////////////
    //// Offspring Mutation ////
    ////////////////////////////

    /**
     * Mutate offspring beased on the mutation method and mutation ratio.
     * @param population - child population on current iteration.
     * @param mutationMethod - method used to mutate ["BFM", "IVM", "ISM", "DPM", "EXM"].
     * @param mutationRatio - probability of mutation.
     * @return List<Knapsack> - mutated offspring.
     */
    private List<Knapsack> mutateOffspring(List<Knapsack> population, String mutationMethod, double mutationRatio){
        List<Knapsack> mutatedPopulation = new ArrayList<>();

        if(mutationMethod.equals("BFM")){
            for(var sack : this.population){
                double mutationProbability = Configuration.RANDOM_GENERATOR.nextDouble(); 
                if(mutationProbability < mutationRatio){
                    mutatedPopulation.add(sack.doBitFlipMutation());
                }
                else{
                    mutatedPopulation.add(sack);
                }
            }
        }
        else if(mutationMethod.equals("IVM")){
            ;
        }
        else if(mutationMethod.equals("ISM")){
            ;
        }
        else if(mutationMethod.equals("DPM")){
            ;
        }
        else if(mutationMethod.equals("EXM")){
            ;
        }
        else{
            throw new RuntimeException("Unknown mutation method");
        }
        return mutatedPopulation;
    }

    //////////////////////
    /// Helper Methods ///
    //////////////////////

    /**
     * Return the fittest knapsack in the population.
     * @return Knapsack
     */
    public Knapsack getFittestKnapsack(){
        Collections.sort(this.population);
        return this.population.get(0);
    }

    /**
     * Debugging method.
     * Should always return 0 unless there is a bug.
     */
    private int countInvalidChildren(){
        return (int)this.population.stream().filter(k -> !k.isValid()).count();
    }
}