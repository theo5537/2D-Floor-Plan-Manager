import java.awt.*;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import java.util.ArrayList;

public class CanvasPanel extends JPanel {
    private List<Room> rooms;
    private Room selectedRoom = null; // For dragging or resizing
    private Room referenceRoom = null; // For alignment purposes
    private int offsetX, offsetY;
    private boolean resizing = false; // Track resizing state
    private List<Furniture> furnitureList;
    private Furniture selectedFurniture = null;
    public CanvasPanel(List<Room> rooms) {
        this.rooms = rooms;
        this.setBackground(Color.LIGHT_GRAY);
        furnitureList = new ArrayList<>();
        // Add mouse listeners for dragging and resizing
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Reset selections
                selectedFurniture = null;
                selectedRoom = null;

                // Prioritize furniture selection over room selection
                for (Furniture furniture : furnitureList) {
                    if (isInsideFurniture(e.getX(), e.getY(), furniture)) {
                        selectedFurniture = furniture; // Set the selected furniture
                        offsetX = e.getX() - furniture.getX();
                        offsetY = e.getY() - furniture.getY();
                        return; // Exit after furniture selection
                    }
                }

                // Check if a room is clicked
                for (Room room : rooms) {
                    if (isInsideRoom(e.getX(), e.getY(), room)) {
                        selectedRoom = room; // Set the selected room
                        System.out.println("Room selected: " + room);
                        offsetX = e.getX() - room.getX();
                        offsetY = e.getY() - room.getY();

                        // Check if near resize handle (bottom-right corner)
                        if (isNearResizeHandle(e.getX(), e.getY(), room)) {
                            resizing = true; // Start resizing
                        } else {
                            resizing = false; // Normal dragging
                        }
                        return; // Exit after room selection
                    }
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                resizing = false; // Reset resizing state
                selectedFurniture = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedFurniture != null) {
                    // Move the furniture while dragging
                    int newX = e.getX() - offsetX;
                    int newY = e.getY() - offsetY;

                    // Temporarily move the furniture to check for overlap
                    selectedFurniture.setX(newX);
                    selectedFurniture.setY(newY);

                    // Check for overlaps with other furniture
                    boolean overlap = false;
                    for (Furniture furniture : furnitureList) {
                        if (furniture != selectedFurniture && isInsideFurniture(newX, newY, furniture)) {
                            overlap = true;
                            break;
                        }
                    }

                    if (!overlap) {
                        repaint(); // Only repaint if there's no overlap
                    } else {
                        // Undo the movement if there's an overlap
                        selectedFurniture.setX(selectedFurniture.getX());
                        selectedFurniture.setY(selectedFurniture.getY());
                    }
                }
                if (selectedRoom != null) {
                    if (resizing) {
                        // Resize the room
                        int newWidth = e.getX() - selectedRoom.getX();
                        int newHeight = e.getY() - selectedRoom.getY();

                        // Ensure the room doesn't shrink below minimum size
                        if (newWidth > 50 && newHeight > 50) {
                            selectedRoom.setSize(newWidth, newHeight);
                            repaint(); // Redraw the canvas
                        }
                    } else {
                        // Move the room while dragging
                        int newX = e.getX() - offsetX;
                        int newY = e.getY() - offsetY;

                        // Temporarily move the room to check for overlap
                        selectedRoom.setPosition(newX, newY);

                        // Check for overlaps with other rooms
                        boolean overlap = false;
                        for (Room room : rooms) {
                            if (room != selectedRoom && room.intersects(selectedRoom)) {
                                overlap = true;
                                break;
                            }
                        }

                        if (!overlap) {
                            // Update the reference room's position if it is the selected room
                            if (selectedRoom == referenceRoom) {
                                referenceRoom.setPosition(newX, newY);
                            }
                            repaint(); // Only repaint if there's no overlap
                        } else {
                            // Undo the movement if there's an overlap
                            selectedRoom.setPosition(selectedRoom.getX(), selectedRoom.getY());
                        }
                    }
                }
            }
        });

    }
    public void updateRooms(List<Room> newRooms) {
        this.rooms.clear();
        this.rooms.addAll(newRooms);
    }

    public void updateOpenings(List<Opening> newOpenings) {
        // Assuming you have a way to associate openings with rooms
        for (Room room : rooms) {
            room.setOpenings(newOpenings); // Update openings for each room
        }
    }

    public void updateFurniture(List<Furniture> newFurniture) {
        this.furnitureList.clear();
        this.furnitureList.addAll(newFurniture);
    }
    private boolean isInsideFurniture(int x, int y, Furniture furniture) {
        return x >= furniture.getX() && x <= furniture.getX() + furniture.getWidth() &&
                y >= furniture.getY() && y <= furniture.getY() + furniture.getHeight();
    }

    public void removeRoom(Room room) {
        rooms.remove(room); // Assuming rooms is the list of Room objects
        repaint(); // Refresh the panel to reflect changes
    }

    private boolean isInsideRoom(int x, int y, Room room) {
        return x >= room.getX() && x <= room.getX() + room.getWidth() &&
                y >= room.getY() && y <= room.getY() + room.getHeight();
    }

    private boolean isNearResizeHandle(int x, int y, Room room) {
        int tolerance = 15;
        return x >= room.getX() + room.getWidth() - tolerance &&
                x <= room.getX() + room.getWidth() + tolerance &&
                y >= room.getY() + room.getHeight() - tolerance &&
                y <= room.getY() + room.getHeight() + tolerance;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // First, draw all rooms
        for (Room room : rooms) {
            g2d.setColor(room.getColor());
            g2d.fillRect(room.getX(), room.getY(), room.getWidth(), room.getHeight());

            // Draw a thicker border for the selected room
            if (room == selectedRoom) {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3)); // Thicker border
                g2d.drawRect(room.getX(), room.getY(), room.getWidth(), room.getHeight());
            }

            // Draw a dashed border for the reference room
            if (room == referenceRoom) {
                g2d.setColor(Color.RED);
                float[] dashPattern = {10, 10};
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
                g2d.drawRect(room.getX(), room.getY(), room.getWidth(), room.getHeight());
            }
        }

        // Next, draw all openings (doors and windows) for all rooms
        for (Room room : rooms) {
            room.drawOpenings(g2d); // Draw doors and windows for the room
        }
        for (Furniture furniture : furnitureList) {
            furniture.draw(g);
        }
    }
        public void addFurniture (Furniture furniture){
            furnitureList.add(furniture);
        }


        public Room getSelectedRoom () {
            return selectedRoom;
        }

        public Room getReferenceRoom () {
            return referenceRoom;
        }

        public void addRoom (Room room){
            rooms.add(room);
            repaint(); // Update the canvas
        }
    public List<Room> getRooms() {
        return rooms; // Return the list of rooms
    }

        public void setReferenceRoom (Room referenceRoom){
            this.referenceRoom = referenceRoom;
            repaint();
        }
    }

