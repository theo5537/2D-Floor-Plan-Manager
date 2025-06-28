import java.util.HashMap;
import java.util.Map;

public class FurnitureImagePaths {
    private static final Map<String, String> imagePaths = new HashMap<>();

    static {
        // Furniture
        imagePaths.put("Bed", "C:\\Users\\amayB\\OneDrive\\Documents\\top-view-double-bed-with-headboard-blanket_1284335-229-removebg-preview.png");
        imagePaths.put("Chair", "C:\\Users\\amayB\\OneDrive\\Documents\\14-143330_office-chair-top-view-hd-png-download-removebg-preview.png");
        imagePaths.put("Table", "C:\\Users\\amayB\\OneDrive\\Documents\\images-removebg-preview.png");
        imagePaths.put("Sofa", "C:\\Users\\amayB\\OneDrive\\Documents\\sofa_set-removebg-preview.png");
        imagePaths.put("Dining Set", "C:\\Users\\amayB\\OneDrive\\Documents\\dining hall furniture.png");

        // Fixtures
        imagePaths.put("Commode", "C:\\Users\\amayB\\OneDrive\\Documents\\bathroom2-removebg-preview.png");
        imagePaths.put("Washbasin", "C:\\Users\\amayB\\OneDrive\\Documents\\580-5800897_wash-basin-top-view-png-transparent-png-removebg-preview.png");
        imagePaths.put("Shower", "C:\\Users\\amayB\\OneDrive\\Documents\\5ade471a2e796e9cd849637a1fdea54e.png");
        imagePaths.put("Kitchen Sink", "C:\\Users\\amayB\\OneDrive\\Documents\\new-clean-granite-kitchen-sink-260nw-2249037413-removebg-preview.png");
        imagePaths.put("Stove", "C:\\Users\\amayB\\OneDrive\\Documents\\stove-removebg-preview.png");
    }

    public static String getImagePath(String type) {
        return imagePaths.get(type);
    }
}
