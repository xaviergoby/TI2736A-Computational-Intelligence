import java.util.ArrayList;
import java.util.Collections;
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
    private Direction currentDir;
    private Route route;
    private List<Coordinate> memory;

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
        this.currentDir = null;
        this.route = new Route(start);
        this.memory = new ArrayList<>();
        
        memory.add(currentPosition);
        
        if (rand == null) {
            rand = new Random();
        }
    }

    /**
     * Method that performs a single run through the maze by the ant.
     * @return The route the ant found through the maze.
     */
    public Route findRoute() {
    	if (route.size() > 0) {
    		return route;
    	}
    	
        while (!(currentPosition.equals(end))) {
        	move();
        }
        
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
    	this.currentDir = null;
    }
    
    /**
     * move to method
     */
    public void moveTo(Direction dir) {
//    	System.out.println("Moved to : " + dir);
    	route.add(dir);
    	currentDir = dir;
    	currentPosition = currentPosition.add(dir);
    	memory.add(currentPosition);
    }
    
    /**
     * This method will move the Ant. If there is just one movable direction, the ant will go that way. If the
     * ant has two ways, but one is the previous tile, it will remove that tile and proceed on to his path. If
     * there are more options, the ant will not go back but check for surrounding pheromone, calculate the chance
     * and then choose a direction.
     */
    public void move() {
    	List<Direction> dirs = getMovableDirs(currentPosition);
    	
    	if (dirs.size() == 1) {
    		// If you can just move to one dir, move to that dir.
    		moveTo(dirs.get(0));
    		
    	} else {
    		if (currentDir != null) {
    			dirs.remove(Direction.inverse(currentDir));
    			if (dirs.size() == 1) {
    				moveTo(dirs.get(0));
    				return;
    			}
    		}
    		
    		// checks if the ant hasnt been to that coordinate
    		
    		List<Direction> unvisited = getUnvisitedDirs(dirs);
    		if (unvisited.size() > 0) {
    			dirs = unvisited;
    		}
    		
    		
    		// from this moment on, the ant gets to choose a dir, we will prefer dirs which are close to walls
    		// if there are directions which are close to walls, then choose between those.
    		
    		List<Direction> closeToWalls = getCloseToWalls(dirs);
    		if (closeToWalls.size() > 0) {
    			dirs = closeToWalls;
    		}
    		
    		List<Double> dirChances = getChances(dirs);
    		
    		double decision = new Random().nextDouble();
    		
    		for (int i = 0; i < dirChances.size(); i++) {
    			if (decision <= dirChances.get(i)) {
    				moveTo(dirs.get(i));
    				break;
    			}
    		}
    	}
    }
    
    public List<Double> getChances(List<Direction> dirs) {
    	List<Double> dirChances = new ArrayList<Double>();
		
		SurroundingPheromone surrPher = maze.getSurroundingPheromone(currentPosition);
		for (Direction dir : dirs) {
			double pheromoneOnDir = surrPher.get(dir);
			double totalPheromone = surrPher.getTotalSurroundingPheromone();
			double chooseChance = pheromoneOnDir / totalPheromone;
			dirChances.add(chooseChance);
		}
		
		Collections.sort(dirChances);
		return dirChances;
    }
    
    /**
     * Searches the memory for already visited Directions.
     * @param dirs The Directions to search in.
     * @return List<Direction> The unvisited Directions.
     */
    public List<Direction> getUnvisitedDirs(List<Direction> dirs) {
    	List<Direction> unvisited = new ArrayList<>();
    	for (Direction dir : dirs) {
    		for (Coordinate co : memory) {
    			if (currentPosition.add(dir).equals(co)) {
    				break;
    			}
    		}
    		unvisited.add(dir);
    	}
    	return unvisited;
    }
    
    /**
     * Gets the Directions which are close to walls.
     * @param dirs The Directions to search in.
     * @return List<Direction> The Directions that are close to walls.
     */
    public List<Direction> getCloseToWalls(List<Direction> dirs) {
    	List<Direction> closeToWalls = new ArrayList<>();
    	
    	for (Direction dir : dirs) {
    		if (getMovableDirs(currentPosition.add(dir)).size() < 4) {
    			closeToWalls.add(dir);
    		}
    	}
    	
    	return closeToWalls;
    }
    
    public List<Direction> getMovableDirs(Coordinate pos) {
    	
    	List<Direction> dirs = new ArrayList<>();
    	
    	if (maze.isPassable(pos.add(Direction.North))) {
    		dirs.add(Direction.North);
    	} 
    	if (maze.isPassable(pos.add(Direction.East))) {
    		dirs.add(Direction.East);
    	}
    	if (maze.isPassable(pos.add(Direction.South))) {
    		dirs.add(Direction.South);
    	} 
    	if (maze.isPassable(pos.add(Direction.West))) {
    		dirs.add(Direction.West);
    	}
    	
    	return dirs;
    }
}

