import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class Report implements Comparable<Report>{
    private String fileName;
    private Configuration config;
    private String reportHeader;
    private ArrayList<Knapsack> bestKnapsacksByIteration;
    private int bestFitness;
    private long completeTime;

    public Report(String fileName, Configuration config) {
        this.fileName = fileName;
        this.config = config;
        bestKnapsacksByIteration = new ArrayList<>();
        this.reportHeader = generateReportHeader();
    }

    public void addIteration(Knapsack bestKnapsack){
        this.bestFitness = bestKnapsack.getFitness();
        bestKnapsacksByIteration.add(bestKnapsack);
    }

    public String generateReportHeader() {
        StringBuilder reportHeader = new StringBuilder("Evaluation | " + new Date() + "\n");
        reportHeader.append("Configuration: " + this.fileName + "\n");
        reportHeader.append("               " + this.config.toString() + "\n");
        reportHeader.append("=".repeat(100) + "\n");
        reportHeader.append("#" + " ".repeat(5) + "bWeight" + " ".repeat(5) + "bValue" + " ".repeat(5) + "sQuality" + " ".repeat(5) + "Knapsack\n");
        reportHeader.append("-".repeat(100)+"\n");
        return new String(reportHeader);
    }

    public void save(){
        String reportBody = completeReport();
        String report = this.reportHeader + reportBody;
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year  = localDate.getYear();
        int month = localDate.getMonthValue();
        int day   = localDate.getDayOfMonth();
        try{
            Files.writeString(Paths.get("data/results/ga/report_"+ this.fileName.substring(0,this.fileName.length() - 5) + "_" + year + month + day + ".txt"), report);
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String completeReport(){
        StringBuilder reportBody = new StringBuilder();
        for(int i = 0; i < bestKnapsacksByIteration.size(); i++){
            reportBody.append(String.format("%0$-4s", i+1));
            reportBody.append(" ".repeat(5));
            reportBody.append(bestKnapsacksByIteration.get(i).toReportString(true) + "\n");
        }
        reportBody.append("-".repeat(100) + "\n");
        reportBody.append("[Statistics]\n");
        reportBody.append("Runtime");
        reportBody.append(" ".repeat(5));
        reportBody.append(this.completeTime + "ms\n\n");
        reportBody.append("Covergence" + " ".repeat(5) + "#" + " ".repeat(5) + "bWeight" + " ".repeat(5) + "bValue" + " ".repeat(5) + "sQuality" + "\n");

        for(int i = 0; i < 4; i++){
            int quartile = bestKnapsacksByIteration.size()/(4 - i);
            reportBody.append(" ".repeat(14));
            reportBody.append(String.format("%0$-10s", quartile));
            reportBody.append(bestKnapsacksByIteration.get(quartile - 1).toReportString(false) + "\n");
        }

        String sequenceLength = "..."; 
        String improvement = "..."; 

        reportBody.append("Plateau | Longest sequence " + sequenceLength + " with improvement less average " + improvement + "%\n\n");
        reportBody.append("=".repeat(100) + "\n");

        return new String(reportBody);
    }

    public void setCompleteTime(long completeTime){
        this.completeTime = completeTime;
    }

    @Override
    public int compareTo(Report other){
        return Integer.compare(other.getBestFitness(), this.bestFitness);
    }

    public int getBestFitness(){
        return bestFitness;
    }
}