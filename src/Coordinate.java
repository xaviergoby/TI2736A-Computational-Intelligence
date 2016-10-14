import java.io.Serializable;

/**
 * Class representing a coordinate
 */
public class Coordinate implements Serializable {
    private static final long serialVersionUID = 0L;
    private final int x;
    private final int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Add a coordinate to this coordiante
     * @param other the other coordinate to be added
     * @return the result coordinate
     */
    public Coordinate add(Coordinate other) {
        return new Coordinate(this.x + other.x, this.y + other.y);
    }

    /**
     * Find Coordinate based on a direction for a unit move
     * @param dir Direction of unit move
     * @return result coordinate
     */
    public Coordinate add(Direction dir) {
        return add(Direction.dirToCoordinateDelta(dir));
    }

    /**
     * Substract a coordinate from the current coordinate
     * @param other the to be subtracted coordinate
     * @return result coordinate
     */
    public Coordinate subtract(Coordinate other) {
        return new Coordinate(this.x - other.x, this.y - other.y);
    }

    /**
     * Find Coordinate based on a inverted direction for a unit move
     * @param Direction of unit move
     * @return result coordinate
     */
    public Coordinate subtract(Direction dir) {
        return subtract(Direction.dirToCoordinateDelta(dir));
    }

    /**
     * String representation of coordinate
     * @return String representation of coordinate
     */
    public String toString() {
        return x + ", " + y;
    }

    /**
     * Equals method for Coordinate
     * @param other Other Coordinate to check
     * @return boolean whether they're equal
     */
    public boolean equals(Object other) {
        if (!(other instanceof Coordinate)) {
            return false;
        } else {
            Coordinate otherC = (Coordinate) other;
            return this.x == otherC.x && this.y == otherC.y;
        }
    }

    /**
     * Check whether a point lies between a x range with [low,up)
     * @param low lower bound
     * @param up upper bound (non-inclusive)
     * @return boolean whether point lies between two coordinates
     */
    public boolean xBetween(int low, int up) {
        return low <= x && x < up;
    }

     /**
     * Check whether a point lies between a y range with [low,up)
     * @param low lower bound
     * @param up upper bound (non-inclusive)
     * @return boolean whether point lies between two coordinates
     */
    public boolean yBetween(int low, int up) {
        return low <= y && y < up;
    }

    /**
     * x getter
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * y getter
     * @return y
     */
    public int getY() {
        return y;
    }
}
