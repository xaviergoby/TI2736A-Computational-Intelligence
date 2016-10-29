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
	private boolean[][] visitedMaze;
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
		this.visitedMaze = new boolean[maze.width][maze.length];
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
     * This method will move the Ant. If there is just one movable direction, the ant will go that way. If the
     * ant has two ways, but one is the previous tile, it will remove that tile and proceed on to his path. If
     * there are more options, the ant will not go back but check for surrounding pheromone, calculate the chance
     * and then choose a direction.
     */
    public void move() {
		EnumMap<Direction, Double> directions = getCompetingDirections(currentPosition);

		// Backtrack the dead ends.
		if (directions.size() == 0) {
			backtrack();
			return;
		}
		
		double randomNumber = new Random().nextDouble();
		double total = 0.0d;
		SurroundingPheromone surroundingPheromone = maze.getSurroundingPheromone(currentPosition);
		double totalSurroundingPheromone = 0.0d;
		for (Direction direction : directions.keySet()) {
			totalSurroundingPheromone += surroundingPheromone.get(direction);
		}

		// Roulette wheel selection
		for (Direction direction : directions.keySet()) {
			total += (surroundingPheromone.get(direction) / totalSurroundingPheromone);
			if (total >= randomNumber) {
				// We got a hit!
				// Moving to the next tile...
				route.add(direction);
				directionStack.push(direction);
				visitedMaze[currentPosition.getX()][currentPosition.getY()] = true; // Must happen before changing currentPosition!
				currentPosition = currentPosition.add(direction);
				break;
			}
		}
	}

	/**
	 * Backtrack to the previous junction, so that we can choose a different direction.
	 * This works for any number of junctions deep.
	 */
	private void backtrack() {
		visitedMaze[currentPosition.getX()][currentPosition.getY()] = true;
		int counter = 0;
		while (getCompetingDirections(currentPosition).size() == 0) {
			counter++;
			currentPosition = currentPosition.subtract(directionStack.pop());
		}
		for (int i = 0; i < counter; i++) {
			route.removeLast();
		}
	}

	/**
	 * Returns an EnumMap<Direction, Double> containing all competing directions and their pheromone value.
	 * Competing directions are not on walls, and not already visited.
	 * @param position The position you are currently on.
	 * @return An EnumMap<Direction, Double> containing all competing directions and their pheromone value.
	 */
	private EnumMap<Direction, Double> getCompetingDirections(Coordinate position) {
		EnumMap<Direction, Double> directions = new EnumMap<>(Direction.class);
    	
    	if (maze.isPassable(position.add(Direction.North)) && !visitedMaze[currentPosition.getX()][currentPosition.getY()-1])
			directions.put(Direction.North, maze.getPheromone(position.add(Direction.North)));

    	if (maze.isPassable(position.add(Direction.East)) && !visitedMaze[currentPosition.getX()+1][currentPosition.getY()])
			directions.put(Direction.East, maze.getPheromone(position.add(Direction.East)));

    	if (maze.isPassable(position.add(Direction.South)) && !visitedMaze[currentPosition.getX()][currentPosition.getY()+1])
			directions.put(Direction.South, maze.getPheromone(position.add(Direction.South)));

    	if (maze.isPassable(position.add(Direction.West)) && !visitedMaze[currentPosition.getX()-1][currentPosition.getY()])
			directions.put(Direction.West, maze.getPheromone(position.add(Direction.West)));
    	
    	return directions;
    }
}

