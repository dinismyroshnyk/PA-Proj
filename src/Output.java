public class Output {
    // Public methods
        // Draw a dynamic border and insert text
        public static void drawBox(String title, String[] menuItems, int[] selectedId) {
            int boxSize = getBoxSize(title, menuItems);
            toggleDrawingMode(DrawingMode.ON);
            System.out.print("lq");
            for (int i = 0; i < boxSize; i++) {
                System.out.print("q");
            }
            System.out.println("qk");
            for (int i = 0; i < menuItems.length; i++) {
                System.out.print("x ");
                for (int j = 0; j < boxSize; j++) {
                    System.out.print(" ");
                }
                System.out.println(" x");
            }
            System.out.print("mq");
            for (int i = 0; i < boxSize; i++) {
                System.out.print("q");
            }
            System.out.print("qj");
            toggleDrawingMode(DrawingMode.OFF);
            insertBoxItems(title, menuItems, boxSize, selectedId);
        }

        // Draw the menu controls
        public static void drawControls() {
            OS.runTaskInSaneMode(() -> {
                String title = "Controls";
                String[] menuItems = {
                    "Up: k or ↑",
                    "Down: j or ↓",
                    "Select: Enter"
                };
                int[] outOfBoundsId = {-1};
                Output.drawBox(title, menuItems, outOfBoundsId);
            });
        }

    // Helper methods
        // Drawing mode enum
            private enum DrawingMode {
                ON,
                OFF
            }

        // Toggle drawing mode
        private static void toggleDrawingMode(DrawingMode toggle) {
            switch (toggle) {
                case ON:
                    System.out.print("\33(0");
                    break;
                case OFF:
                    System.out.print("\33(B");
                    break;
                default:
                    System.out.println("Invalid toggle.");
                    break;
            }
        }

        // Get the box size
        private static int getBoxSize(String title, String[] menuItems) {
            int boxSize = title.length();
            for (String item : menuItems) {
                if (item.length() > boxSize) {
                    boxSize = item.length();
                }
            }
            return boxSize;
        }

        // Insert the box items
        private static void insertBoxItems(String title, String[] menuItems, int boxSize, int[] selectedId) {
            System.out.print("\33[" + (boxSize + 2) + "D");
            System.out.print("\33[" + (menuItems.length + 1) + "A");
            System.out.print(title);
            System.out.print("\33[" + (title.length()) + "D");
            for (String item : menuItems) {
                if (selectedId[0] >= 0 && selectedId[0] < menuItems.length) {
                    if (menuItems[selectedId[0]].equals(item)) {
                        System.out.print("\33[1B");
                        int spaces = boxSize - item.length();
                        item = item + " ".repeat(spaces);
                        System.out.print("\33[7m" + item + "\33[0m");
                        System.out.print("\33[" + (item.length()) + "D");
                    } else {
                        System.out.print("\33[1B");
                        System.out.print(item);
                        System.out.print("\33[" + (item.length()) + "D");
                    }
                } else {
                    System.out.print("\33[1B");
                    System.out.print(item);
                    System.out.print("\33[" + (item.length()) + "D");
                }
            }
            System.out.print("\33[2B\33[2D");
        }
}