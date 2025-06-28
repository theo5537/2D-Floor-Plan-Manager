import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.io.Serializable;
public class Room implements Serializable{
    private static final long serialVersionUID = 1L;

    private int x, y, width, height;
    private Color color;
    private List<Opening> openings; // List to store both doors and windows

    // Define grid boundaries
    private static final int GRID_WIDTH = 1200;  // Width of the grid
    private static final int GRID_HEIGHT = 800;   // Height of the grid

    public Room(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.openings = new ArrayList<>();
        enforceBounds(); // Ensure the room starts within bounds
    }
    public boolean isBathroom() {
        return color.equals(Color.GREEN);
    }

    public boolean isBedroom() {
        return color.equals(Color.BLUE);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        enforceBounds();
    }

    // Getter and setter for y position
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        enforceBounds();
    }
    // Getter and setter for width
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        enforceBounds();
    }

    // Getter and setter for height
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        enforceBounds();
    }

    // Getter and setter for color
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // Getter and setter for openings (doors and windows)
    public List<Opening> getOpenings() {
        return openings;
    }

    public void setOpenings(List<Opening> openings) {
        this.openings = openings;
    }
    // Add an opening (either door or window)
    public void addOpening(Opening opening) {
        openings.add(opening);
    }

    // Draw all openings (both doors and windows) in the room
    public void drawOpenings(Graphics g) {
        for (Opening opening : openings) {
            if (opening.isDoor()) {
                drawDoor(g, opening);
            } else {
                drawWindow(g, opening);
            }
        }
    }

    public boolean isOutsideWall(int x, int y) {
        // Check if the chosen point is near an outside wall
        int tolerance = 35; // Adjust for wall thickness
        boolean leftWall = (x >= this.x - tolerance && x <= this.x + tolerance);
        boolean rightWall = (x >= this.x + this.width - tolerance && x <= this.x + this.width + tolerance);
        boolean topWall = (y >= this.y - tolerance && y <= this.y + tolerance);
        boolean bottomWall = (y >= this.y + this.height - tolerance && y <= this.y + this.height + tolerance);

        // Return true if any of these walls is an outside wall
        return leftWall || rightWall || topWall || bottomWall;
    }


    // Method to draw doors
    private void drawDoor(Graphics g, Opening door) {
        g.setColor(Color.LIGHT_GRAY); // Set color to light gray for the door

        // Calculate the absolute position of the door based on its relative center and the room's position
        int[] doorPosition = getAbsoluteOpeningPosition(door);

        int doorX = doorPosition[0];
        int doorY = doorPosition[1];

        // Door width is fixed to 3
        int doorWidth = 6;

        // Draw the door based on its direction
        switch (door.getDirection()) {
            case "north":
            case "south":
                g.fillRect(doorX, doorY - doorWidth / 2, door.getLength(), doorWidth);
                break;
            case "east":
            case "west":
                g.fillRect(doorX - doorWidth / 2, doorY, doorWidth, door.getLength());
                break;
        }
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        enforceBounds(); // Ensure the room stays within bounds after setting the position
    }


    // Method to draw windows (dotted line)
    private void drawWindow(Graphics g, Opening window) {
        g.setColor(Color.BLUE); // Set color to blue for the window

        // Example drawing code for a dotted line:
        float[] dash = {10.0f};
        BasicStroke dashed = new BasicStroke(6.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(dashed);

        // Calculate the absolute position of the window
        int[] windowPosition = getAbsoluteOpeningPosition(window);

        int windowX = windowPosition[0];
        int windowY = windowPosition[1];

        // Draw the window based on its direction
        switch (window.getDirection()) {
            case "north":
            case "south":
                g2d.drawLine(windowX, windowY - 3, windowX + window.getLength(), windowY - 3); // Dotted line for window
                break;
            case "east":
            case "west":
                g2d.drawLine(windowX - 3, windowY, windowX - 3, windowY + window.getLength()); // Dotted line for window
                break;
        }
    }
    // Calculate the absolute position of any opening (door or window) in the room
    public int[] getAbsoluteOpeningPosition(Opening opening) {
        int absoluteX = 0, absoluteY = 0;

        switch (opening.getDirection()) {
            case "north": // Top wall
                absoluteX = x + opening.getRelativeCenter() - opening.getLength() / 2;
                absoluteY = y;
                break;
            case "south": // Bottom wall
                absoluteX = x + opening.getRelativeCenter() - opening.getLength() / 2;
                absoluteY = y + height;
                break;
            case "east": // Right wall
                absoluteX = x + width;
                absoluteY = y + opening.getRelativeCenter() - opening.getLength() / 2;
                break;
            case "west": // Left wall
                absoluteX = x;
                absoluteY = y + opening.getRelativeCenter() - opening.getLength() / 2;
                break;
        }

        return new int[]{absoluteX, absoluteY};
    }
    public void setSize(int width, int height) {
        if (width < 50 || height < 50) {
            System.out.println("Room size is too small.");
            return; // Prevent resizing to a very small size
        }

        // Scale door positions relative to the new size
        for (Opening opening : openings) {
            switch (opening.getDirection()) {
                case "north":
                case "south":
                    if (opening.getRelativeCenter() > width) {
                        opening.setRelativeCenter(width / 2); // Reset to center if out of bounds
                    }
                    break;
                case "east":
                case "west":
                    if (opening.getRelativeCenter() > height) {
                        opening.setRelativeCenter(height / 2); // Reset to center if out of bounds
                    }
                    break;
            }
        }

        this.width = width;
        this.height = height;
        enforceBounds(); // Check bounds after resizing
    }


    // Collision Check
    public boolean intersects(Room other) {
        return x < other.getX() + other.getWidth() &&
                x + width > other.getX() &&
                y < other.getY() + other.getHeight() &&
                y + height > other.getY();
    }

    // Ensure the room stays within the defined grid boundaries
    private void enforceBounds() {
        if (x < 0) {
            x = 0;
        } else if (x + width > GRID_WIDTH) {
            x = GRID_WIDTH - width;
        }

        if (y < 0) {
            y = 0;
        } else if (y + height > GRID_HEIGHT) {
            y = GRID_HEIGHT - height;
        }
    }

    @Override
    public String toString() {
        return "Room{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", color=" + color +
                "openings="+openings+
                '}';
    }
}
