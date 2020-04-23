import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Report {
    private String fileName;
    private Configuration config;
    private String reportHeader;
    private ArrayList<HashMap<Integer, Knapsack>> bestKnapsacksByIteration;

    public Report(String fileName, Configuration config) {
        this.fileName = fileName;
        bestKnapsacksByIteration = new ArrayList<>();
        this.reportHeader = generateReportHeader(fileName, config);
    }

    public void addIteration(int iterationNumber, Knapsack bestKnapsack){
        HashMap<Integer, Knapsack> iterationBest = new HashMap<>();
        iterationBest.put(iterationNumber, bestKnapsack);
        bestKnapsacksByIteration.add(iterationBest);
    }

    public String generateReportHeader(String fileName, Configuration config) {
        StringBuilder reportHeader = new StringBuilder("Evaluation | " + new Date() + "\n");
        reportHeader.append("Configuration: " + fileName + "\n");
        reportHeader.append("               " + config.toString() + "\n");
        reportHeader.append("=".repeat(30) + "\n");
        reportHeader.append("#" + " ".repeat(5) + "bWeight" + " ".repeat(5) + "bValue" + " ".repeat(5) + "sQuality" + " ".repeat(5) + "Knapsack\n");
        reportHeader.append("-".repeat(30)+"\n");
        return new String(reportHeader);
    }

    public void save(String runtime){
        String reportBody = completeReport(runtime);
        String report = this.reportHeader + reportBody;
        try{
            Files.writeString(Paths.get("data/results/ga/result_"+this.fileName), report);
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String completeReport(String runtime){
        StringBuilder reportBody = new StringBuilder();
        for(int i = 0; i < bestKnapsacksByIteration.size(); i++){
            var knapMap = bestKnapsacksByIteration.get(i);
            reportBody.append(i);
            reportBody.append(" ".repeat(5));
            reportBody.append(knapMap.get(i).toString() + "\n");
        }
        reportBody.append("-".repeat(30));
        reportBody.append("[Statistics]\n");
        reportBody.append("Runtime");
        reportBody.append(" ".repeat(5));
        reportBody.append(runtime + "ms\n\n");
        reportBody.append("Covergence" + " ".repeat(5) + "#" + " ".repeat(5) + "bWeight" + " ".repeat(5) + "bValue" + " ".repeat(5) + "sQuality");
        return new String(reportBody);
    }


}