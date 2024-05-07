import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    // Public methods
        // Runnable tasks with exception throwing
        public interface DatabaseTask {
            void run() throws SQLException, ClassNotFoundException;
        }

        // Database error handling
        public static void databaseTaskWithErrorHandling(DatabaseTask task, boolean closeResources) {
            try {
                task.run();
            } catch (SQLException e) {
                Utils.clearConsole();
                System.out.println("Database operation failed.");
                System.out.println("Exception: " + e);
                try {
                    conn.rollback();
                    System.exit(1);
                } catch (SQLException e2) {
                    Utils.clearConsole();
                    System.out.println("Failed to rollback transaction.");
                    System.out.println("Exception: " + e2);
                    System.exit(1);
                }
            } catch (ClassNotFoundException e) {
                Utils.clearConsole();
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
                        Utils.clearConsole();
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
            String credentialsPath = "Properties";
            File credentialsFile = new File(credentialsPath);
            String[] params = new String[5];
            // Check if the credentials file exists
            if (credentialsFile.exists()) {
                // Read the credentials from the file
                params = Utils.readParamsFromFile(credentialsFile, params);
            } else {
                // Prompt the user for credentials
                params = readCredentialsFromUser(params);
            }
            // Attempt to connect with the provided credentials
            if (connectToDatabase(params)) {
                if (!credentialsFile.exists()) {
                    saveCredentialsToFile(credentialsPath, params);
                }
            }
        }

    // Helper methods
        // Class level variables
        private static Statement st = null;
        private static Connection conn = null;

        // Read the credentials from the user
        private static String[] readCredentialsFromUser(String[] params) {
            System.out.println("[Database Setup]");
            System.out.print("ip: ");
            params[0] = IO.readLine();
            System.out.print("Port: ");
            params[1] = IO.readLine();
            System.out.print("Database: ");
            params[2] = IO.readLine();
            System.out.print("User: ");
            params[3] = IO.readLine();
            params[4] = Security.maskPassword("Password");
            return params;
        }

        // Save the credentials to the file
        public static void saveCredentialsToFile(String path, String[] params) {
            params[4] = Security.encryptDecryptString(params[4], Security.EncryptionParam.ENCRYPT);
            if (params[4] == null) {
                Utils.clearConsole();
                System.out.println("Failed to encrypt password. Exiting application.");
                System.exit(1);
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
                for (String param : params) {
                    writer.println(param);
                }
            } catch (IOException e) {
                Utils.clearConsole();
                System.out.println("Error saving credentials");
                System.out.println("Exception: " + e);
                System.exit(1);
            }
        }

        // Connect to the database
        private static boolean connectToDatabase(String[] params) {
            databaseTaskWithErrorHandling(() -> {
                String ip = params[0];
                String port = params[1];
                String database = params[2];
                String parameters = "?useTimezone=true&serverTimezone=UTC&verifyServerCertificate=false&useSSL=true";
                String url = "jdbc:mysql://" + ip + ":" + port + "/" + database + parameters;
                // Load the JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Connect to the database
                conn = DriverManager.getConnection(url, params[3], params[4]);
                st = conn.createStatement();
                // Transaction control
                conn.setAutoCommit(false);
            }, false);
            return conn != null;
        }
}