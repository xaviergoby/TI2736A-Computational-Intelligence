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

    public AntColonyOptimization(Maze maze, int antsPerGeneration, int numberOfGenerations, double qualityFactor, double evaporationFactor) {
        this.maze = maze;
        this.antsPerGeneration = antsPerGeneration;
        this.numberOfGenerations = numberOfGenerations;
        this.qualityFactor = qualityFactor;
        this.evaporationFactor = evaporationFactor;
    }

    /**
     * Loop that starts the shortest path process
     * @param spec Spefication of the route we wish to optimize
     * @return ACO optimized route
     */
    public Route findShortestRoute(PathSpecification spec) {
        maze.reset();
        Route shortest = null;
        for (int generation = 1; generation <= numberOfGenerations; generation++) {
            List<Route> routes = new ArrayList<>();
        	for (int ant = 0; ant < antsPerGeneration; ant++) {
        		routes.add(new Ant(maze, spec).findRoute());
        	}
            maze.evaporate(evaporationFactor);
            maze.addPheromoneRoutes(routes, qualityFactor);
            if(generation == numberOfGenerations) {
                shortest = routes.get(antsPerGeneration-1);
            }
        }
        return shortest;
    }

    /**
     * Driver function for Assignment 1
     */
    public static void main(String[] args) throws FileNotFoundException {
        int antsPerGeneration = 1;
        int numberOfGenerations = 1;
        double qualityFactor = 1600;
        double evaporationFactor = 0.1;
        Maze maze = Maze.createMaze("./data/easy maze.txt");
        PathSpecification spec = PathSpecification.readCoordinates("./data/easy coordinates.txt");
        AntColonyOptimization aco = new AntColonyOptimization(maze, antsPerGeneration, numberOfGenerations, qualityFactor, evaporationFactor);
        long startTime = System.currentTimeMillis();
        Route shortestRoute = aco.findShortestRoute(spec);
        System.out.println("Time taken: " + ((System.currentTimeMillis() - startTime) / 1000.0));
        shortestRoute.writeToFile("./data/easy solution.txt");
        System.out.println(shortestRoute.size());
    }
}
