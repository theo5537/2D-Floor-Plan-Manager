import java.io.Serializable;
public class Opening implements Serializable{
    private static final long serialVersionUID = 1L;
    private int relativeCenter; // Center position relative to the wall
    private int length;
    private String direction; // "north", "south", "east", "west"
    private boolean isDoor; // Flag to distinguish between door and window

    // Constructor for creating an opening (door or window)
    public Opening(int relativeCenter, int length, String direction, boolean isDoor) {
        this.relativeCenter = relativeCenter;
        this.length = length;
        this.direction = direction.toLowerCase();
        this.isDoor = isDoor; // true for door, false for window
    }

    public int getRelativeCenter() {
        return relativeCenter;
    }

    public void setRelativeCenter(int relativeCenter) {
        this.relativeCenter = relativeCenter;
    }

    public int getLength() {
        return length;
    }

    public String getDirection() {
        return direction;
    }

    public boolean isDoor() {
        return isDoor;
    }
}


