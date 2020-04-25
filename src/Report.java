import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class used to generate a report for a given simulation and write it to a file.
 */
public class Report implements Comparable<Report>{
    private String fileName;
    private Configuration config;
    private String reportHeader;
    private ArrayList<Knapsack> bestKnapsacksByIteration;
    private int bestFitness;
    private long completeTime;

    /**
     * Constructor.
     * @param fileName - of config.
     * @param config - for simulation.
     */
    public Report(String fileName, Configuration config) {
        this.fileName = fileName;
        this.config = config;
        bestKnapsacksByIteration = new ArrayList<>();
        this.reportHeader = generateReportHeader();
    }

    /**
     * Add new knapsack and update best fitness value.
     * Note best fitness won't necessarily be the newest knapsack if elitism ratio is 0.
     * @param bestKnapsack - for the current iteration.
     */
    public void addIteration(Knapsack bestKnapsack){
        this.bestFitness = bestKnapsack.getFitness() > this.bestFitness ? bestKnapsack.getFitness() : this.bestFitness;
        bestKnapsacksByIteration.add(bestKnapsack);
    }

    /**
     * Generate the report header.
     * @return String - report header
     */
    public String generateReportHeader() {
        StringBuilder reportHeader = new StringBuilder("Evaluation | " + new Date() + "\n");
        reportHeader.append("Configuration: " + this.fileName + "\n");
        reportHeader.append("               " + this.config.toString() + "\n");
        reportHeader.append("=".repeat(100) + "\n");
        reportHeader.append("#" + " ".repeat(5) + "bWeight" + " ".repeat(5) + "bValue" + " ".repeat(5) + "sQuality" + " ".repeat(5) + "Knapsack\n");
        reportHeader.append("-".repeat(100)+"\n");
        return new String(reportHeader);
    }

    /**
     * Write the report to a file
     * @param saveFilePath - where it should be saved to.
     */
    public void save(String saveFilePath){
        String reportBody = completeReport();
        String report = this.reportHeader + reportBody;
        try{
            Files.writeString(Paths.get(saveFilePath), report);
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Generate the report body.
     * @return String - report body.
     */
    public String completeReport(){
        StringBuilder reportBody = new StringBuilder();
        for(int i = 0; i < bestKnapsacksByIteration.size(); i++){
            reportBody.append(String.format("%0$-5s", i+1));
            reportBody.append(" ".repeat(5));
            reportBody.append(bestKnapsacksByIteration.get(i).toReportString(true) + "\n");
        }
        reportBody.append("-".repeat(100) + "\n");
        reportBody.append("[Statistics]\n");
        reportBody.append("Runtime");
        reportBody.append(" ".repeat(5));
        reportBody.append(this.completeTime + "ms\n\n");
        reportBody.append("Covergence" + " ".repeat(5) + "#" + " ".repeat(5) + "bWeight" + " ".repeat(5) + "bValue" + " ".repeat(5) + "sQuality" + "\n");

        if(bestKnapsacksByIteration.size() > 4){
            for(int i = 0; i < 4; i++){
                int quartile = bestKnapsacksByIteration.size()/(4 - i);
                reportBody.append(" ".repeat(14));
                reportBody.append(String.format("%0$-10s", quartile));
                reportBody.append(bestKnapsacksByIteration.get(quartile - 1).toReportString(false) + "\n");
            }
        }
        else{
            reportBody.append("Too few iterations for quartile data.\n");
        }
        

        reportBody.append("Plateau | Longest sequence " + getLongestPlateau() + "\n\n");
        reportBody.append("=".repeat(100) + "\n");

        return new String(reportBody);
    }

    /**
     * Set the time in ms that it took to run this simulation.
     * @param completeTime
     */
    public void setCompleteTime(long completeTime){
        this.completeTime = completeTime;
    }

    /**
     * Compare reports by fittest knapsack.
     */
    @Override
    public int compareTo(Report other){
        return Integer.compare(other.getBestFitness(), this.bestFitness);
    }

    /**
     * Get the fittest knapsack for this report.
     * @return
     */
    public int getBestFitness(){
        return bestFitness;
    }

    /**
     * Find the longest period with no growth in KnapsackFitness.
     * @return String - longest period in form "[start]-[end]"
     */
    private String getLongestPlateau(){
        int longestStart = 0, longestEnd = 0, start = 0, oldFitness = 0;
    
        for (int i = 0; i < this.bestKnapsacksByIteration.size(); i++){
            int currentFitness = this.bestKnapsacksByIteration.get(i).getFitness();
            if(oldFitness != currentFitness){
                if(i - start > longestEnd - longestStart){
                    longestStart = start;
                    longestEnd = i;
                }
                start = i + 1;
            }
            oldFitness = currentFitness;
        }
        if(this.bestKnapsacksByIteration.size() - start > longestEnd - longestStart){
            longestStart = start;
            longestEnd = this.bestKnapsacksByIteration.size();
        }

        return longestEnd - longestStart > 0 ? longestStart + "-" + longestEnd : "no plateau.";
    }

    /**
     * Save configuration used for this simulation.
     * @param saveFilePath - path for the file to be saved to
     * @param configurationType - ["ga"|"sa"|"pso"]
     */
    public void saveJson(String saveFilePath, String configurationType){
        try{
            String bestConfiguration = new String(Files.readAllBytes(Paths.get("data/configuration/" + configurationType + "/" +  this.fileName)));
            Files.writeString(Paths.get(saveFilePath + configurationType + "_best.json"), bestConfiguration);
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}