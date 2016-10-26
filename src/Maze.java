import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

/**
 * Class that holds all the maze data. This means the pheromones, the open and blocked tiles in the system as
 * well as the starting and end coordinates.
 */
public class Maze {

    private int width;
    private int length;
    private int[][] walls;
    private double[][] pheromones;

    /**
     * Constructor of a maze
     * @param walls int array of tiles accessible (1) and non-accessible (0)
     * @param width width of Maze (horizontal)
     * @param length length of Maze (vertical)
     */
    public Maze(int[][] walls, int width, int length, double initialPheromoneValue) {
        this.walls = walls;
        this.length = length;
        this.width = width;
        initializePheromones(initialPheromoneValue);
    }

    /**
     * Initialize the maze to hold pheromoneValue in all accessible tiles.
     * This will exclude any accessible tile that is not at least surrounded by one wall.
     *      This makes sure that the inside of courtyards are not initialized.
     *      This exclusion will not happen at the borders of the maze.
     * @param pheromoneValue The amount of pheromones that should be in the maze.
     */
    private void initializePheromones(double pheromoneValue) {
        long startTime = System.currentTimeMillis();
        this.pheromones = new double[width][length];
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[i].length; j++) {
                if (walls[i][j] == 1) {
                    pheromones[i][j] = pheromoneValue;
                }
            }
        }
        System.out.println("[Maze] Initialization completed!");
        System.out.println("[Maze] Time taken: " + ((System.currentTimeMillis() - startTime) / 1000.0));
        if (AntColonyOptimization.DEBUG) {
            System.out.println("[Maze] Pheromone maze:");
            System.out.println(pheromonesToString());
        }
    }

    /**
     * Update the pheromones along a certain route according to a certain Q
     * @param r The route of the ants
     * @param Q Normalization factor for amount of dropped pheromone
     */
    public void addPheromoneRoute(Route r, double Q) {
        //Compute pheromone to add to each coordinate
        double newPheromone = Q/r.size();

        Coordinate current = r.getStart(); //Begin at the start coordinate
        for (Direction dir : r.getRoute()) {
            pheromones[current.getX()][current.getY()] += newPheromone; //Add the pheromone to the coordinate
            current = current.add(dir); //Add the direction to the coordinate to get to a new coordinate
        }
    }

    /**
     * Update pheromones for a list of routes
     * @param routes A list of routes
     * @param Q Normalization factor for amount of dropped pheromone
     */
    public void addPheromoneRoutes(List<Route> routes, double Q) {
        for (Route r : routes) {
            addPheromoneRoute(r, Q);
        }
    }

    /**
     * Evaporate pheromone
     * @param rho evaporation factor
     */
    public void evaporate(double rho) {
    	for (int i = 0; i < pheromones.length; i++) {
    		for (int j = 0; j < pheromones[i].length; j++) {
    			pheromones[i][j] = (1 - rho) * pheromones[i][j];
    		}
    	}
    }
    
    /**
     * Check passability of tile.
     * @param coordinate The coordinate.
     * @return boolean The passability.
     */
    public boolean isPassable(Coordinate coordinate) {
    	if (inBounds(coordinate)) {
    		return (walls[coordinate.getX()][coordinate.getY()] == 1);
    	}
    	return false;
    }

    /**
     * Returns a the amount of pheromones on the neighbouring positions (N/S/E/W).
     * @param position The position to check the neighbours of.
     * @return the pheromones of the neighbouring positions.
     */
    public SurroundingPheromone getSurroundingPheromone(Coordinate position) {
        double north = getPheromone(position.add(Direction.North));
        double south = getPheromone(position.add(Direction.South));
        double east = getPheromone(position.add(Direction.East));
        double west = getPheromone(position.add(Direction.West));

        return new SurroundingPheromone(north, east, south, west);
    }

    /**
     * Pheromone getter for a specific position. If the position is not in bounds returns 0
     * @param pos Position coordinate
     * @return pheromone at point
     */
    public double getPheromone(Coordinate pos) {
    	if (inBounds(pos)) {
    		return pheromones[pos.getX()][pos.getY()];
    	} else {
    		return 0.0d;
    	}
    }


    /**
     * Check whether a coordinate lies in the current maze.
     * @param position The position to be checked
     * @return Whether the position is in the current maze
     */
    private boolean inBounds(Coordinate position) {
        return position.xBetween(0, width) && position.yBetween(0, length);
    }

    /**
     * Representation of Maze's pheromones as defined by the input file format.
     * @return String representation
     */
    public String pheromonesToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(width);
        sb.append(' ');
        sb.append(length);
        sb.append(" \n");
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++ ) {
                if (pheromones[x][y]!=0) {
                    sb.append("X");
                } else {
                    sb.append(" ");
                }
                sb.append(' ');
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    /**
     * Representation of Maze as defined by the input file format.
     * @return String representation
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(width);
        sb.append(' ');
        sb.append(length);
        sb.append(" \n");
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++ ) {
                sb.append(walls[x][y]);
                sb.append(' ');
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Method that builds a mze from a file
     * @param filePath Path to the file
     * @return A maze object with pheromones initialized to 0's inaccessible and 1's accessible.
     */
    public static Maze createMaze(String filePath, double initialPheromoneValue) throws FileNotFoundException {
        Scanner scan = new Scanner(new FileReader(filePath));
        int width = scan.nextInt();
        int length = scan.nextInt();
        int[][] mazeLayout = new int[width][length];
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                mazeLayout[x][y] = scan.nextInt();
            }
        }
        scan.close();
        return new Maze(mazeLayout, width, length, initialPheromoneValue);
    }
}
