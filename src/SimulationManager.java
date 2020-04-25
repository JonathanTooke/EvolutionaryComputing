import java.util.IntSummaryStatistics;

/**
 * Parent of Swarm (PSO), Simulated Annealing (SA), and Population (GA)
 */
public abstract class SimulationManager {

    public abstract Knapsack execute();

    /**
     * View summary stats for a given population state.
     * @return IntSummaryStatistics - summary statistics.
     */
    public abstract IntSummaryStatistics getSummaryStats();
}