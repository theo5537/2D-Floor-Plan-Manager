import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.io.File;
import java.awt.event.*;
import javax.swing.JPanel;
public class FloorPlanner implements Serializable{
    private static final long serialVersionUID = 1L;
    private List<Room> rooms; // List of rooms in the floor plan
    private List<Furniture> furnitureItems;
    private boolean rotateMode = false;
    public FloorPlanner() {
    }
    public List<Room> getRooms() { return rooms; }

    public List<Furniture> getFurnitureItems() { return furnitureItems; }

    public void start() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize a list to store rooms
        List<Room> rooms = new ArrayList<>();

        JFrame frame = new JFrame("2D Floor Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        // Main layout
        frame.setLayout(new BorderLayout());

        // Canvas Panel: Pass the list of rooms to the CanvasPanel constructor
        CanvasPanel canvasPanel = new CanvasPanel(rooms);
        frame.add(canvasPanel, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(300, 800));
        controlPanel.setBackground(Color.DARK_GRAY);

        // Add Room Button
        JButton addRoomButton = new JButton("Add Room");
        addRoomButton.addActionListener(e -> {
            // Show room type selection dialog
            String[] options = {"Bathroom", "Bedroom", "Drawing Room", "Kitchen"};
            int choice = JOptionPane.showOptionDialog(
                    null, "Choose a room type", "Add Room",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]
            );

            if (choice != -1) {
                Color color = switch (choice) {
                    case 0 -> Color.BLUE;   // Bathroom
                    case 1 -> Color.GREEN;  // Bedroom
                    case 2 -> Color.ORANGE; // Drawing Room
                    case 3 -> Color.RED;    // Kitchen
                    default -> Color.GRAY;  // Outside
                };

                // Ask for the room's position on the canvas
                String input = JOptionPane.showInputDialog("Enter position (x, y):");
                if (input != null && !input.trim().isEmpty()) {
                    try {
                        String[] coordinates = input.split(",");
                        int x = Integer.parseInt(coordinates[0].trim());
                        int y = Integer.parseInt(coordinates[1].trim());

                        // Create a new room with the specified position
                        Room newRoom = new Room(x, y, 100, 80, color);

                        // Check for overlap with existing rooms before adding
                        if (Alignment.checkOverlap(newRoom, x, y, rooms)) {
                            JOptionPane.showMessageDialog(null, "Room overlaps with an existing room! Please choose a different location.");
                        } else {
                            // Add the room if no overlap
                            canvasPanel.addRoom(newRoom);  // Add the room to the CanvasPanel
                            canvasPanel.repaint(); // Refresh the canvas
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input! Please enter valid coordinates.");
                    }
                }
            }
        });


        // Set Reference Room Button
        JButton setReferenceButton = new JButton("Set Reference Room");
        setReferenceButton.addActionListener(e -> {
            Room selectedRoom = canvasPanel.getSelectedRoom();
            if (selectedRoom != null) {
                canvasPanel.setReferenceRoom(selectedRoom);  // Set as reference room
                JOptionPane.showMessageDialog(null, "Reference room set!");
            } else {
                JOptionPane.showMessageDialog(null, "Please select a room first.");
            }
        });

        // Align Room Button
        JButton alignButton = new JButton("Align Room");
        alignButton.addActionListener(e -> {
            Room selectedRoom = canvasPanel.getSelectedRoom();
            Room referenceRoom = canvasPanel.getReferenceRoom();

            if (selectedRoom != null && referenceRoom != null) {
                // Step 1: Select alignment direction
                String[] directions = {"North", "South", "East", "West"};
                JComboBox<String> directionBox = new JComboBox<>(directions);

                int directionOption = JOptionPane.showOptionDialog(
                        null,
                        directionBox,
                        "Select Alignment Direction",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[]{"OK", "Cancel"},
                        "OK"
                );

                if (directionOption == JOptionPane.OK_OPTION) {
                    String selectedDirection = (String) directionBox.getSelectedItem();

                    // Step 2: Select alignment sub-option based on selected direction
                    String[] subOptions;
                    if (selectedDirection.equals("North") || selectedDirection.equals("South")) {
                        subOptions = new String[]{"Left", "Center", "Right"}; // Horizontal options
                    } else {
                        subOptions = new String[]{"Top", "Center", "Bottom"}; // Vertical options
                    }

                    JComboBox<String> subOptionBox = new JComboBox<>(subOptions);

                    int subOptionChoice = JOptionPane.showOptionDialog(
                            null,
                            subOptionBox,
                            "Select Alignment Sub-Option",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            new Object[]{"OK", "Cancel"},
                            "OK"
                    );

                    if (subOptionChoice == JOptionPane.OK_OPTION) {
                        String selectedSubOption = (String) subOptionBox.getSelectedItem();

                        // Align the selected room
                        Alignment.align(selectedRoom, referenceRoom, selectedDirection, selectedSubOption, rooms);
                        canvasPanel.repaint(); // Update the canvas
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please set a reference room and select a room to align.");
            }
        });

        // Delete Room Button
        JButton deleteRoomButton = new JButton("Delete Room");
        deleteRoomButton.addActionListener(e -> {
            Room selectedRoom = canvasPanel.getSelectedRoom();
            if (selectedRoom != null) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete the selected room?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    canvasPanel.removeRoom(selectedRoom); // Remove the room from the CanvasPanel
                    JOptionPane.showMessageDialog(null, "Room deleted successfully.");
                    canvasPanel.repaint(); // Refresh the canvas
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a room to delete.");
            }
        });
        JButton addDoorButton = new JButton("Add Door");
        addDoorButton.addActionListener(e -> {
            Room selectedRoom = canvasPanel.getSelectedRoom();  // Get the currently selected room

            if (selectedRoom != null) {
                String[] directions = {"North", "South", "East", "West"};
                String direction = (String) JOptionPane.showInputDialog(
                        null,
                        "Select door direction:",
                        "Add Door",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        directions,
                        directions[0]
                );

                if (direction != null) {
                    // Check if there is another room in the specified direction
                    boolean hasAdjacentRoom = false;
                    int threshold = 10; // Define a threshold for adjacency

                    for (Room room : canvasPanel.getRooms()) { // Use the getRooms method
                        if (room != selectedRoom) {
                            switch (direction) {
                                case "North":
                                    if (room.getY() + room.getHeight() >= selectedRoom.getY() - threshold &&
                                            room.getY() + room.getHeight() <= selectedRoom.getY() &&
                                            room.getX() < selectedRoom.getX() + selectedRoom.getWidth() &&
                                            room.getX() + room.getWidth() > selectedRoom.getX()) {
                                        hasAdjacentRoom = true;
                                    }
                                    break;
                                case "South":
                                    if (room.getY() <= selectedRoom.getY() + selectedRoom.getHeight() + threshold &&
                                            room.getY() + room.getHeight() >= selectedRoom.getY() + selectedRoom.getHeight() &&
                                            room.getX() < selectedRoom.getX() + selectedRoom.getWidth() &&
                                            room.getX() + room.getWidth() > selectedRoom.getX()) {
                                        hasAdjacentRoom = true;
                                    }
                                    break;
                                case "East":
                                    if (room.getX() <= selectedRoom.getX() + selectedRoom.getWidth() + threshold &&
                                            room.getX() + room.getWidth() >= selectedRoom.getX() + selectedRoom.getWidth() &&
                                            room.getY() < selectedRoom.getY() + selectedRoom.getHeight() &&
                                            room.getY() + room.getHeight() > selectedRoom.getY()) {
                                        hasAdjacentRoom = true;
                                    }
                                    break;
                                case "West":
                                    if (room.getX() + room.getWidth() >= selectedRoom.getX() - threshold &&
                                            room.getX() + room.getWidth() <= selectedRoom.getX() &&
                                            room.getY() < selectedRoom.getY() + selectedRoom.getHeight() &&
                                            room.getY() + room.getHeight() > selectedRoom.getY()) {
                                        hasAdjacentRoom = true;
                                    }
                                    break;
                            }
                        }
                    }

                    // Check if the selected room is a bathroom or bedroom
                    boolean isBathroomOrBedroom = selectedRoom.isBathroom() || selectedRoom.isBedroom();

                    if (!hasAdjacentRoom && isBathroomOrBedroom) {
                        JOptionPane.showMessageDialog(null,
                                "Doors cannot be added to the outside of bathrooms or bedrooms.");
                        return; // Prevent further processing
                    }

                    String input = JOptionPane.showInputDialog("Enter door length:");
                    if (input != null && !input.trim().isEmpty()) {
                        try {
                            int length = Integer.parseInt(input.trim());

                            // Prompt for relativeCenter (position along the wall)
                            String relativeCenterInput = JOptionPane.showInputDialog(
                                    "Enter the distance from the starting edge of the wall (in pixels):"
                            );
                            if (relativeCenterInput != null && !relativeCenterInput.trim().isEmpty()) {
                                int relativeCenter = Integer.parseInt(relativeCenterInput.trim());

                                // Ensure the door fits within the selected wall
                                boolean fits = switch (direction) {
                                    case "North", "South" -> relativeCenter >= 0 &&
                                            relativeCenter + length <= selectedRoom.getWidth();
                                    case "East", "West" -> relativeCenter >= 0 &&
                                            relativeCenter + length <= selectedRoom.getHeight();
                                    default -> false;
                                };

                                if (!fits) {
                                    JOptionPane.showMessageDialog(null,
                                            "The door exceeds the wall's boundaries! Please enter valid values.");
                                    return;
                                }

                                // Create the new opening (door)
                                Opening newOpening = new Opening(relativeCenter, length, direction, true);

                                // Overlap check
                                boolean overlaps = false;
                                for (Opening existingOpening : selectedRoom.getOpenings()) {
                                    if (existingOpening.getDirection().equalsIgnoreCase(direction)) {
                                        // Check if ranges overlap
                                        int start1 = newOpening.getRelativeCenter();
                                        int end1 = newOpening.getRelativeCenter() + newOpening.getLength();

                                        int start2 = existingOpening.getRelativeCenter();
                                        int end2 = existingOpening.getRelativeCenter() + existingOpening.getLength();

                                        if (Math.max(start1, start2) < Math.min(end1, end2)) {
                                            overlaps = true;
                                            break;
                                        }
                                    }
                                }

                                if (overlaps) {
                                    JOptionPane.showMessageDialog(null,
                                            "Opening overlaps with an existing opening! Please adjust the position or size.");
                                } else {
                                    // Add the opening if no overlap is detected
                                    selectedRoom.addOpening(newOpening);

                                    canvasPanel.repaint(); // Refresh the canvas
                                }
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Invalid input! Please enter valid numeric values.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a room to add a door.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a room to add a door.");
            }
        });

        JButton addWindowButton = new JButton("Add Window");
        addWindowButton.addActionListener(e -> {
            Room selectedRoom = canvasPanel.getSelectedRoom();  // Get the currently selected room

            if (selectedRoom != null) {
                String[] directions = {"North", "South", "East", "West"};
                String direction = (String) JOptionPane.showInputDialog(
                        null,
                        "Select window direction:",
                        "Add Window",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        directions,
                        directions[0]
                );

                if (direction != null) {
                    // Check if there is another room in the specified direction
                    boolean hasAdjacentRoom = false;
                    int threshold = 10; // Define a threshold for adjacency

                    for (Room room : canvasPanel.getRooms()) { // Use the getRooms method
                        if (room != selectedRoom) {
                            switch (direction) {
                                case "North":
                                    if (room.getY() + room.getHeight() >= selectedRoom.getY() - threshold &&
                                            room.getY() + room.getHeight() <= selectedRoom.getY() &&
                                            room.getX() < selectedRoom.getX() + selectedRoom.getWidth() &&
                                            room.getX() + room.getWidth() > selectedRoom.getX()) {
                                        hasAdjacentRoom = true;
                                    }
                                    break;
                                case "South":
                                    if (room.getY() <= selectedRoom.getY() + selectedRoom.getHeight() + threshold &&
                                            room.getY() + room.getHeight() >= selectedRoom.getY() + selectedRoom.getHeight() &&
                                            room.getX() < selectedRoom.getX() + selectedRoom.getWidth() &&
                                            room.getX() + room.getWidth() > selectedRoom.getX()) {
                                        hasAdjacentRoom = true;
                                    }
                                    break;
                                case "East":
                                    if (room.getX() <= selectedRoom.getX() + selectedRoom.getWidth() + threshold &&
                                            room.getX() + room.getWidth() >= selectedRoom.getX() + selectedRoom.getWidth() &&
                                            room.getY() < selectedRoom.getY() + selectedRoom.getHeight() &&
                                            room.getY() + room.getHeight() > selectedRoom.getY()) {
                                        hasAdjacentRoom = true;
                                    }
                                    break;
                                case "West":
                                    if (room.getX() + room.getWidth() >= selectedRoom.getX() - threshold &&
                                            room.getX() + room.getWidth() <= selectedRoom.getX() &&
                                            room.getY() < selectedRoom.getY() + selectedRoom.getHeight() &&
                                            room.getY() + room.getHeight() > selectedRoom.getY()) {
                                        hasAdjacentRoom = true;
                                    }
                                    break;
                            }
                        }
                    }

                    // Prevent adding a window if there is an adjacent room
                    if (hasAdjacentRoom) {
                        JOptionPane.showMessageDialog(null,
                                "A window cannot be placed between two rooms.");
                        return; // Prevent further processing
                    }

                    String input = JOptionPane.showInputDialog("Enter window length:");
                    if (input != null && !input.trim().isEmpty()) {
                        try {
                            int length = Integer.parseInt(input.trim());

                            // Prompt for relativeCenter (position along the wall)
                            String relativeCenterInput = JOptionPane.showInputDialog(
                                    "Enter the distance from the starting edge of the wall (in pixels):"
                            );
                            if (relativeCenterInput != null && !relativeCenterInput.trim().isEmpty()) {
                                int relativeCenter = Integer.parseInt(relativeCenterInput.trim());

                                // Ensure the window fits within the selected wall
                                boolean fits = switch (direction) {
                                    case "North", "South" -> relativeCenter >= 0 &&
                                            relativeCenter + length <= selectedRoom.getWidth();
                                    case "East", "West" -> relativeCenter >= 0 &&
                                            relativeCenter + length <= selectedRoom.getHeight();
                                    default -> false;
                                };

                                if (!fits) {
                                    JOptionPane.showMessageDialog(null,
                                            "The window exceeds the wall's boundaries! Please enter valid values.");
                                    return;
                                }

                                // Create the new opening (window)
                                Opening newOpening = new Opening(relativeCenter, length, direction, false);

                                // Overlap check
                                boolean overlaps = false;
                                for (Opening existingOpening : selectedRoom.getOpenings()) {
                                    if (existingOpening.getDirection().equalsIgnoreCase(direction)) {
                                        // Check if ranges overlap
                                        int start1 = newOpening.getRelativeCenter();
                                        int end1 = newOpening.getRelativeCenter() + newOpening.getLength();
                                        int start2 = existingOpening.getRelativeCenter();
                                        int end2 = existingOpening.getRelativeCenter() + existingOpening.getLength();

                                        if (Math.max(start1, start2) < Math.min(end1, end2)) {
                                            overlaps = true;
                                            break;
                                        }
                                    }
                                }

                                if (overlaps) {
                                    JOptionPane.showMessageDialog(null,
                                            "Window overlaps with an existing opening! Please adjust the position or size.");
                                } else {
                                    // Add the opening if no overlap is detected
                                    selectedRoom.addOpening(newOpening);
                                    canvasPanel.repaint(); // Refresh the canvas
                                }
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Invalid input! Please enter valid numeric values.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a room to add a window.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a room to add a window.");
            }
        });
        JButton addFurnitureButton = new JButton("Add Furniture");
        addFurnitureButton.addActionListener(e -> {
            // Step 1: Ask if the user wants to add Furniture or Fixture
            String[] categories = {"Furniture", "Fixture"};
            String category = (String) JOptionPane.showInputDialog(
                    null,
                    "Select category:",
                    "Add Furniture or Fixture",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    categories,
                    categories[0]
            );

            if (category != null) { // User didn't cancel the selection
                // Step 2: Show the respective options based on the category
                String[] options = category.equals("Furniture")
                        ? new String[]{"Bed", "Chair", "Table", "Sofa", "Dining Set"}
                        : new String[]{"Commode", "Washbasin", "Shower", "Kitchen Sink", "Stove"};

                String type = (String) JOptionPane.showInputDialog(
                        null,
                        "Select " + category.toLowerCase() + " type:",
                        "Add " + category,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (type != null) { // User didn't cancel the type selection
                    // Step 3: Get position for the furniture/fixture
                    String xInput = JOptionPane.showInputDialog("Enter X-coordinate (top-left corner):");
                    String yInput = JOptionPane.showInputDialog("Enter Y-coordinate (top-left corner):");

                    try {
                        int x = Integer.parseInt(xInput.trim());
                        int y = Integer.parseInt(yInput.trim());

                        // Step 4: Create and add the furniture/fixture
                        Furniture newFurniture = new Furniture(type, x, y);
                        canvasPanel.addFurniture(newFurniture); // Add to the list
                        furnitureItems.add(newFurniture);

                        canvasPanel.repaint(); // Refresh the canvas to display it
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input! Coordinates must be numeric.");
                    }
                }
            }
        });

        JButton saveButton = new JButton("Save Floor Plan");
        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Floor Plan");

            // Show save dialog
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Get the selected file and ensure it ends with ".ser"
                File fileToSave = fileChooser.getSelectedFile();
                String fileName = fileToSave.getAbsolutePath();
                if (!fileName.endsWith(".ser")) {
                    fileName += ".ser";
                }

                // Save the floor plan
                try {
                    FloorPlanSaver saver = new FloorPlanSaver(rooms); // Assuming `rooms` is your current list of Room objects
                    saver.saveToFile(fileName);

                    // Confirm the save operation
                    JOptionPane.showMessageDialog(null, "Floor plan saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error saving floor plan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JButton loadButton = new JButton("Load Floor Plan");
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Load Floor Plan");

            // Show open dialog
            int userSelection = fileChooser.showOpenDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = fileChooser.getSelectedFile();

                try {
                    // Load rooms from the file
                    FloorPlanSaver loader = new FloorPlanSaver(new ArrayList<>()); // Create a new FloorPlanSaver
                    loader.loadFromFile(fileToLoad.getAbsolutePath());

                    // Update the application's rooms list directly
                    rooms.clear();
                    rooms.addAll(loader.getRoomList());

                    // Redraw the UI to reflect loaded data
                    canvasPanel.repaint();

                    // Show success message
                    JOptionPane.showMessageDialog(null, "Floor plan loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error loading floor plan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });





        controlPanel.add(addRoomButton);
        controlPanel.add(setReferenceButton);
        controlPanel.add(alignButton);
        controlPanel.add(deleteRoomButton);
        controlPanel.add(addDoorButton);
        controlPanel.add(addWindowButton);
        controlPanel.add(addFurnitureButton);
        controlPanel.add(saveButton);
        controlPanel.add(loadButton);


        frame.add(controlPanel, BorderLayout.WEST);
        frame.setVisible(true);



    }

    public static void main(String[] args) {
        // Start the FloorPlanner application
        SwingUtilities.invokeLater(() -> new FloorPlanner().start());
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FloorPlanner:\n");
        sb.append("Rooms:\n");
        for (Room room : rooms) {
            sb.append(room.toString()).append("\n"); // Assuming Room has a proper toString() method
        }
        sb.append("Furniture:\n");
        for (Furniture furniture : furnitureItems) {
            sb.append(furniture.getType()).append("\n"); // Assuming Furniture has a getType() method
        }
        return sb.toString();
    }
}

