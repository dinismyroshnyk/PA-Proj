import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.lang.reflect.Field;

public class Main {
    public static void main(String[] args) {
        // Open scanner for user input
        Scanner scanner = new Scanner(System.in);
        // Prepare and connect to the database
        Database.setUpDatabase(scanner);
        // Application and SQL handling
        try {
            doUsersExist(scanner);
            mainLoop(scanner);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            try {
                Database.conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Failed to rollback transaction.");
                System.out.println("Exception: " + e2);
            }
        } finally {
            if (Database.st != null) {
                try {
                    Database.st.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close statement.");
                    System.out.println("Exception: " + e);
                }
            }
            if (Database.conn != null) {
                try {
                    Database.conn.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close connection.");
                    System.out.println("Exception: " + e);
                }
            }
        }
        // Close scanner and exit application
        scanner.close();
        System.exit(0);
    }

    // Create a manager if no users exist
    private static void doUsersExist(Scanner scanner) {
        // Check if there are any users in the database
        Database.rs = null;
        Database.sqlQuery = new StringBuffer();
        Database.sqlQuery.append(" SELECT username FROM UTILIZADORES ");
        try {
            Database.rs = Database.st.executeQuery(Database.sqlQuery.toString());
        } catch (SQLException e) {
            System.out.println("Failed to execute query.");
            System.out.println("Exception: " + e);
            System.exit(1);
        }
        // If no users are found, create a manager
        try {
            if (!Database.rs.next()) {
                clearConsole();
                System.out.println("No users found. Creating a manager...");
                pressAnyKey(scanner);
                User manager = User.createUser("manager", scanner);
                List<Object> values = getUserValues(manager);
                Database.insertUserIntoDatabase(values, scanner);
            }
        } catch (SQLException e) {
            System.out.println("Failed to check if users exist.");
            System.out.println("Exception: " + e);
            System.exit(1);
        }
    }

    // Extract the values of the fields of a given object
    private static List<Object> getUserValues(User user) {
        List<Object> values = new ArrayList<>();
        Field[] userFields = User.class.getDeclaredFields();
        Field[] objectFields = user.getClass().getDeclaredFields();
        Field[] allFields = new Field[userFields.length + objectFields.length];
        // Copy fields from User class
        System.arraycopy(userFields, 0, allFields, 0, userFields.length);
        // Append fields from the actual runtime class of the user
        System.arraycopy(objectFields, 0, allFields, userFields.length, objectFields.length);
        for (Field field : allFields) {
            field.setAccessible(true);
            try {
                values.add(field.get(user));
            } catch (IllegalAccessException e) {
                System.out.println("Failed to access field.");
                System.out.println("Exception: " + e);
            }
        }
        return values;
    }

    // Main loop of the application
    private static void mainLoop(Scanner scanner) {
        boolean running = true;
        while (running) {
            clearConsole();
            System.out.println("Menu:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.print("\nOption: ");
            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    String user = loginUser(scanner);
                    if (user != null)
                        loggedUserLoop(scanner, user);
                    break;
                case "2":
                    List<Object> values = registerUser(scanner);
                    if (values != null) {
                        Database.insertUserIntoDatabase(values, scanner);
                    }
                    break;
                case "0":
                    clearConsole();
                    running = false;
                    break;
                default:
                    clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    pressAnyKey(scanner);
                    break;
            }
        }
    }

    // Logged user loop
    private static void loggedUserLoop(Scanner scanner, String user) {
        boolean running = true;
        while (running) {
            clearConsole();
            System.out.println("Logged as " + user);
            System.out.println("1. Option 1");
            System.out.println("2. Option 2");
            System.out.println("0. Logout");
            System.out.print("\nOption: ");
            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    clearConsole();
                    System.out.println("Option 1...");
                    pressAnyKey(scanner);
                    break;
                case "2":
                    clearConsole();
                    System.out.println("Option 2...");
                    pressAnyKey(scanner);
                    break;
                case "0":
                    clearConsole();
                    System.out.println("Goodbye, " + user + "!");
                    pressAnyKey(scanner);
                    running = false;
                    break;
                default:
                    clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    pressAnyKey(scanner);
                    break;
            }
        }
    }

    // Register a new user
    private static List<Object> registerUser(Scanner scanner) {
        clearConsole();
        System.out.println("Register a new user: ");
        System.out.println("1. Register as Author");
        System.out.println("2. Register as Reviewer");
        System.out.println("0. Go back");
        System.out.print("\nOption: ");
        String option = scanner.nextLine();
        switch (option) {
            case "1":
                User author = User.createUser("author", scanner);
                return getUserValues(author);
            case "2":
                User reviewer = User.createUser("reviewer", scanner);
                return getUserValues(reviewer);
            case "0":
                break;
            default:
                clearConsole();
                System.out.println("Invalid option. Please try again.");
                pressAnyKey(scanner);
                break;
        }
        return null;
    }

    // Attempt to login a user
    private static String loginUser(Scanner scanner) {
        clearConsole();
        System.out.print("Login: ");
        String login = scanner.nextLine();
        String password = Security.maskPassword();
        // Retrieve the salt and the hashed password
        Database.rs = null;
        Database.sqlQuery = new StringBuffer();
        Database.sqlQuery.append(" SELECT salt, password, nome FROM UTILIZADORES WHERE username = ?");
        PreparedStatement ps = null;
        try {
            ps = Database.conn.prepareStatement(Database.sqlQuery.toString());
            ps.setString(1, login);
            Database.rs = ps.executeQuery();
            if (Database.rs.next()) {
                byte[] salt = Database.rs.getBytes("salt");
                String hash = Database.rs.getString("password");
                String input = Security.hashPassword(password, salt);
                if (hash.equals(input)) {
                    String user = Database.rs.getString("nome");
                    clearConsole();
                    System.out.println("Login successful.");
                    System.out.println("Welcome, " + user + "!");
                    pressAnyKey(scanner);
                    return user;
                } else {
                    System.out.println("\nLogin failed.");
                    pressAnyKey(scanner);
                    return null;
                }
            } else {
                System.out.println("\nLogin failed.");
                pressAnyKey(scanner);
                return null;
            }
        } catch (SQLException e) {
            System.out.println("\nFailed to execute query.");
            System.out.println("Exception: " + e);
            pressAnyKey(scanner);
            return null;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("\nFailed to close prepared statement.");
                    System.out.println("Exception: " + e);
                    pressAnyKey(scanner);
                }
            }
        }
    }

    // Clear the console
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Press any key to continue
    public static void pressAnyKey(Scanner scanner) {
        System.out.print("Press any key to continue...");
        scanner.nextLine();
    }
}