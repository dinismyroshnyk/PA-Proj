import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    // Class level variables
    static Statement st = null;
    static ResultSet rs = null;
    static Connection conn = null;
    static StringBuffer sqlQuery = null;

    // Runnable tasks with exception throwing
    public interface DatabaseTask {
        void run() throws SQLException, ClassNotFoundException;
    }

    // Database error handling
    public static void databaseTaskWithErrorHandling(DatabaseTask task, boolean closeResources) {
        try {
            task.run();
        } catch (SQLException e) {
            OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), "sane");
            System.out.println("Database operation failed.");
            System.out.println("Exception: " + e);
            try {
                conn.rollback();
                System.exit(1);
            } catch (SQLException e2) {
                System.out.println("Failed to rollback transaction.");
                System.out.println("Exception: " + e2);
                System.exit(1);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to load JDBC driver.");
            System.out.println("Exception: " + e);
            System.exit(1);
        } finally {
            if (closeResources) {
                try {
                    if (st != null) {
                        st.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    System.out.println("Failed to close database resources.");
                    System.out.println("Exception: " + e);
                    System.exit(1);
                }
            }
        }
    }

    // Set up the database connection
    public static void setUpDatabase() {
        // Path to the credentials file
        String credentialsPath = ".credentials";
        File credentialsFile = new File(credentialsPath);
        // Check if the credentials file exists
        if (credentialsFile.exists()) {
            // Read the credentials from the file
            try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFile))) {
                String user = reader.readLine();
                user = Security.encryptDecryptString(user, "-d");
                String password = reader.readLine();
                password = Security.encryptDecryptString(password, "-d");
                connectToDatabase(user, password);
            } catch (IOException e) {
                OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), "sane");
                System.out.println("Error reading credentials file: " + e.getMessage());
                System.exit(1);
            }
        } else {
            // Prompt the user for credentials
            System.out.println("[Database Setup]");
            System.out.print("User: ");
            String user = Input.readLine();
            String password = Security.maskPassword("Password");
            // Attempt to connect with the provided credentials
            if (connectToDatabase(user, password)) {
                // If successful, encrypt and save the credentials
                user = Security.encryptDecryptString(user, "-e");
                password = Security.encryptDecryptString(password, "-e");
                if (password == null) {
                    System.out.println("Failed to encrypt password. Exiting application.");
                    System.exit(1);
                }
                try (PrintWriter writer = new PrintWriter(new FileWriter(credentialsPath))) {
                    writer.println(user);
                    writer.println(password);
                } catch (IOException e) {
                    System.out.println("Error saving credentials: " + e.getMessage());
                    System.exit(1);
                }
            } else {
                System.out.println("Invalid credentials. Exiting application.");
                System.exit(1);
            }
        }
    }

    // Connect to the database
    private static boolean connectToDatabase(String user, String password) {
        databaseTaskWithErrorHandling(() -> {
            String ip = "localhost";
            String port = "3306";
            String database = "projeto";
            String parameters = "?useTimezone=true&serverTimezone=UTC&verifyServerCertificate=false&useSSL=true";
            String url = "jdbc:mysql://" + ip + ":" + port + "/" + database + parameters;
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connect to the database
            conn = DriverManager.getConnection(url, user, password);
            st = conn.createStatement();
            // Transaction control
            conn.setAutoCommit(false);
        }, false);
        return conn != null;
    }
}