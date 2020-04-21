import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class used to represent a population of knapsacks
 */
public class Population {
    private GAConfiguration config;
    private List<Knapsack> population;

    /**
     * Constructor
     * @param config
     */
    public Population(GAConfiguration config) {
        this.config = config;
        this.population = initializePopulation();
        tournamentSelect();
    }

    private List<Knapsack> initializePopulation() {
        List<Knapsack> initialPopulation = new ArrayList<>();
        for (int i = 0; i < GAConfiguration.POPULATION_SIZE; i++){
            initialPopulation.add(new Knapsack().withRandomKnapsackItems().withFitnessCalculated());
        }
        return initialPopulation;
    }

    public Knapsack getBestKnapsack(){
        Collections.sort(this.population);
        return this.population.get(0);
    }

    public void evolve() {
        //1. Parent Selection
        if(this.config.getSelectionMethod().equals("RWS")){
            this.population = rouletteWheelSelect();
        }
        else if(this.config.getSelectionMethod().equals("TS")){
            this.population = tournamentSelect();
        }
        else{
            throw new RuntimeException("Unknown selection method");
        }
        //2. Offspring Production
        ArrayList<Knapsack> tempPop = new ArrayList<>();
        //Copy references to Knapsack ojects
        Knapsack parent1;
        Knapsack parent2;
        tempPop.addAll(this.population);
        while(tempPop.size() >= 2){
            int rand1 = Configuration.RANDOM_GENERATOR.nextInt(tempPop.size());
            parent1 = tempPop.get(rand1);
            tempPop.remove(parent1);

            int rand2 = Configuration.RANDOM_GENERATOR.nextInt(tempPop.size());
            parent2 = tempPop.get(rand2);
            tempPop.remove(parent2);

            double crossoverProbability = Configuration.RANDOM_GENERATOR.nextDouble(); 
            if (crossoverProbability < this.config.getCrossoverRatio()) {
                parent1.doCrossover(parent2, config.getCrossoverMethod());
            }
        } 
                
        //3. Offspring Mutation
        if(this.config.getMutationMethod().equals("BFM")){
            for(var sack : population){
                double mutationProbability = Configuration.RANDOM_GENERATOR.nextDouble(); 
                if(mutationProbability < config.getMutationRatio()){
                    System.out.println("Hee");
                    sack.bitFlipMutation();
                }
            }
        }
        else if(this.config.getMutationMethod().equals("IVM")){
            ;
        }
        else if(this.config.getMutationMethod().equals("ISM")){
            ;
        }
        else if(this.config.getMutationMethod().equals("DPM")){
            ;
        }
        else if(this.config.getMutationMethod().equals("EXM")){
            ;
        }
        else{
            throw new RuntimeException("Unknown mutation method");
        }
    }

    private List<Knapsack> rouletteWheelSelect(){
        List<Knapsack> newPopulation = new ArrayList<>();

        int totalFitness = this.population
            .stream()
            .mapToInt(Knapsack::getFitness)
            .sum();

        double probabilitySum = 0;
        for (var sack : this.population){
            double probability = sack.getFitness()/(double)totalFitness;
            sack.setRwsValue(probabilitySum + probability);
            probabilitySum += probability;
        }

        for(int i = 0; i < this.population.size(); i++){
            double random = Configuration.RANDOM_GENERATOR.nextDouble();
            for (int j = 0; j < this.population.size(); j++){
                //Can assume sorted in order since RWS is strictly increasing function
                if (random <= this.population.get(j).getRwsValue()){ 
                    newPopulation.add(this.population.get(j));
                }
            } 
        }
        return newPopulation;
    }

    private List<Knapsack> tournamentSelect(){
        List<Knapsack> newPopulation = new ArrayList<>();
        Collections.sort(this.population);

        for(int i = 0; i < this.population.size(); i++){
            int bestCandidate = 0;
            for(int j = 0; j < GAConfiguration.TOURNAMENT_SIZE; j++){
                int candidate = Configuration.RANDOM_GENERATOR.nextInt(this.population.size());
                bestCandidate = candidate > bestCandidate ? candidate : bestCandidate;
            }
            newPopulation.add(this.population.get(bestCandidate));
        }
        return newPopulation;
    }


}