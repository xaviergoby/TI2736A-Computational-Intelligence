import java.util.EnumMap;
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
    //Double for pheromone drop
    private pheromoneDrop;
    

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
    public Coordinate getCoordinates(){
    	return currentPosition;
    }
    
    /**
     * set currentPosiont
     */
    public void setCoordinates(Coordinte currentPosition){
    	this.currentPosition = currentPosition;
    }
    
    /**
     * move to method
     */
    public void moveTo(Direction dir){
    	int newX = currentPosition.getXcoordinate().add(dir.getDeltaX());
    	int newY = currentPosiotn.getYcoordinate().add(dir.getDeltaY());
    	currentPosition.setCoordinates(newX,newY);
    }
    
    /**
     * getPheromonedrop // KAN DIT???
     */
    public double getPheromoneDrop(){
    	return pheromoneDrop.getPheromone(currentposition)
    }
}

