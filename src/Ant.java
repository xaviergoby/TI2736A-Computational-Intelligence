import java.util.*;

/**
 * Class that represents the ants functionality.
 */
public class Ant {
    private Maze maze;
    private Coordinate start;
    private Coordinate end;
    private Coordinate currentPosition;
    private Route route;
    private List<Coordinate> visited;
	private Stack<Direction> directionStack;

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
        this.route = new Route(start);
        this.visited = new ArrayList<>();
		this.directionStack = new Stack<>();
    }

    /**
     * Method that performs a single run through the maze by the ant.
     * @return The route the ant found through the maze.
     */
    public Route findRoute() {
    	if (route.size() > 0) return route;
        while (!currentPosition.equals(end)) move();
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
//    	System.out.println("Moved to : " + dir);
    	route.add(dir);
    	currentPosition = currentPosition.add(dir);
    	visited.add(currentPosition);
    }
    
    /**
     * This method will move the Ant. If there is just one movable direction, the ant will go that way. If the
     * ant has two ways, but one is the previous tile, it will remove that tile and proceed on to his path. If
     * there are more options, the ant will not go back but check for surrounding pheromone, calculate the chance
     * and then choose a direction.
     */
    public void move() {
    	EnumMap<Direction, Double> directions = getCompetingDirections(currentPosition);
		//System.out.println();
		//System.out.println("The amount of competing directions is: " + directions.size());

		// Backtrack the dead ends.
		if (directions.size() == 0) {
			System.out.println("Backtracking...");
			backtrack();
			return;
		}

		Direction chosenDirection = null;
		double randomNumber = new Random().nextDouble();
		//System.out.println("The random number is: " + randomNumber);
		double total = 0.0d;
		SurroundingPheromone surroundingPheromone = maze.getSurroundingPheromone(currentPosition);
		double totalSurroundingPheromone = 0.0d;
		for (Direction direction : directions.keySet()) {
			totalSurroundingPheromone += surroundingPheromone.get(direction);
		}

		//System.out.println("totalSurroundingPheromone: " + totalSurroundingPheromone);

		// Roulette wheel selection
		for (Direction direction : directions.keySet()) {
			total += (surroundingPheromone.get(direction) / totalSurroundingPheromone);
			//System.out.println("Pheromone for direction " + direction + " = " + surroundingPheromone.get(direction));
			//System.out.println("Total for direction: " + direction.toString() + " = " + total);
			if (total >= randomNumber) {
				//System.out.println("chosenDirection: " + direction.toString());
				chosenDirection = direction;
				break;
			}
		}
		System.out.println();
		System.out.println("The current position is: " + currentPosition.toString());
		System.out.println("The chosen direction is: " + chosenDirection.toString());

		// Moving to the next tile...
		route.add(chosenDirection);
		directionStack.push(chosenDirection);
		visited.add(currentPosition); // Must happen before changing currentPosition!
		currentPosition = currentPosition.add(chosenDirection);

//    	if (dirs.size() == 1) {
//    		// If you can just move to one dir, move to that dir.
//    		moveTo(dirs.get(0));
//
//    	} else {
//    		if (currentDirection != null) {
//    			dirs.remove(Direction.inverse(currentDirection));
//    			if (dirs.size() == 1) {
//    				moveTo(dirs.get(0));
//    				return;
//    			}
//    		}
//
//    		// checks if the ant hasnt been to that coordinate
//
//    		List<Direction> unvisited = getUnvisitedDirs(dirs);
//    		if (unvisited.size() > 0) {
//    			dirs = unvisited;
//    		}
//
//
//    		// from this moment on, the ant gets to choose a dir, we will prefer dirs which are close to walls
//    		// if there are directions which are close to walls, then choose between those.
//
//    		List<Direction> closeToWalls = getCloseToWalls(dirs);
//    		if (closeToWalls.size() > 0) {
//    			dirs = closeToWalls;
//    		}
//
//			moveTo(decide(getCompetingDirections(currentPosition)));
//
//    		List<Double> dirChances = getChances(dirs);
//
//    		double decision = new Random().nextDouble();
//			moveTo(dirs.get(decision));
//
//    		for (int i = 0; i < dirChances.size(); i++) {
//    			if (decision <= dirChances.get(i)) {
//    				moveTo(dirs.get(i));
//    				break;
//    			}
//    		}
//			//System.out.println(currentPosition.toString());
//    	}
    }

	/**
	 * Decide between Directions with a Roulette Wheel Selection, given their pheromone value.
	 * @param directions HashMap of Directions and their pheromone value.
	 * @return The chosen Direction.
	 */
	public Direction decide(HashMap<Direction, Double> directions) {

		Iterator<Map.Entry<Direction, Double>> iterator = directions.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Direction, Double> entry = iterator.next();
			if(isVisited(entry.getKey())){
				iterator.remove();
			}
		}

		// Remove Directions previously visited.
		List<Direction> toRemove = new ArrayList<>();
		for (HashMap.Entry<Direction, Double> entry : directions.entrySet()) {
			if (isVisited(entry.getKey())) toRemove.add(entry.getKey());
		}

		// Change Pheromone value to chance.
		for (HashMap.Entry<Direction, Double> entry : directions.entrySet()) {
			directions.put(entry.getKey(), entry.getValue() / maze.getSurroundingPheromone(currentPosition).getTotalSurroundingPheromone());
		}

		double randomNumber = new Random().nextDouble();
		double total = 0.0d;

		// Decide the Direction.
		for (HashMap.Entry<Direction, Double> entry : directions.entrySet()) {
			total += entry.getValue();
			if (total >= randomNumber)
				return entry.getKey();
		}

		// Shouldn't be reachable.
		System.err.println("The decide() couldn't decide...");
		return null;
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

    public boolean isVisited(Direction direction) {
		for (Coordinate coordinate : visited) {
			if (currentPosition.add(direction).equals(coordinate)) {
				return true;
			}
		}
		return false;
	}

    /**
     * Searches the visited for already visited Directions.
     * Greedy algorithm!
     * @param dirs The Directions to search in.
     * @return List<Direction> The unvisited Directions.
     */
    public List<Direction> getUnvisitedDirs(List<Direction> dirs) {
    	List<Direction> unvisited = new ArrayList<>();
    	for (Direction dir : dirs) {
    		for (Coordinate co : visited) {
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
    		if (getCompetingDirections(currentPosition.add(dir)).size() < 4) {
    			closeToWalls.add(dir);
    		}
    	}
    	
    	return closeToWalls;
    }

	/**
	 * Backtrack to the previous junction, so that we can choose a different direction.
	 * This works for any number of junctions deep.
	 */
	private void backtrack() {
		visited.add(currentPosition);
		while (getCompetingDirections(currentPosition).size() == 0) {
			currentPosition = currentPosition.add(Direction.inverse(directionStack.peek()));
			route.add(Direction.inverse(directionStack.pop()));
		}
		//System.out.println("position is now: " + currentPosition);
	}

	/**
	 * Returns an EnumMap<Direction, Double> containing all competing directions and their pheromone value.
	 * Competing directions are not on walls, and not already visited.
	 * @param position The position you are currently on.
	 * @return An EnumMap<Direction, Double> containing all competing directions and their pheromone value.
	 */
	private EnumMap<Direction, Double> getCompetingDirections(Coordinate position) {
		EnumMap<Direction, Double> directions = new EnumMap<>(Direction.class);
    	
    	if (maze.isPassable(position.add(Direction.North)) && !visited.contains(position.add(Direction.North)))
			directions.put(Direction.North, maze.getPheromone(position.add(Direction.North)));

    	if (maze.isPassable(position.add(Direction.East)) && !visited.contains(position.add(Direction.East)))
			directions.put(Direction.East, maze.getPheromone(position.add(Direction.East)));

    	if (maze.isPassable(position.add(Direction.South)) && !visited.contains(position.add(Direction.South)))
			directions.put(Direction.South, maze.getPheromone(position.add(Direction.South)));

    	if (maze.isPassable(position.add(Direction.West)) && !visited.contains(position.add(Direction.West)))
			directions.put(Direction.West, maze.getPheromone(position.add(Direction.West)));
    	
    	return directions;
    }
}

