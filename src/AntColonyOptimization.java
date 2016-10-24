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
    public static boolean DEBUG = true;
 
    public AntColonyOptimization(Maze maze, int antsPerGeneration, int numberOfGenerations, double qualityFactor, double evaporationFactor) {
        this.maze = maze;
        this.antsPerGeneration = antsPerGeneration;
        this.numberOfGenerations = numberOfGenerations;
        this.qualityFactor = qualityFactor;
        this.evaporationFactor = evaporationFactor;
    }

    /**
     * Finds the shortest route by applying ACO.
     * Loops through all ants in a generation and:
     *      Finds the shortest route between them
     *      Applies pheromones over all routes
     *      Evaporates the maze
     * This happens for all generations, then the shortest route is returned.
     * @param spec {@link PathSpecification} of the route we wish to optimize.
     * @return ACO optimized {@link Route}.
     */
    public Route findShortestRoute(PathSpecification spec) {
        List<Route> routes = new ArrayList<>();
        Route shortestRoute = new Ant(maze, spec).findRoute();
        
        for (int generation = 1; generation <= numberOfGenerations; generation++) {
        	if (DEBUG) System.out.println("Generation: " + generation);
            for (int ant = 1; ant < antsPerGeneration; ant++) {
                if (DEBUG) System.out.println("Ant: " + ant);
                
        		Ant currentAnt = new Ant(maze, spec);
                Route route = currentAnt.findRoute();
                routes.add(route);
        	}
            for (Route route: routes) if (route.shorterThan(shortestRoute)) shortestRoute = route;
        	maze.addPheromoneRoutes(routes, qualityFactor);
            routes.clear();
            maze.evaporate(evaporationFactor); //Evaporate after every generation
        }
        return shortestRoute;
    }

    /**
     * Driver function for Assignment 1.
     * Easy solution: gen = 1000, ants = 50
     */
    public static void main(String[] args) throws FileNotFoundException {
        String mazeName = "easy";
        int antsPerGeneration = 100;
        int numberOfGenerations = 100;
        double qualityFactor = 2000;
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
