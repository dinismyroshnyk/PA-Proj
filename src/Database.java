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
import java.util.Scanner;

public class Database {
    // Class level variables
    static Statement st = null;
    static ResultSet rs = null;
    static Connection conn = null;
    static StringBuffer sqlQuery = null;

    // Set up the database connection
    public static void setUpDatabase(Scanner scanner) {
        // Path to the credentials file
        String credentialsPath = ".credentials";
        File credentialsFile = new File(credentialsPath);
        // Check if the credentials file exists
        if (credentialsFile.exists()) {
            // Read the credentials from the file
            try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFile))) {
                String user = reader.readLine();
                String password = reader.readLine();
                password = Security.encryptDecryptString(password, "-d");
                connectToDatabase(user, password);
            } catch (IOException e) {
                System.out.println("Error reading credentials file: " + e.getMessage());
                System.exit(1);
            }
        } else {
            // Prompt the user for credentials
            System.out.print("Database User: ");
            String user = scanner.nextLine();
            System.out.print("Database Password: ");
            String password = scanner.nextLine();
            // Attempt to connect with the provided credentials
            if (connectToDatabase(user, password)) {
                // If successful, encrypt and save the credentials
                password = Security.encryptDecryptString(password, "-e");
                try (PrintWriter writer = new PrintWriter(new FileWriter(credentialsPath))) {
                    writer.println(user);
                    writer.println(password);
                } catch (IOException e) {
                    System.out.println("Error saving credentials: " + e.getMessage());
                    System.exit(1);
                }
            } else {
                System.out.println("Invalid credentials. Please try again.");
                setUpDatabase(scanner);
            }
        }
    }

    // Connect to the database
    private static boolean connectToDatabase(String user, String password) {
        String ip = "localhost";
        String port = "3306";
        String database = "projeto";
        String parameters = "?useTimezone=true&serverTimezone=UTC&verifyServerCertificate=false&useSSL=true";
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + database + parameters;
        // Load the JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            System.out.println("Exception: " + e);
            return false;
        }
        // Connect to the database
        try {
            conn = DriverManager.getConnection(url, user, password);
            st = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            System.out.println("Exception: " + e);
            return false;
        }
        // Transaction control
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("Failed to turn off auto-commit.");
            System.out.println("Exception: " + e);
            return false;
        }
        return true;
    }
}