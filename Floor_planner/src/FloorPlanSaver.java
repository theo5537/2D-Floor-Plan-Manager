import java.io.*;
import java.util.List;

public class FloorPlanSaver {
    private List<Room> rooms;

    // Constructor
    public FloorPlanSaver(List<Room> rooms) {
        this.rooms = rooms;
    }

    // Method to save the floor plan to a file
    public void saveToFile(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(rooms);
            System.out.println("Floor plan saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load the floor plan from a file
    public void loadFromFile(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            rooms = (List<Room>) ois.readObject();
            System.out.println("Floor plan loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Method to print the floor plan
    public void printFloorPlan() {
        System.out.println("Rooms:");
        for (Room room : rooms) {
            System.out.println(room);
        }
    }
    public List<Room> getRoomList() {
        return rooms;
    }

}
