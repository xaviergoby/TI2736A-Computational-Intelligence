import java.io.IOException;
import java.util.ArrayList;

/**
 * TSP problem solver using genetic algorithms
 */
public class GeneticAlgorithm {

    private int generations;
    private int popSize;


    public GeneticAlgorithm(int generations, int popSize) {
        this.generations = generations;
        this.popSize = popSize;
    }


    /**
     * Knuth-Yates shuffle, reordering a array randomly
     * @param chromosome array to shuffle.
     */
    private void shuffle(int[] chromosome) {
        int n = chromosome.length;
        for (int i = 0; i < n; i++) {
            int r = i + (int) (Math.random() * (n - i));
            int swap = chromosome[r];
            chromosome[r] = chromosome[i];
            chromosome[i] = swap;
        }
    }

    public int[] solveTSP(TSPData pd) {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 17};
    }

    /**
     * Assignment 2.b
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int populationSize = 20;
        int generations = 20;
        String persistFile = "./tmp/productMatrixDist";
        TSPData tspData = TSPData.readFromFile(persistFile);
        GeneticAlgorithm ga = new GeneticAlgorithm(generations, populationSize);
        int[] solution = ga.solveTSP(tspData);
        tspData.writeActionFile(solution, "./data/TSP solution.txt");
    }
}
