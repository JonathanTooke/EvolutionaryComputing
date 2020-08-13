import java.util.ArrayList;

public class Particle extends Knapsack {
    private ArrayList<Boolean> bestPosition;
    private ArrayList<Double> velocities;
    private int individualBestValue;


    ///////////////////////////
    ////    Constructors   ////
    ///////////////////////////

    /**
     * Default Constructor.
     */
    public Particle(){
        super();
    }

    /**
     * Constructor.
     * @param knapsackSelection - populate initial selected knapsack items
     */
    public Particle(ArrayList<Boolean> knapsackSelection) {
        super();
        this.knapsackSelection = Knapsack.copyBoolArrayList(knapsackSelection);
    }

    /**
     * For chaining with constructor to include a randomly
     * selected set of knapsack items.
     * @return this
     */
    public Particle withRandomPositions() {
        this.knapsackSelection = generateRandomItems();
        return this;
    }

    /**
     * For chaining with constructor to include a fitness 
     * calculation.
     * @return this
     */
    public Particle withFitnessCalculated() {
        this.fitness = calculateFitness();
        return this;
    }

    /**
     * For chaining with constructor to include preset velocities at 0 
     * @return this
     */
    public Particle withVelocitiesInitialized(SwarmConfiguration config){
        this.velocities = new ArrayList<>();
        for(int i = 0; i < Configuration.NUM_ITEMS; i++){
            double rand = Configuration.RANDOM_GENERATOR.nextDouble(config.getMinimumVelocity()/2, config.getMaximumVelocity()/2);
            this.velocities.add(rand);
        }
        return this;
    }

    ///////////////////////////
    ////   PSO Operators   ////
    ///////////////////////////
    

    /**
     * Update individual best value and position.
     */
    public void updateIndividualBestValue(){
        if(this.fitness > individualBestValue){
            this.bestPosition = Knapsack.copyBoolArrayList(this.knapsackSelection);
            this.individualBestValue = fitness;
        }
    }

    /**
     * Update the velocity at each index.
     * Vi(t + 1) = w(Vi) + (c1)(r1)[p_best(t) - xi(t)] + (c2)(r2)[g_best(t) - xi(t)]
     * @param globalBestPosition - binary representation of global best position
     */
    public void updateVelocity(ArrayList<Boolean> globalBestPosition, SwarmConfiguration config){
        for(int i =0; i < velocities.size(); i++){
            double w = config.getInertia();
            double Vi = this.velocities.get(i);
            double c1 = config.getC1();
            double c2 = config.getC2();
            int p_best = this.bestPosition.get(i) ? 1 : 0;
            int g_best = globalBestPosition.get(i) ? 1 : 0;
            int Xi = this.knapsackSelection.get(i) ? 1 : 0;
            double r1 = Configuration.RANDOM_GENERATOR.nextDouble();
            double r2 = Configuration.RANDOM_GENERATOR.nextDouble();
            double newVelocity = w*Vi + c1*r1*(p_best - Xi) + c2*r2*(g_best - Xi);

            if(newVelocity > config.getMaximumVelocity())
                newVelocity = config.getMaximumVelocity();
            else if(newVelocity < -config.getMinimumVelocity())
                newVelocity = -config.getMinimumVelocity();

            velocities.set(i, newVelocity);
        }
    }

    public void updatePosition(){
        for(int i =0; i < this.velocities.size(); i++){
            double rand = Configuration.RANDOM_GENERATOR.nextDouble();
            if(rand < sigmoid(this.velocities.get(i))){
                this.knapsackSelection.set(i, true);
            }
            else{
                this.knapsackSelection.set(i, false); 
            }
        }
    }

    /**
     * Sigmoid function used to determine whether an item should be included
     * or not given its velocity.
     * @param velocity - velocity for the item.
     * @return double - 1 if the item should be included, 0 otherwise.
     */
    private static double sigmoid(double velocity)
    {
        return 1 / (1 + Math.exp(-velocity));
    }
}   