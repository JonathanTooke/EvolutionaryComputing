import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chromosome used to represent a Knapsack for a given selection in a GA Algorithm
 * Provides crossover and mutation operators.
 */
public class Chromosome extends Knapsack{
    private double rwsValue;


    ///////////////////////////
    ////    Constructors   ////
    ///////////////////////////

    /**
     * Default Constructor.
     */
    public Chromosome(){
        super();
    }

    /**
     * Constructor.
     * @param knapsackSelection - populate initial selected knapsack items
     */
    public Chromosome(ArrayList<Boolean> knapsackSelection) {
        super();
        this.knapsackSelection = knapsackSelection;
    }

    /**
     * Copy constructor.
     * @param chromosome - chromosome to copy.
     */
    public Chromosome(Chromosome chromosome){
        super();
        this.knapsackSelection = new ArrayList<Boolean>();
        for(var item : chromosome.getKnapsackSelection()){
            this.knapsackSelection.add(item);
        }
    }

    /**
     * For chaining with constructor to include a randomly
     * selected set of knapsack items.
     * @return Chromosome - this
     */
    public Chromosome withRandomKnapsackItems() {
        this.knapsackSelection = generateRandomItems();
        return this;
    }

    /**
     * For chaining with constructor to include a fitness 
     * calculation.
     * @return Chromosome - this
     */
    public Chromosome withFitnessCalculated() {
        this.fitness = calculateFitness();
        return this;
    }


    ///////////////////////////
    //// Genetic Operators ////
    ///////////////////////////
    
    
    /**
     * One and Two Point Crossover Operations
     * @param other - the other Knapsack to perform the crossover with.
     * @param crossoverType - Either 1PX or 2PX i.e. 1 or 2 point.
     * @return
     */
    public List<Chromosome> doCrossover(Chromosome other, String crossoverType){
        int geneSize = this.knapsackSelection.size();

        ArrayList<Chromosome> children = new ArrayList<>();

        //Allow for multiple crossover attempts to better the chance of a valid crossover.
        //Note that this can be disabled by setting GAConfiguration.CONCEPTION_ATTEMPTS = 1.
        for(int i = 0; i < PopulationConfiguration.CONCEPTION_ATTEMPTS; i++){

            //Set first crossover point to 0 if 1PX Crossover.
            int crossPoint1 = crossoverType.equals("1PX") ? 0 : Configuration.RANDOM_GENERATOR.nextInt(geneSize);
            int crossPoint2 = Configuration.RANDOM_GENERATOR.nextInt(geneSize - crossPoint1) + crossPoint1;

            ArrayList<Boolean> c1 = new ArrayList<>();
            ArrayList<Boolean> c2 = new ArrayList<>();

            c1.addAll(this.knapsackSelection.subList(0, crossPoint1));
            c1.addAll(other.getKnapsackSelection().subList(crossPoint1, crossPoint2));
            c1.addAll(this.knapsackSelection.subList(crossPoint2, geneSize));
            Chromosome child1 = new Chromosome(c1).withFitnessCalculated();

            c2.addAll(other.getKnapsackSelection().subList(0, crossPoint1));
            c2.addAll(this.knapsackSelection.subList(crossPoint1, crossPoint2));
            c2.addAll(other.getKnapsackSelection().subList(crossPoint2, geneSize));
            Chromosome child2 = new Chromosome(c2).withFitnessCalculated();
            
            if(child1.isValid() && children.size() < 2){
                children.add(child1);
            }
            if(child2.isValid() && children.size() < 2){
                children.add(child2);
            }
            if(children.size() == 2){
                break;
            }
        }
        //If the crossover failed to generate a valid child, return the parents.
        if(children.size() == 0){
            children.add(other);
            children.add(this);
        }
        else if(children.size() == 1){
            children.add(this);
        }
        return children;
    }

    /**
     * Implementation of Bit Flip Mutation.
     * Note that the mutation will be attempted GAConfiguration.MUTATION_ATTEMPTS times.
     * If it is unsuccesful in producing a valid child on every attempt, it will return the original child.
     * @return Chromosome - mutated child.
     */
    public Chromosome doBitFlipMutation(){
        for(int i = 0; i < PopulationConfiguration.MUTATION_ATTEMPTS; i++){
            ArrayList<Boolean> newSelection = copyBoolArrayList(this.knapsackSelection);
            int itemToMutate = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            newSelection.set(itemToMutate, !newSelection.get(itemToMutate));

            Chromosome mutatedKnapsack = new Chromosome(newSelection).withFitnessCalculated();
            if(mutatedKnapsack.isValid())
                return mutatedKnapsack;
        }
        return this;
    }

    /**
     * Implementation of Exchange Mutation. 
     * @return Knapsack - mutated child.
     */
    public Chromosome doExchangeMutation(){
        for(int i = 0; i < PopulationConfiguration.MUTATION_ATTEMPTS; i++){
            ArrayList<Boolean> newSelection = copyBoolArrayList(this.knapsackSelection);

            int allele1 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            int allele2 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            Collections.swap(newSelection, allele1, allele2);

            Chromosome mutatedKnapsack = new Chromosome(newSelection).withFitnessCalculated();
            if(mutatedKnapsack.isValid())
                return mutatedKnapsack;
        }
        return this;
    }

    /**
     * Implementation of Inversion Mutation. 
     * @return Knapsack - mutated child.
     */
    public Chromosome doInversionMutation(){
        for(int i = 0; i < PopulationConfiguration.MUTATION_ATTEMPTS; i++){
            ArrayList<Boolean> newSelection = copyBoolArrayList(this.knapsackSelection);

            int allele1 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            int allele2 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            Collections.reverse(newSelection.subList(Math.min(allele1, allele2), Math.max(allele1, allele2)));

            Chromosome mutatedKnapsack = new Chromosome(newSelection).withFitnessCalculated();
            if(mutatedKnapsack.isValid())
                return mutatedKnapsack;
        }
        return this;
    }

    /**
     * Implementation of Insertion Mutation. 
     * @return Knapsack - mutated child.
     */
    public Chromosome doInsertionMutation(){
        for(int i = 0; i < PopulationConfiguration.MUTATION_ATTEMPTS; i++){
            ArrayList<Boolean> newSelection = copyBoolArrayList(this.knapsackSelection);

            int allele1 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            int allele2 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            boolean value = newSelection.get(allele2);

            newSelection.remove(allele2);
            newSelection.add(allele1 + 1, value);

            Chromosome mutatedKnapsack = new Chromosome(newSelection).withFitnessCalculated();
            if(mutatedKnapsack.isValid())
                return mutatedKnapsack;
        }
        return this;
    }

    /**
     * Implementation of Displacement Mutation. 
     * @return Knapsack - mutated child.
     */
    public Chromosome doDisplacementMutation(){
        for(int i = 0; i < PopulationConfiguration.MUTATION_ATTEMPTS; i++){
            ArrayList<Boolean> newSelection = copyBoolArrayList(this.knapsackSelection);

            int allele1 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());
            int allele2 = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size());

            int leftAllele = Math.min(allele1, allele2);
            int rightAllele = Math.max(allele1, allele2);

            var selectionSublist = new ArrayList<Boolean>(newSelection.subList(leftAllele, rightAllele));
            for(int j = leftAllele; j < rightAllele + 1; j++){
                newSelection.remove(leftAllele);
            }

            int index = Configuration.RANDOM_GENERATOR.nextInt(newSelection.size()+1);
            newSelection.addAll(index, selectionSublist);

            Chromosome mutatedKnapsack = new Chromosome(newSelection).withFitnessCalculated();
            if(mutatedKnapsack.isValid())
                return mutatedKnapsack;
        }
        return this;
    }

    /////////////////////////////
    //// Getters and Setters ////
    /////////////////////////////

    public double getRwsValue() {
        return this.rwsValue;
    }

    public void setRwsValue(double rwsValue) {
        this.rwsValue = rwsValue;
    }
}