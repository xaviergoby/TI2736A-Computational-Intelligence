import java.util.Arrays;
import java.util.Random;

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
	    public void shuffle() {
	        int n = chromosome.length;
	        for (int i = 0; i < n; i++) {
	            int r = i + (int) (Math.random() * (n - i));
	            int swap = chromosome[r];
	            chromosome[r] = chromosome[i];
	            chromosome[i] = swap;
	        }
	    }
	    
	    public void mutate(Random rand) {
	    	int mutationPoint = rand.nextInt(chromosome.length);
	    	int secondPoint = rand.nextInt(chromosome.length);
	    	while (mutationPoint == secondPoint) {
	    		secondPoint = rand.nextInt(chromosome.length);
	    	}
	    	int temp = chromosome[mutationPoint];
	    	chromosome[mutationPoint] = chromosome[secondPoint];
	    	chromosome[secondPoint] = temp;
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