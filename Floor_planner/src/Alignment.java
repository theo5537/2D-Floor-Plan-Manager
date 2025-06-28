import java.util.List;
import javax.swing.JOptionPane;

public class Alignment {

    // Method to handle alignment logic
    public static void align(Room selectedRoom, Room referenceRoom, String alignment, String subOption, List<Room> rooms) {
        if (referenceRoom == null) {
            JOptionPane.showMessageDialog(null, "Reference room is null!");
            return;
        }

        int refX = referenceRoom.getX();
        int refY = referenceRoom.getY();
        int refWidth = referenceRoom.getWidth();
        int refHeight = referenceRoom.getHeight();

        int newX = selectedRoom.getX();
        int newY = selectedRoom.getY();

        switch (alignment.toLowerCase()) {
            case "south":
                switch (subOption.toLowerCase()) {
                    case "left":
                        // Align leftmost point of reference room's north side to center of south side of selected room
                        newX = refX; // Leftmost point of the north side
                        newY = refY + refHeight; // Center of the south side of the selected room
                        break;
                    case "center":
                        // Align center of reference room's north side to center of south side of selected room
                        newX = refX + (refWidth - selectedRoom.getWidth()) / 2; // Center of the north side
                        newY = refY + refHeight; // Center of the south side of the selected room
                        break;
                    case "right":
                        // Align rightmost point of reference room's north side to center of south side of selected room
                        newX = refX + refWidth - selectedRoom.getWidth(); // Rightmost point of the north side
                        newY = refY + refHeight; // Center of the south side of the selected room
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid sub-option for north alignment!");
                        return;
                }
                break;

            case "north":
                switch (subOption.toLowerCase()) {
                    case "left":
                        // Align leftmost point of reference room's south side to center of north side of selected room
                        newX = refX; // Leftmost point of the south side
                        newY = refY - selectedRoom.getHeight(); // Center of the north side of the selected room
                        break;
                    case "center":
                        // Align center of reference room's south side to center of north side of selected room
                        newX = refX + (refWidth - selectedRoom.getWidth()) / 2; // Center of the south side
                        newY = refY - selectedRoom.getHeight(); // Center of the north side of the selected room
                        break;
                    case "right":
                        // Align rightmost point of reference room's south side to center of north side of selected room
                        newX = refX + refWidth - selectedRoom.getWidth(); // Rightmost point of the south side
                        newY = refY - selectedRoom.getHeight(); // Center of the north side of the selected room
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid sub-option for south alignment!");
                        return;
                }
                break;

            case "east":
                switch (subOption.toLowerCase()) {
                    case "top":
                        // Align topmost point of reference room's west side to center of east side of selected room
                        newX = refX + refWidth; // Right of the west side
                        newY = refY; // Align to the topmost point
                        break;
                    case "center":
                        // Align center of reference room's west side to center of east side of selected room
                        newX = refX + refWidth; // Right of the west side
                        newY = refY + (refHeight - selectedRoom.getHeight()) / 2; // Center
                        break;
                    case "bottom":
                        // Align bottommost point of reference room's west side to center of east side of selected room
                        newX = refX + refWidth; // Right of the west side
                        newY = refY + refHeight - selectedRoom.getHeight(); // Align to the bottommost point
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid sub-option for west alignment!");
                        return;
                }
                break;

            case "west":
                switch (subOption.toLowerCase()) {
                    case "top":
                        // Align topmost point of reference room's east side to center of west side of selected room
                        newX = refX - selectedRoom.getWidth(); // Left of the east side
                        newY = refY; // Align to the topmost point
                        break;
                    case "center":
                        // Align center of reference room's east side to center of west side of selected room
                        newX = refX - selectedRoom.getWidth(); // Left of the east side
                        newY = refY + (refHeight - selectedRoom.getHeight()) / 2; // Center
                        break;
                    case "bottom":
                        // Align bottommost point of reference room's east side to center of west side of selected room
                        newX = refX - selectedRoom.getWidth(); // Left of the east side
                        newY = refY + refHeight - selectedRoom.getHeight(); // Align to the bottommost point
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid sub-option for east alignment!");
                        return;
                }
                break;

            default:
                JOptionPane.showMessageDialog(null, "Invalid alignment direction!");
                return;
        }

        // Ensure the new position does not overlap with any existing room
        if (checkOverlap(selectedRoom, newX, newY, rooms)) {
            JOptionPane.showMessageDialog(null, "Alignment results in overlap with another room!");
        } else {
            selectedRoom.setPosition(newX, newY);
        }
    }

    // Helper method to check for overlap
    public static boolean checkOverlap(Room room, int newX, int newY, List<Room> rooms) {
        // Temporarily set the new position for the selected room
        int originalX = room.getX();
        int originalY = room.getY();
        room.setPosition(newX, newY);

        // Check for overlaps with other rooms
        for (Room existingRoom : rooms) {
            if (existingRoom != room && room.intersects(existingRoom)) {
                // Restore the original position before returning
                room.setPosition(originalX, originalY);
                return true;
            }
        }

        // Restore the original position
        room.setPosition(originalX, originalY);
        return false;
    }
}