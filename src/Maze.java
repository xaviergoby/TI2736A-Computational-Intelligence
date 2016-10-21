import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Class that holds all the maze data. This means the pheromones, the open and blocked tiles in the system as
 * well as the starting and end coordinates.
 */
public class Maze {

    private final double INITIAL_PHEROMONE_VALUE = 1;

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
    public Maze(int[][] walls, int width, int length) {
        this.walls = walls;
        this.length = length;
        this.width = width;
        initializePheromones(INITIAL_PHEROMONE_VALUE);
    }

    /**
     * Initialize the maze to hold pheromoneValue in all blocks
     * @param pheromoneValue The amount of pheromones that should be in the maze.
     */
    private void initializePheromones(double pheromoneValue) {
        this.pheromones = new double[width][length];
        for (double[] row : pheromones) {
        	Arrays.fill(row, pheromoneValue);
        }
    }

    /**
     * Reset the maze for a new shortest path problem.
     */
    public void reset() {
        initializePheromones(INITIAL_PHEROMONE_VALUE);
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
    
    public void addPheromoneToCoordinate(Coordinate co, double q) {
    	pheromones[co.getX()][co.getY()] += q;
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
    private double getPheromone(Coordinate pos) {
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
    public static Maze createMaze(String filePath) throws FileNotFoundException {
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
        return new Maze(mazeLayout, width, length);
    }
}
