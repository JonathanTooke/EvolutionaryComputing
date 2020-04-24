import java.util.ArrayList;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.List;

/**
 * Class used to represent and evolve a population of knapsacks for a GA Configuration.
 */
public class Population {
    private PopulationConfiguration config;
    private List<Chromosome> population;

    /**
     * Constructor.
     * Initialize a random population.
     * @param config - GA configuration for this population.
     */
    public Population(PopulationConfiguration config) {
        this.config = config;
        this.population = initializePopulation();
    }

    /**
     * Build and return a randomly generated population of valid knapsacks.
     * @return List<Knapsack>.
     */
    private List<Chromosome> initializePopulation() {
        List<Chromosome> initialPopulation = new ArrayList<>();
        for (int i = 0; i < PopulationConfiguration.POPULATION_SIZE; i++){
            initialPopulation.add(new Chromosome().withRandomKnapsackItems().withFitnessCalculated());
        }
        return initialPopulation;
    }

    /**
     * Main event loop for the population. 
     * Handles the process of evolving one population to the next.
     */
    public Knapsack evolve() {
        //1. Extract The Elite 
        List<Chromosome> elite = extractElite(this.population, PopulationConfiguration.ELITISM_RATIO);

        //2. Select Parents
        this.population = selectParents(this.population, this.config.getSelectionMethod());

        //3. Offspring Production
        this.population = createOffspring(this.population, this.config.getCrossoverMethod(), this.config.getCrossoverRatio());

        //4. Offspring Mutation
        this.population = mutateOffspring(this.population, this.config.getMutationMethod(), this.config.getMutationRatio());

        //5. Update Fitness Values
        for(var sack : this.population){
            sack.setFitness(sack.calculateFitness());
        }
        
        //6. Merge elite back in.
        this.population = mergeElite(elite, this.population);

        //Debugging, can be enabled with the java -enableassertions flag.
        assert(this.population.containsAll(elite));
        assert(this.population.size() == PopulationConfiguration.POPULATION_SIZE);
        assert(countInvalidChildren() == 0);

        //Return fittest knapsack to application loop
        return getFittestKnapsack();
    }

    ///////////////////////
    /// Elitism Methods ///
    ///////////////////////

    /**
     * Extract the elite from the population given an elitism ratio.
     * @param population - parent population.
     * @param elitismRatio - ratio of elite to be extracted.
     * @return List<GAKnapsack>
     */
    private List<Chromosome> extractElite(List<Chromosome> population, double elitismRatio){
        Collections.sort(population);
        int num_elite = (int)(population.size() * elitismRatio);

        List<Chromosome> elite = new ArrayList<>();
        for(int i = 0; i < num_elite; i++){
            elite.add(new Chromosome(population.get(i)).withFitnessCalculated());
        }
        return elite;
    }

    /**
     * Ensure that the fittest individuals from the previous generation remain in the new
     * generation in accordance with the elitism ratio. Requires randomly removing some children
     * to maintain population size.
     * @param elite - elite individuials from the previous generation.
     * @param population - new generation.
     * @return List<GAKnapsack> - new population with elite members merged in.
     */
    private List<Chromosome> mergeElite(List<Chromosome> elite, List<Chromosome> population){
        List<Chromosome> newPopulation = new ArrayList<>(elite);
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
     * @return - List<GAKnapsack> seleceted parents.
     */
    private List<Chromosome> selectParents(List<Chromosome> population, String selectionMethod){
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
     * @return List<GAKnapsack> - selected parents.
     */
    private List<Chromosome> rouletteWheelSelect(List<Chromosome> population){
        List<Chromosome> newPopulation = new ArrayList<>();
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
     * @return List<GAKnapsack> - selected parents.
     */
    private List<Chromosome> tournamentSelect(List<Chromosome> population){
        List<Chromosome> newPopulation = new ArrayList<>();
        Collections.sort(population);

        for(int i = 0; i < population.size(); i++){
            int bestCandidate = 0;
            for(int j = 0; j < PopulationConfiguration.TOURNAMENT_SIZE; j++){
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
     * @return List<GAKnapsack> - new offspring
     */
    private List<Chromosome> createOffspring(List<Chromosome> population, String crossoverMethod, double crossoverRatio){
        ArrayList<Chromosome> tempPop = new ArrayList<>();
        ArrayList<Chromosome> children = new ArrayList<>();

        Chromosome parent1, parent2;

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
    private List<Chromosome> mutateOffspring(List<Chromosome> population, String mutationMethod, double mutationRatio){
        List<Chromosome> mutatedPopulation = new ArrayList<>();

        for(var sack : this.population){
            double mutationProbability = Configuration.RANDOM_GENERATOR.nextDouble(); 
            if(mutationProbability < mutationRatio){
                switch(mutationMethod){
                    case "BFM":
                        mutatedPopulation.add(sack.doBitFlipMutation());
                        break;
                    case "IVM":
                        mutatedPopulation.add(sack.doInversionMutation());
                        break;
                    case "ISM":
                        mutatedPopulation.add(sack.doInsertionMutation());
                        break;
                    case "DPM":
                        mutatedPopulation.add(sack.doDisplacementMutation());
                        break;
                    case "EXM":
                        mutatedPopulation.add(sack.doExchangeMutation());
                        break;
                    default:
                        throw new RuntimeException("Unknown mutation method");
                } 
            }
            else{
                mutatedPopulation.add(sack);
            }
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
    public Chromosome getFittestKnapsack(){
        Collections.sort(this.population);
        return this.population.get(0);
    }

    public IntSummaryStatistics getSummaryStats(){
        return this.population.stream().mapToInt((x) -> x.getFitness()).summaryStatistics();
    }

    /**
     * Debugging method.
     * Should always return 0 unless there is a bug.
     */
    private int countInvalidChildren(){
        return (int)this.population.stream().filter(k -> !k.isValid()).count();
    }

    public List<Chromosome> getPopulation(){
        return this.population;
    }
}