import java.util.HashMap;
import java.util.Map;
import java.awt.Dimension;

public class FurnitureDimensions {
    private static final Map<String, Dimension> dimensions = new HashMap<>();

    static {
        // Define dimensions for each furniture type
        dimensions.put("Bed", new Dimension(100, 100)); // Width, Height
        dimensions.put("Chair", new Dimension(80, 80));
        dimensions.put("Table", new Dimension(100, 100));
        dimensions.put("Sofa", new Dimension(120, 120));
        dimensions.put("Dining Set", new Dimension(180, 180));
        // Add more furniture types as needed
    }

    public static Dimension getDimensions(String type) {
        return dimensions.get(type);
    }
}