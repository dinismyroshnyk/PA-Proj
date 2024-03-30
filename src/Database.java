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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.sql.Date;

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
            sqlQuery.append(" INSERT INTO UTILIZADORES (username, password, nome, email, tipo, estado, salt) VALUES (?, ?, ?, ?, ?, ?, ?)");
        } else if (values.contains("author")) {
            sqlQuery.append(" INSERT INTO UTILIZADORES (username, password, nome, email, tipo, estado, contribuinte, telefone, morada, estilo_literario, data_inicio, salt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        } else if (values.contains("reviewer")) {
            sqlQuery.append(" INSERT INTO UTILIZADORES (username, password, nome, email, tipo, estado, contribuinte, telefone, morada, area_especializacao, formacao_academica, salt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
            Main.pressEnterKey();
        } catch (SQLException e) {
            System.out.println("\nFailed to insert user into database. Rolling back transaction.");
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

    // Get user values from the database
    public static User getUserValues(String login, String password) {
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT * FROM UTILIZADORES WHERE username = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, login);
            rs = ps.executeQuery();
            if (rs.next()) {
                byte[] salt = Database.rs.getBytes("salt");
                String hash = Database.rs.getString("password");
                String input = Security.hashPassword(password, salt);
                if (hash.equals(input)) {
                    List<Object> values = new ArrayList<>();
                    values.add(rs.getString("nome"));
                    values.add(rs.getString("email"));
                    values.add(rs.getString("username"));
                    values.add(rs.getString("password"));
                    values.add(rs.getString("tipo"));
                    values.add(rs.getString("estado"));
                    if (rs.getString("tipo").equals("author")) {
                        values.add(rs.getString("contribuinte"));
                        values.add(rs.getString("telefone"));
                        values.add(rs.getString("morada"));
                        values.add(rs.getString("estilo_literario"));
                        values.add(rs.getDate("data_inicio"));
                    } else if (rs.getString("tipo").equals("reviewer")) {
                        values.add(rs.getString("contribuinte"));
                        values.add(rs.getString("telefone"));
                        values.add(rs.getString("morada"));
                        values.add(rs.getString("area_especializacao"));
                        values.add(rs.getString("formacao_academica"));
                    }
                    return User.createUserObject(values);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute query.");
            System.out.println("Exception: " + e);
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
        return null;
    }

    // Get number of new users in the database
    public static int getUsersCount(String status) {
        int count = 0;
        String status2 = null;
        if (status.equals("active")) {
            status2 = "inactive";
        }
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT * FROM UTILIZADORES WHERE estado = ? || estado = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, status);
            ps.setString(2, status2);
            rs = ps.executeQuery();
            while (rs.next()) {
                count++;
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute query.");
            System.out.println("Exception: " + e);
        }
        if (!status.equals("active")) {
            return count;
        } else return count - 1;
    }

    // Get a list of new users from the database (pagination)
    public static ResultSet getUsers(int page, int pageSize, String status, User user) {
        int offset = (page - 1) * pageSize;
        String status2 = null;
        if (status.equals("active")) {
            status2 = "inactive";
        }
        String currUser = User.getValue(user, "login");
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT * FROM UTILIZADORES WHERE username != ? AND (estado = ? OR estado = ?) ORDER BY username ASC LIMIT ? OFFSET ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, currUser);
            ps.setString(2, status);
            ps.setString(3, status2);
            ps.setInt(4, pageSize);
            ps.setInt(5, offset);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Failed to execute query.");
            System.out.println("Exception: " + e);
        }
        return rs;
    }

    private static final Map<String, Map<String, String>> userOptions = Map.of(
        "manager", Map.of(
            "1", "nome",
            "2", "username",
            "3", "password",
            "4", "email",
            "5", "estado"
        ),
        "author", Map.of(
            "1", "nome",
            "2", "username",
            "3", "password",
            "4", "email",
            "5", "telefone",
            "6", "morada",
            "7", "estilo_literario",
            "8", "data_inicio",
            "9", "estado"
        ),
        "reviewer", Map.of(
            "1", "nome",
            "2", "username",
            "3", "password",
            "4", "email",
            "5", "telefone",
            "6", "morada",
            "7", "area_especializacao",
            "8", "formacao_academica",
            "9", "estado"
        )
    );

    public static void manageExistingUserByID(String userID, String callerType) {
        boolean running = true;
        while (running) {
            Main.clearConsole();
            if (callerType.equals("manager")) {
                System.out.println("Selected user: " + userID);
            }
            sqlQuery = new StringBuffer();
            sqlQuery.append("SELECT * FROM UTILIZADORES WHERE id_utilizadores = ?");
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(sqlQuery.toString());
                ps.setString(1, userID);
                rs = ps.executeQuery();
                if (rs.next()) {
                    displayUserInfo(rs, callerType);
                    System.out.println("0. Go back");
                    System.out.print("\nOption: ");
                    String option = Input.readLine();
                    running = handleUserOptions(option, userID, callerType, rs);
                }
            } catch (SQLException e) {
                System.out.println("Failed to display user.");
                System.out.println("Exception: " + e);
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
    }

    private static boolean handleUserOptions(String option, String userID, String callerType, ResultSet rs) {
        boolean running = true;
        try {
            if (option.equals("0")) {
                running = false;
            } else if (userOptions.get(rs.getString("tipo")).containsKey(option)) {
                if (!callerType.equals("manager") && option.equals("9")) {
                    Main.clearConsole();
                    System.out.println("Invalid option.");
                    Main.pressEnterKey();
                } else {
                    String value = userOptions.get(rs.getString("tipo")).get(option);
                    updateValueForUserID(value, userID, rs);
                }
            } else {
                Main.clearConsole();
                System.out.println("Invalid option.");
                Main.pressEnterKey();
            }
        } catch (Exception e) {
            System.out.println("Failed to handle user options.");
            System.out.println("Exception: " + e);
        }
        return running;
    }

    public static String convertUsernameToID(String username) {
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT id_utilizadores FROM UTILIZADORES WHERE username = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("id_utilizadores");
            }
        } catch (SQLException e) {
            System.out.println("Failed to convert username to ID.");
            System.out.println("Exception: " + e);
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
        return null;
    }

    private static void displayUserInfo(ResultSet rs, String callerType) {
        try {
            System.out.println("1. Name: " + rs.getString("nome"));
            System.out.println("2. Login: " + rs.getString("username"));
            System.out.println("3. Password: [ENCRYPTED]");
            System.out.println("4. Email: " + rs.getString("email"));
            if (!rs.getString("tipo").equals("manager")) {
                System.out.println("5. Phone number: " + rs.getString("telefone"));
                System.out.println("6. Address: " + rs.getString("morada"));
            } else {
                System.out.println("5. Toggle user state. Current state: " + rs.getString("estado"));
            }
            if (rs.getString("tipo").equals("author")) {
                System.out.println("7. Literary style: " + rs.getString("estilo_literario"));
                System.out.println("8. Start date: " + rs.getDate("data_inicio"));
            } else if (rs.getString("tipo").equals("reviewer")) {
                System.out.println("7. Specialization: " + rs.getString("area_especializacao"));
                System.out.println("8. Academic background: " + rs.getString("formacao_academica"));
            }
            if (!rs.getString("tipo").equals("manager") && callerType.equals("manager")) {
                System.out.println("9. Toggle user state. Current state: " + rs.getString("estado"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to display user.");
            System.out.println("Exception: " + e);
        }
    }

    private static final Map<String, Function<String, String>> managerValidators = Map.of(
        "nome", input -> Validator.validateInput("Updated name", false),
        "username", input -> Validator.validateInput("Updated login", true),
        "email", input -> Validator.validateInput("Updated email", true),
        "telefone", input -> Validator.validateInput("Updated phone number", false),
        "morada", input -> Validator.validateInput("Updated address", false),
        "estilo_literario", input -> Validator.validateInput("Updated literary style", false),
        "area_especializacao", input -> Validator.validateInput("Updated specialization", false),
        "formacao_academica", input -> Validator.validateInput("Updated academic background", false)
    );

    private static void updateValueForUserID(String value, String userID, ResultSet rs) {
        sqlQuery = new StringBuffer();
        sqlQuery.append("UPDATE UTILIZADORES SET " + value + " = ? WHERE id_utilizadores = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            if (value.equals("password")) {
                byte[] salt = rs.getBytes("salt");
                String password = Validator.validatePassword(salt, "Updated password");
                ps.setString(1, password);
            } else if (value.equals("data_inicio")) {
                Date startDate = Validator.validateDate();
                ps.setDate(1, startDate);
            } else if (value.equals("estado")) {
                if (rs.getString("estado").equals("active")) {
                    ps.setString(1, "inactive");
                } else {
                    ps.setString(1, "active");
                }
            } else {
                ps.setString(1, managerValidators.get(value).apply(value));
            }
            ps.setString(2, userID);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            System.out.println("Failed to update value.");
            System.out.println("Exception: " + e);
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
    };

    public static boolean requestAccountDeletion(User user) {
        Main.clearConsole();
        String textBlock = """
            WARNING: This action is irreversible.
            Your account will be inaccessible until your request is reviewed and processed.
            In case of acceptance, your account will be permanently deleted.
            Are you sure you wish to proceed?
            """;
        System.out.println(textBlock);
        System.out.print("Type 'Yes, delete my account' to confirm: ");
        String confirmation = Input.readLine();
        if (!confirmation.equalsIgnoreCase("Yes, delete my account")) {
            Main.clearConsole();
            System.out.println("Account deletion request cancelled.");
            Main.pressEnterKey();
            return false;
        }
        sqlQuery = new StringBuffer();
        sqlQuery.append("UPDATE UTILIZADORES SET estado = 'pending-deletion' WHERE username = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, User.getValue(user, "login"));
            ps.executeUpdate();
            conn.commit();
            Main.clearConsole();
            System.out.println("Account deletion request submitted successfully.");
            System.out.println("The result of your request will be sent to the registered email address.");
            System.out.println("You will be logged out.");
            Main.pressEnterKey();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to submit account deletion request. Rolling back transaction.");
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
        return false;
    }

    public static void manageUserRequests(String userID, String decision) {
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT estado FROM UTILIZADORES WHERE id_utilizadores = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, userID);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("estado").equals("pending-activation")) {
                    manageRequest(userID, decision, "registration");
                } else if (rs.getString("estado").equals("pending-deletion")) {
                    manageRequest(userID, decision, "deletion");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to accept/reject request.");
            System.out.println("Exception: " + e);
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

    private static void manageRequest(String userID, String decision, String requestType) {
        String action = querryOptions.get(requestType).get(decision);
        String message = messageOptions.get(requestType).get(decision);
        String error = errorOptions.get(requestType).get(decision);
        sqlQuery = new StringBuffer();
        sqlQuery.append(action);
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, userID);
            ps.executeUpdate();
            conn.commit();
            Main.clearConsole();
            System.out.println(message);
        } catch (SQLException e) {
            System.out.println(error + " Rolling back transaction.");
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

    private static final Map<String, Map<String, String>> querryOptions = Map.of(
        "registration", Map.of(
            "approve", "UPDATE UTILIZADORES SET estado = 'active' WHERE id_utilizadores = ?",
            "reject", "DELETE FROM UTILIZADORES WHERE id_utilizadores = ?"
        ),
        "deletion", Map.of(
            "approve", "UPDATE UTILIZADORES SET estado = 'deleted' WHERE id_utilizadores = ?",
            "reject", "UPDATE UTILIZADORES SET estado = 'active' WHERE id_utilizadores = ?"
        )
    );

    private static final Map<String, Map<String, String>> messageOptions = Map.of(
        "registration", Map.of(
            "approve", "User registration approved.",
            "reject", "User registration rejected."
        ),
        "deletion", Map.of(
            "approve", "User deletion request approved. User account deleted.",
            "reject", "User deletion request rejected. User account restored."
        )
    );

    private static final Map<String, Map<String, String>> errorOptions = Map.of(
        "registration", Map.of(
            "approve", "Failed to activate user.",
            "reject", "Failed to delete user."
        ),
        "deletion", Map.of(
            "approve", "Failed to delete user account.",
            "reject", "Failed to restore user account."
        )
    );
}
