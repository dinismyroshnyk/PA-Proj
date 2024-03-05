import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) throws Exception {
        // JDBC related variables
        String ip = "localhost";
        String port = "3306";
        String database = "projeto";
        String parameters  = "?useTimezone=true&serverTimezone=UTC&verifyServerCertificate=false&useSSL=true";
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + database + parameters;
        String user = "root";
        String password = "toor";

        // JDBC objects
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        // Load the JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            System.out.println("Exception: " + e);
            System.exit(1);
        }

        // Connect to the database
        try {
            conn = DriverManager.getConnection(url, user, password);
            st = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            System.out.println("Exception: " + e);
            System.exit(1);
        }

        // Transaction control
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("Failed to turn off auto-commit.");
            System.out.println("Exception: " + e);
            System.exit(1);
        }

        // Open scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Application and SQL handling
        try {
            doUsersExist(scanner);
            mainLoop(scanner);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            try {
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Failed to rollback transaction.");
                System.out.println("Exception: " + e2);
            }
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close statement.");
                    System.out.println("Exception: " + e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
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

    private static void doUsersExist(Scanner scanner) {
        if (User.getAllUsers().isEmpty()) {
            System.out.println("No users found. Please register a manager.");
            boolean valid = false;
            while (!valid) {
                System.out.print("Login: ");
                String login = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                System.out.print("Name: ");
                String name = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                Manager.register(login, password, name, email);
                if (!User.getAllUsers().isEmpty()) {
                    System.out.println("Manager created successfully.");
                    valid = true;
                }
            }
        }
    }

    private static void mainLoop(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.print("Enter login: ");
            String login = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            User user = User.login(login, password);
            if (user != null) {
                User.welcomeUser(user);

                // Rest of the application...

                User.goodbyeUser(user);
            } else System.out.println("Invalid login or password.");
            running = exitApplication(scanner);
        }
    }

    private static boolean exitApplication(Scanner scanner) {
        System.out.print("Do you want to exit the application? (y/n): ");
        String exit = scanner.nextLine().toLowerCase();
        if (exit.equals("y") || exit.equals("yes")) return false;
        else if (exit.equals("n") || exit.equals("no")) return true;
        else {
            System.out.println("Invalid input. Please try again.");
            return exitApplication(scanner);
        }
    }
}