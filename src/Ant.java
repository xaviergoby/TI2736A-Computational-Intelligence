import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

/**
 * Class that represents the ants functionality.
 */
public class Ant {
    private Maze maze;
    private Coordinate start;
    private Coordinate end;
    private Coordinate currentPosition;
    private static Random rand;    

    /**
     * Constructor for ant taking a Maze and PathSpecification.
     * @param maze Maze the ant will be running in.
     * @param spec The path specification consisting of a start coordinate and an end coordinate.
     */
    public Ant(Maze maze, PathSpecification spec) {
        this.maze = maze;
        this.start = spec.getStart();
        this.end = spec.getEnd();
        this.currentPosition = start;
        if (rand == null) {
            rand = new Random();
        }
    }

    /**
     * Method that performs a single run through the maze by the ant.
     * @return The route the ant found through the maze.
     */
    public Route findRoute() {
        Route route = new Route(start);
        return route;
    }
    
    /**
     * get currentPosition
     */
    public Coordinate getCoordinates() {
    	return currentPosition;
    }
    
    /**
     * set currentPosiont
     */
    public void setCoordinates(Coordinate currentPosition) {
    	this.currentPosition = currentPosition;
    }
    
    /**
     * move to method
     */
    public void moveTo(Direction dir) {
    	currentPosition = currentPosition.add(dir);
    }
    
    public void move() {
    	List<Direction> dirs = getMovableDirs();
    	
    	if (dirs.size() == 1) {
    		// If you can just move to one dir, move to that dir.
    		moveTo(dirs.get(0));
    	} else {
    		List<Double> dirChances = new ArrayList<Double>();
    		for (Direction dir : dirs) {
    			
    		}
    	}
    }
    
    public List<Direction> getMovableDirs() {
    	
    	List<Direction> dirs = new ArrayList<>();
    	
    	if (maze.isPassable(currentPosition.add(Direction.North))) {
    		dirs.add(Direction.North);
    	} else if (maze.isPassable(currentPosition.add(Direction.East))) {
    		dirs.add(Direction.East);
    	} else if (maze.isPassable(currentPosition.add(Direction.South))) {
    		dirs.add(Direction.South);
    	} else if (maze.isPassable(currentPosition.add(Direction.West))) {
    		dirs.add(Direction.West);
    	}
    	
    	return dirs;
    }
}

