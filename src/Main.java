import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.lang.reflect.Field;

public class Main {
    public static void main(String[] args) throws Exception {
        // Open scanner for user input
        Scanner scanner = new Scanner(System.in);
        // Prepare and connect to the database
        Database.setUpDatabase(scanner);
        // Application and SQL handling
        try {
            doUsersExist(scanner);
            System.out.println("Application running...");
            //mainLoop(scanner);
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
                System.out.println("No users found. Creating a manager...");
                System.out.print("Login: ");
                String login = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                password = Security.hashPassword(password);
                System.out.print("Name: ");
                String name = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                Manager manager = Manager.register(login, password, name, email);
                List<Object> values = getUserValues(manager);
                // Insert the manager into the database
                Database.sqlQuery = new StringBuffer();
                Database.sqlQuery.append(" INSERT INTO UTILIZADORES (username, password, nome, email, tipo, estado) VALUES (?, ?, ?, ?, ?, ?)");
                PreparedStatement ps = null;
                try {
                    ps = Database.conn.prepareStatement(Database.sqlQuery.toString());
                    for (int i = 0; i < values.size(); i++) {
                        ps.setObject(i + 1, values.get(i));
                    }
                    ps.executeUpdate();
                    Database.conn.commit();
                    System.out.println("Manager created successfully.");
                } catch (SQLException e) {
                    System.out.println("Failed to insert manager into database. Rolling back transaction.");
                    System.out.println("Exception: " + e);
                    System.exit(1);
                } finally {
                    if (ps != null) {
                        try {
                            ps.close();
                        } catch (SQLException e) {
                            System.out.println("Failed to close prepared statement.");
                            System.out.println("Exception: " + e);
                        }
                    }
                }
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
                e.printStackTrace();
            }
        }
        return values;
    }

    // Main loop of the application
    //private static void mainLoop(Scanner scanner) {
    //    boolean running = true;
    //    while (running) {
    //        System.out.print("Enter login: ");
    //        String login = scanner.nextLine();
    //        System.out.print("Enter password: ");
    //        String password = scanner.nextLine();
    //        User user = User.login(login, password);
    //        if (user != null) {
    //            User.welcomeUser(user);
//
    //            // Rest of the application...
//
    //            User.goodbyeUser(user);
    //        } else System.out.println("Invalid login or password.");
    //        running = exitApplication(scanner);
    //    }
    //}
//
    //// Ask the user if they want to exit the application
    //private static boolean exitApplication(Scanner scanner) {
    //    System.out.print("Do you want to exit the application? (y/n): ");
    //    String exit = scanner.nextLine().toLowerCase();
    //    if (exit.equals("y") || exit.equals("yes")) return false;
    //    else if (exit.equals("n") || exit.equals("no")) return true;
    //    else {
    //        System.out.println("Invalid input. Please try again.");
    //        return exitApplication(scanner);
    //    }
    //}
}