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
	
	public class Chromosome {
		private int[] chromosome;
		private double fitness;
		
		public Chromosome(int[] chromosome) {
			this.chromosome = chromosome;
			this.fitness = 0;
		}
		
		/**
	     * Knuth-Yates shuffle, reordering a array randomly
	     * @param chromosome array to shuffle.
	     */
	    private void shuffle() {
	        int n = chromosome.length;
	        for (int i = 0; i < n; i++) {
	            int r = i + (int) (Math.random() * (n - i));
	            int swap = chromosome[r];
	            chromosome[r] = chromosome[i];
	            chromosome[i] = swap;
	        }
	    }
		
		public int[] getChromosome() { return chromosome; }
		public double getFitness() { return fitness; }
		public void setFitness(double newFitness) { fitness = newFitness; }
		
		public boolean equals(Object other) {
			if (!(other instanceof Chromosome)) return false;
			Chromosome castChromosome = (Chromosome) other;
			return (Arrays.equals(chromosome, castChromosome.chromosome)) && fitness == castChromosome.fitness;
		}
	}

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

    public int[] solveTSP(TSPData pd) {
    	List<Chromosome> initialPopulation = new ArrayList<>();
    	initializePopulation(initialPopulation);
    	
    	calculateFitness(initialPopulation, pd);
    	   	
    	
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 17};
    }
    
    public List<Chromosome> createNextGeneration(List<Chromosome> currentPopulation) {
    	List<Chromosome> nextGeneration = new ArrayList<>(popSize);
    	
    	while (nextGeneration.size() < 20) {
    		// make children
    		Chromosome father = getRouletteChromosome(currentPopulation);
    		
    		// should check whether mother != father
    		Chromosome mother = getRouletteChromosome(currentPopulation);
    		
    		double crossOver = randomizer.nextDouble();
    		double mutation = randomizer.nextDouble();
    		
    		Chromosome child = getFittestCandidate(father,mother);
    		
    		if (crossOver <= crossOverChance) {
    			// one-point crossover
    			child = createCrossOver(father,mother);
    		}
    		
    		if (mutation <= mutationChance) {
    			// apply mutation
    			
    		}
    		
    		nextGeneration.add(child);
    		
    	}
    	
    	return nextGeneration;
    }
    
    public Chromosome createCrossOver(Chromosome father, Chromosome mother) {
    	int crossOverPoint = randomizer.nextInt(father.chromosome.length);
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
    
    public boolean hasGen(int[] chromosome, int gen) {
		for (int i = 0; i < chromosome.length; i++) {
			if (chromosome[i] == gen) {
				return true;
			}
		}
		return false;
	}
    
    public Chromosome getFittestCandidate(Chromosome father, Chromosome mother) {
    	return (father.getFitness() <= mother.getFitness()) ? father : mother;
    }
    
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
    
    public void calculateFitness(List<Chromosome> pop, TSPData tsp) {
    	int fitnessSum = 0;
    	for (Chromosome chromosome : pop) {
    		fitnessSum += getTotalDistance(chromosome, tsp);
    	}
    	
    	for (Chromosome chromosome : pop) {
    		chromosome.setFitness(1.0d - (getTotalDistance(chromosome,tsp) / fitnessSum));
    	}
    }
    
    public int getTotalDistance(Chromosome order, TSPData tsp) {
    	int total = tsp.getStartDistances()[order.getChromosome()[0]];
    	for (int i = 0; i < order.getChromosome().length-1; i++) {
    		total += tsp.getDistances()[i][i+1];
    	}
    	total += tsp.getEndDistances()[order.getChromosome().length-1];
    	return total;
    }
    
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
