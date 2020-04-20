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
    }

    private List<Knapsack> initializePopulation() {
        List<Knapsack> initialPopulation = new ArrayList<>();
        for (int i = 0; i < GAConfiguration.POPULATION_SIZE; i++){
            initialPopulation.add(new Knapsack(config).withRandomKnapsackItems().withFitnessCalculated());
        }
        return initialPopulation;
    }

    public void evolve() {
    }

    private List<Knapsack> rouletteWheelSelect(){
        List<Knapsack> newPopulation = new ArrayList<>();

        int totalFitness = this.population
            .stream()
            .mapToInt(Knapsack::getFitness)
            .sum();

        double probabilitySum = 0;
        for (var sack : this.population){
            double probability = sack.getFitness()/totalFitness;
            sack.setRwsValue(probabilitySum + probability);
            probabilitySum += probability;
        }

        for(int i = 0; i < this.population.size(); i++){
            double random = Configuration.RANDOM_GENERATOR.nextDouble();
            for (int j = 0; j < this.population.size(); j++){
                if (random < this.population.get(j).getRwsValue()){
                    newPopulation.add(this.population.get(j));
                }
            } 
        }

        return newPopulation;
    }
}