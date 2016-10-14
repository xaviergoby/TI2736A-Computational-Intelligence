import java.util.EnumMap;

/**
 * Enum representing the directions an ant can take.
 */
public enum Direction {
    North,
    East,
    West,
    South;

    private static Coordinate northVector = new Coordinate(0,-1);
    private static Coordinate southVector = new Coordinate(0,1);
    private static Coordinate westVector = new Coordinate(-1,0);
    private static Coordinate eastVector = new Coordinate(1,0);
    private static EnumMap<Direction, Coordinate> dirToCoordinateDeltaMap = buildDirToCoordinateDelta();

    private static EnumMap<Direction, Coordinate> buildDirToCoordinateDelta() {
        EnumMap<Direction, Coordinate> map = new EnumMap<>(Direction.class);
        map.put(Direction.East, eastVector);
        map.put(Direction.West, westVector);
        map.put(Direction.North, northVector);
        map.put(Direction.South, southVector);
        return map;
    }

    public static Coordinate dirToCoordinateDelta(Direction dir) {
        return dirToCoordinateDeltaMap.get(dir);
    }

    public static int dirToInt(Direction dir) {
        switch(dir) {
            case North:
                return 1;
            case South:
                return 3;
            case East:
                return 0;
            case West:
                return 2;
            default:
                throw new IllegalArgumentException("Case statement does not match all possible values");
        }
    }
}
