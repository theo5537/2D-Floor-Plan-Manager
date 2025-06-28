import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
public class Furniture implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x, y; // Position
    private int width, height; // Size
    private transient BufferedImage image; // Image for furniture
    private String type; // Type of furniture
    private int rotation = 0; // Rotation angle in degrees

    public Furniture(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;

        loadImage(); // Load the image in the constructor as well
        setDimensions(); // Set dimensions for the furniture type
    }

    // Method to load the image
    private void loadImage() {
        String imagePath = FurnitureImagePaths.getImagePath(type);
        if (imagePath != null) {
            try {
                this.image = ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                System.err.println("Failed to load image for " + type + ": " + e.getMessage());
            }
        } else {
            System.err.println("No image path found for type: " + type);
        }
    }

    // Method to set the dimensions
    private void setDimensions() {
        Dimension dimension = FurnitureDimensions.getDimensions(type);
        if (dimension != null) {
            this.width = dimension.width;
            this.height = dimension.height;
        } else {
            // Default size if not specified
            this.width = 50; // Fallback size
            this.height = 50; // Fallback size
        }
    }

    // Custom deserialization method
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject(); // Deserialize the other fields

        loadImage(); // Reload the image after deserialization
        setDimensions(); // Ensure the dimensions are set after deserialization
    }

    // Getters and setters, and other methods...

    public void draw(Graphics g) {
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.rotate(Math.toRadians(rotation), x + width / 2, y + height / 2);

            // Draw the image with the specific width and height
            g2d.drawImage(image, x, y, width, height, null);
            g2d.rotate(Math.toRadians(-rotation), x + width / 2, y + height / 2);
        }
    }

    public void rotate() {
        rotation = (rotation + 90) % 360;
    }

    // Getters and setters for position and rotation

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getRotation() { return rotation; }
    public void setRotation(int rotation) { this.rotation = rotation; }
    public String getType() { return type; }

    @Override
    public String toString() {
        return "Furniture{" +
                "type='" + type + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
