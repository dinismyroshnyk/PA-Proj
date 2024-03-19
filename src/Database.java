import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Database {
    // Class level variables
    static Statement st = null;
    static ResultSet rs = null;
    static Connection conn = null;
    static StringBuffer sqlQuery = null;

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
                System.out.println("Error reading credentials file: " + e.getMessage());
                System.exit(1);
            }
        } else {
            // Prompt the user for credentials
            System.out.println("[Database Setup]");
            System.out.print("User: ");
            String user = Input.readLine();
            String password = Security.maskPassword();
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

    // Insert a user into the database
    public static void insertUserIntoDatabase(List<Object> values) {
        sqlQuery = new StringBuffer();
        if (values.contains("manager")) {
            sqlQuery.append(" INSERT INTO UTILIZADORES (username, password, salt, nome, email, tipo, estado) VALUES (?, ?, ?, ?, ?, ?, ?)");
        } else if (values.contains("author")) {
            sqlQuery.append(" INSERT INTO UTILIZADORES (username, password, salt, nome, email, tipo, estado, contribuinte, telefone, morada, estilo_literario, data_inicio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        } else if (values.contains("reviewer")) {
            sqlQuery.append(" INSERT INTO UTILIZADORES (username, password, salt, nome, email, tipo, estado, contribuinte, telefone, morada, area_especializacao, formacao_academica) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            for (int i = 0; i < values.size(); i++) {
                ps.setObject(i + 1, values.get(i));
            }
            ps.executeUpdate();
            conn.commit();
            Main.clearConsole();
            System.out.println("User created successfully.");
            Main.pressAnyKey();
        } catch (SQLException e) {
            System.out.println("Failed to insert user into database. Rolling back transaction.");
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

    // Check if a string type exists in the database
    public static boolean existsInDatabase(String st, String type) {
        try {
            sqlQuery = new StringBuffer();
            if (type.equals("email")) {
                sqlQuery.append("SELECT * FROM UTILIZADORES WHERE email = ?");
            } else if (type.equals("login")) {
                sqlQuery.append("SELECT * FROM UTILIZADORES WHERE username = ?");
            }else if (type.equals("nif")) {
                sqlQuery.append("SELECT * FROM UTILIZADORES WHERE contribuinte = ?");
            }
            PreparedStatement ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, st);
            ResultSet rs = ps.executeQuery();
            // If the result set is not empty, the string exists in the database
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute query.");
            System.out.println("Exception: " + e);
        }
        return false;
    }
}
