import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the first assignment. Finds shortest path between two points in a maze according to a specific
 * path specification.
 */
public class AntColonyOptimization {
    private int antsPerGeneration;
    private int numberOfGenerations;
    private double qualityFactor;
    private double evaporationFactor;
    private Maze maze;
    private List<Ant> ants;
 
    public AntColonyOptimization(Maze maze, int antsPerGeneration, int numberOfGenerations, double qualityFactor, double evaporationFactor) {
        this.maze = maze;
        this.antsPerGeneration = antsPerGeneration;
        this.numberOfGenerations = numberOfGenerations;
        this.qualityFactor = qualityFactor;
        this.evaporationFactor = evaporationFactor;
        this.ants = new ArrayList<>();
    }

    /**
     * Loop that starts the shortest path process
     * @param spec Spefication of the route we wish to optimize
     * @return ACO optimized route
     */
    public Route findShortestRoute(PathSpecification spec) {
        maze.reset();
        for (int generation = 0; generation < numberOfGenerations; generation++) {
        	for (int ant = 0; ant < antsPerGeneration; ant++) {
        		Ant currentAnt = new Ant(maze,spec);
        		currentAnt.findRoute();
        		ants.add(currentAnt);
        		maze.evaporate(evaporationFactor);
        	}
        }
        
        Ant bestAnt = ants.get(0);
        
        for (Ant a : ants) {
        	if (a.findRoute().size() < bestAnt.findRoute().size()) {
        		bestAnt = a;
        	}
        }
        
        return bestAnt.findRoute();
    }

    /**
     * Driver function for Assignment 1.
     * Easy solution: gen = 1000, ants = 50
     */
    public static void main(String[] args) throws FileNotFoundException {
        String mazeName = "easy";
        int antsPerGeneration = 50;
        int numberOfGenerations = 500;
        double qualityFactor = 1600;
        double evaporationFactor = 0.1;
        Maze maze = Maze.createMaze("./data/"+mazeName+" maze.txt");
        PathSpecification spec = PathSpecification.readCoordinates("./data/"+mazeName+" coordinates.txt");
        AntColonyOptimization aco = new AntColonyOptimization(maze, antsPerGeneration, numberOfGenerations, qualityFactor, evaporationFactor);
        long startTime = System.currentTimeMillis();
        Route shortestRoute = aco.findShortestRoute(spec);
        System.out.println("Time taken: " + ((System.currentTimeMillis() - startTime) / 1000.0));
        shortestRoute.writeToFile("./data/"+mazeName+" solution.txt");
        System.out.println(shortestRoute.size());
    }
}
