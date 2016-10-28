import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * TSP problem solver using genetic algorithms
 */
public class GeneticAlgorithm {

    private int generations;
    private int popSize;
    private double crossOverChance;
    private double mutationChance;
    private Random randomizer;

    public GeneticAlgorithm(int generations, int popSize, double crossOverChance, double mutationChance) {
        this.generations = generations;
        this.popSize = popSize;
        this.crossOverChance = crossOverChance;
        this.mutationChance = mutationChance;
        this.randomizer = new Random();
    }

    /**
     * Solves the TSP using a genetic algorithm.
     * @param pd The TSPData.
     * @return int[] The order of items.
     */
    public int[] solveTSP(TSPData pd) {
    	List<Chromosome> population = new ArrayList<>();
    	initializePopulation(population);
    	
    	int n = 0;
    	while (n < generations) {
    		calculateFitness(population, pd);
    		population = createNextGeneration(population);
    		n += 1;
    	}
    	
        return getBestChromosome(population).getChromosome();
    }
    
    /**
     * Creates the next generation.
     * @param currentPopulation The current geneation.
     * @return List<Chromosome> The next generation.
     */
    public List<Chromosome> createNextGeneration(List<Chromosome> currentPopulation) {
    	List<Chromosome> nextGeneration = new ArrayList<>(popSize);
    	
    	while (nextGeneration.size() < 20) {
    		// make children
    		Chromosome father = getRouletteChromosome(currentPopulation);
    		
    		// should check whether mother != father
    		Chromosome mother = getRouletteChromosome(currentPopulation);
    		
    		double crossOver = randomizer.nextDouble();
    		double mutation = randomizer.nextDouble();
    		
    		// clone the best chromosome in case no offspring can be generated
    		Chromosome child = getFittestCandidate(father,mother);
    		
    		if (crossOver <= crossOverChance) {
    			// one-point crossover
    			child = createCrossOver(father,mother);
    		}
    		
    		if (mutation <= mutationChance) {
    			// apply mutation
    			child.mutate(randomizer);
    		}
    		
    		nextGeneration.add(child);
    		
    	}
    	
    	return nextGeneration;
    }
    
    /**
     * Returns the best chromosome in a list.
     * @param gen The generation with Chromosomes.
     * @return Çhromosome The best Chromosome.
     */
    public Chromosome getBestChromosome(List<Chromosome> gen) {
    	Chromosome best = gen.get(0);
    	for (Chromosome chromosome : gen) {
    		if (chromosome.getFitness() >= best.getFitness()) {
    			best = chromosome;
    		}
    	}
    	return best;
    }
    
    /**
     * Creates a single-point crossover.
     * @param father The father Chromosome.
     * @param mother The mother Chromosome.
     * @return Chromosome The child Chromosome.
     */
    public Chromosome createCrossOver(Chromosome father, Chromosome mother) {
    	int crossOverPoint = randomizer.nextInt(father.getChromosome().length);
    	int[] childChromosome = new int[18];
    	
    	// copy values from father starting from crossoverpoint
    	for (int i = crossOverPoint; i < childChromosome.length; i++) {
    		childChromosome[i] = father.getChromosome()[i];
    	}
    	
    	// fill mother values
    	int childPointer = 0;
    	for (int j = 0; j < mother.getChromosome().length; j++) {
    		if (!hasGen(childChromosome, mother.getChromosome()[j])) {
    			childChromosome[childPointer] = mother.getChromosome()[j];
    			childPointer += 1;
    			if (childPointer >= crossOverPoint) {
    				break;
    			}
    		}
    	}
    	return new Chromosome(childChromosome);
    }
    
    /**
     * Checks if a int[] has a gen.
     * @param chromosome int[] list.
     * @param gen Gen to check for.
     * @return boolean True if it contains the gen.
     */
    public boolean hasGen(int[] chromosome, int gen) {
		for (int i = 0; i < chromosome.length; i++) {
			if (chromosome[i] == gen) {
				return true;
			}
		}
		return false;
	}
    
    /**
     * Get fittest Candidate out of 2 Chromosomes.
     * @param father Father Chromosome.
     * @param mother Mother Chromosome.
     * @return Chromosome The fittest chromosome.
     */
    public Chromosome getFittestCandidate(Chromosome father, Chromosome mother) {
    	return (father.getFitness() <= mother.getFitness()) ? father : mother;
    }
    
    /**
     * Select chromosome based on its fitness using a roulette wheel.
     * @param currentPopulation The current population.
     * @return Chromosome The selected Chromosome.
     */
    public Chromosome getRouletteChromosome(List<Chromosome> currentPopulation) {
    	double totalChance = 0;
    	Chromosome parent = new Chromosome(new int[0]);
    	double wheel = randomizer.nextDouble();
    	for (int i = 0; i < currentPopulation.size(); i++) {
    		totalChance += currentPopulation.get(i).getFitness();
    		if (wheel <= totalChance) {
    			parent = currentPopulation.get(i);
    			break;
    		}
    	}
    	return parent;
    }
    
    /**
     * Calculates the fitness of each chromosome.
     * @param pop List of Chromosomes.
     * @param tsp The TSPData.
     */
    public void calculateFitness(List<Chromosome> pop, TSPData tsp) {
    	int fitnessSum = 0;
    	for (Chromosome chromosome : pop) {
    		fitnessSum += getTotalDistance(chromosome, tsp);
    	}
    	
    	for (Chromosome chromosome : pop) {
    		chromosome.setFitness(1.0d - (getTotalDistance(chromosome,tsp) / fitnessSum));
    	}
    }
    
    /**
     * Get total distance of a chromosome.
     * @param order The chromosome.
     * @param tsp The TSPData to search in.
     * @return int The total distance.
     */
    public int getTotalDistance(Chromosome order, TSPData tsp) {
    	int total = tsp.getStartDistances()[order.getChromosome()[0]];
    	for (int i = 0; i < order.getChromosome().length-1; i++) {
    		total += tsp.getDistances()[i][i+1];
    	}
    	total += tsp.getEndDistances()[order.getChromosome().length-1];
    	return total;
    }
    
    /**
     * Initialize random population.
     * @param initialPop List<Chromosome> Empty gen.
     */
    public void initializePopulation(List<Chromosome> initialPop) {
    	Collections.fill(initialPop, new Chromosome(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 17}));
    	for (Chromosome chromosome : initialPop) {
    		chromosome.shuffle();
    	}
    }

    /**
     * Assignment 2.b
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int populationSize = 20;
        int generations = 20;
        double crossOverChance = 0.7d;
        double mutationChance = 0.01d;
        String persistFile = "./tmp/productMatrixDist";
        TSPData tspData = TSPData.readFromFile(persistFile);
        GeneticAlgorithm ga = new GeneticAlgorithm(generations, populationSize, crossOverChance, mutationChance);
        int[] solution = ga.solveTSP(tspData);
        tspData.writeActionFile(solution, "./data/TSP solution.txt");
    }
}
