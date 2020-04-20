import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Knapsack {
    private KnapsackItem[] knapsackItems;

    public Knapsack(String fileName) {
        loadKnapsack(fileName);
        // this.knapsackItems = loadKnapsack(fileName);
    }

    private void loadKnapsack(String fileName){
        File file = new File(fileName);
        try {
            Scanner sc = new Scanner(file); 
            while (sc.hasNextLine()) 
            System.out.println(sc.nextLine()); 
            sc.close();
        } 
        catch(IOException e) {
            throw new RuntimeException("Error loading knapsack", e);
        }
    }



    public KnapsackItem[] getKnapsackItems() {
        return this.knapsackItems;
    }

}