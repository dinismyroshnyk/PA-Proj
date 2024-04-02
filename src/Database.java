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
            } else if (type.equals("title")) {
                sqlQuery.append("SELECT * FROM OBRAS WHERE titulo = ?");
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

    public static ResultSet searchReview(String searchCriteria, String searchValue) {
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT r.ID_REVISAO, u.NOME AS autor, o.TITULO AS titulo, r.DATA_SUBMISSAO AS data, r.N_SERIE AS n_serie, r.ESTADO AS estado FROM REVISOES r, UTILIZADORES u, OBRAS o WHERE r.ID_OBRA = o.ID_OBRA AND o.ID_OBRA = u.ID_UTILIZADORES  AND " + searchCriteria + " LIKE ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString()); 
            ps.setString(1, searchValue + "%");
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Failed to execute query.");
            System.out.println("Exception: " + e);
        }
        return rs;
    } 

    // Search for a user in the database
    public static ResultSet searchUser(String searchCriteria, String searchValue, String status) {
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT * FROM UTILIZADORES WHERE estado = ? AND " + searchCriteria + " LIKE ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, status); // Define o estado como o valor passado
            ps.setString(2, "%" + searchValue + "%"); // Garante que a pesquisa seja parcial
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Failed to execute query.");
            System.out.println("Exception: " + e);
        }
        return rs;
    }

    // Get a list of new users from the database (pagination)
    public static ResultSet getUsers(int page, int pageSize, String status, User user) {
        int offset = (page - 1) * pageSize;
        String status2 = null;
        //list users in ascending or descending order
        System.out.println("List users in ascending order or descending order? ('asc' or 'desc')");
        String order = Input.readLine();

        if (status.equals("active")) {
            status2 = "inactive";
        }
        String currUser = User.getValue(user, "login");
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT * FROM UTILIZADORES WHERE username != ? AND (estado = ? OR estado = ?) ORDER BY NOME "+ order +" LIMIT ? OFFSET ?");
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
            sqlQuery.append("SELECT * FROM UTILIZADORES WHERE ID_UTILIZADORES = ?");
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
        sqlQuery.append("SELECT ID_UTILIZADORES FROM UTILIZADORES WHERE username = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("ID_UTILIZADORES");
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
        sqlQuery.append("UPDATE UTILIZADORES SET " + value + " = ? WHERE ID_UTILIZADORES = ?");
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
        sqlQuery.append("SELECT estado FROM UTILIZADORES WHERE ID_UTILIZADORES = ?");
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
            "approve", "UPDATE UTILIZADORES SET estado = 'active' WHERE ID_UTILIZADORES = ?",
            "reject", "DELETE FROM UTILIZADORES WHERE ID_UTILIZADORES = ?"
        ),
        "deletion", Map.of(
            "approve", "UPDATE UTILIZADORES SET estado = 'deleted' WHERE ID_UTILIZADORES = ?",
            "reject", "UPDATE UTILIZADORES SET estado = 'active' WHERE ID_UTILIZADORES = ?"
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

    public static int getReviewsCount(String status) {
        int count = 0;
        sqlQuery = new StringBuffer();
        if (status.equals("initiated")) {
            sqlQuery.append("SELECT * FROM REVISOES WHERE estado = 'initiated'");
        } else if (status.equals("accepted")) {
            sqlQuery.append("SELECT * FROM REVISOES WHERE estado = 'accepted'");
        } else sqlQuery.append("SELECT * FROM REVISOES");
        try {
            rs = st.executeQuery(sqlQuery.toString());
            while (rs.next()) {
                count++;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get reviews count.");
            System.out.println("Exception: " + e);
        }
        return count;
    }

    public static void insertBookIntoDatabase(Book book) {
        if (book == null) {
            System.out.println("Failed to insert book into database. Book object is null.");
            Main.pressEnterKey();
            return;
        }
        sqlQuery = new StringBuffer();
        sqlQuery.append("INSERT INTO OBRAS (id_utilizadores, titulo, subtitulo, estilo_literario, tipo_publicacao, n_paginas, n_palavras, codigo_isbn, n_edicao, data_submissao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            Author author = (Author) Book.getValue(book, "author");
            String authorID = Database.convertUsernameToID(User.getValue(author, "login"));
            ps.setString(1, authorID);
            ps.setString(2, Book.getValue(book, "title").toString());
            String subtitle = Book.getValue(book, "subtitle").toString();
            if (subtitle.isEmpty()) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, subtitle);
            }
            ps.setString(4, Book.getValue(book, "literaryStyle").toString());
            ps.setString(5, Book.getValue(book, "publicationType").toString());
            ps.setInt(6, Integer.parseInt(Book.getValue(book, "numberOfPages").toString()));
            ps.setInt(7, Integer.parseInt(Book.getValue(book, "numberOfWords").toString()));
            ps.setString(8, Book.getValue(book, "isbn").toString());
            ps.setInt(9, Integer.parseInt(Book.getValue(book, "edition").toString()));
            ps.setDate(10, (Date) Book.getValue(book, "submissionDate"));
            ps.executeUpdate();
            conn.commit();
            Main.clearConsole();
            System.out.println("Book inserted successfully.");
            Main.pressEnterKey();
        } catch (SQLException e) {
            System.out.println("Failed to insert book into database. Rolling back transaction.");
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

    public static void insertLicenseIntoDatabase(License license) {
        if (license == null) {
            System.out.println("Failed to insert license into database. License object is null.");
            Main.pressEnterKey();
            return;
        }
        sqlQuery = new StringBuffer();
        sqlQuery.append("INSERT INTO LICENCAS (numero, data_inicio, data_fim, n_disponivel, comentarios) VALUES (?, ?, ?, ?, ?)");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, License.getValue(license, "licenseNumber").toString());
            ps.setDate(2, (Date) License.getValue(license, "validFrom"));
            ps.setDate(3, (Date) License.getValue(license, "validTo"));
            ps.setInt(4, Integer.parseInt(License.getValue(license, "usageLimit").toString()));
            String comments = License.getValue(license, "comments").toString();
            if (comments.isEmpty()) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, comments);
            }
            ps.executeUpdate();
            conn.commit();
            Main.clearConsole();
            System.out.println("License inserted successfully.");
            Main.pressEnterKey();
        } catch (SQLException e) {
            System.out.println("Failed to insert license into database. Rolling back transaction.");
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

    public static int getBookCount(Author author) {
        int count = 0;
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT * FROM OBRAS WHERE OBRAS.ID_UTILIZADORES = ? AND NOT EXISTS (SELECT 1 FROM REVISOES WHERE OBRAS.id_obra = REVISOES.id_obra AND (REVISOES.estado = 'initiated' OR REVISOES.estado = 'accepted' OR REVISOES.estado = 'in_progress'))");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            String authorID = Database.convertUsernameToID(User.getValue(author, "login"));
            ps.setString(1, authorID);
            rs = ps.executeQuery();
            while (rs.next()) {
                count++;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get book count.");
            System.out.println("Exception: " + e);
        }
        return count;
    }

    public static ResultSet getBooks(int page, int pageSize, Author author) {
        int offset = (page - 1) * pageSize;
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT * FROM OBRAS WHERE OBRAS.ID_UTILIZADORES = ? AND NOT EXISTS (SELECT 1 FROM REVISOES WHERE OBRAS.id_obra = REVISOES.id_obra AND (REVISOES.estado = 'initiated' OR REVISOES.estado = 'accepted' OR REVISOES.estado = 'in_progress')) LIMIT ? OFFSET ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            String authorID = Database.convertUsernameToID(User.getValue(author, "login"));
            ps.setString(1, authorID);
            ps.setInt(2, pageSize);
            ps.setInt(3, offset);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Failed to get books.");
            System.out.println("Exception: " + e);
        }
        return rs;
    }

    public static Book getBookByID(Author author, String bookID) {
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT * FROM OBRAS WHERE id_obra = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, bookID);
            rs = ps.executeQuery();
            if (rs.next()) {
                String title = rs.getString("titulo");
                String subtitle = rs.getString("subtitulo");
                String literaryStyle = rs.getString("estilo_literario");
                String publicationType = rs.getString("tipo_publicacao");
                int numberOfPages = rs.getInt("n_paginas");
                int nuberOfWords = rs.getInt("n_palavras");
                int edition = rs.getInt("n_edicao");
                String isbn = rs.getString("codigo_isbn");
                Date submissionDate = rs.getDate("data_submissao");
                Date approvalDate = null;
                try {
                    approvalDate = rs.getDate("data_aprovacao");
                } catch (SQLException e) {
                    approvalDate = null;
                }
                Book book = new Book(author, title, subtitle, literaryStyle, publicationType, numberOfPages, nuberOfWords, isbn, edition, submissionDate, approvalDate);
                return book;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get book by ID.");
            System.out.println("Exception: " + e);
        }
        return null;
    }

    public static void insertReviewIntoDatabase(Review review) {
        if (review == null) {
            System.out.println("Failed to submit review request. Review object is null.");
            Main.pressEnterKey();
            return;
        }
        String sqlQueryReview = "INSERT INTO REVISOES (id_revisao, id_obra, data_submissao, tempo_decorrido, n_serie, custo, estado) VALUES (?, ?, CURRENT_TIMESTAMP, TIME('00:00:00'), ?, ?, ?)";
        String sqlQuerryUser = "INSERT INTO REVISOES_UTILIZADORES (id_revisao, id_utilizadores) VALUES (?, ?)";
        PreparedStatement psReview = null;
        PreparedStatement psUser = null;
        try {
            psReview = conn.prepareStatement(sqlQueryReview.toString());
            psReview.setInt(1, Integer.parseInt(Review.getValue(review, "id").toString()));
            Book book = (Book) Review.getValue(review, "book");
            String bookID = getBookID(book);
            psReview.setString(2, bookID);
            psReview.setString(3, Review.getValue(review, "serialNumber").toString());
            psReview.setNull(4, java.sql.Types.FLOAT);
            psReview.setString(5, Review.getValue(review, "status").toString());
            psReview.executeUpdate();
            Author author = (Author) Book.getValue(book, "author");
            String authorID = Database.convertUsernameToID(User.getValue(author, "login"));
            psUser = conn.prepareStatement(sqlQuerryUser);
            psUser.setInt(1, Integer.parseInt(Review.getValue(review, "id").toString()));
            psUser.setString(2, authorID);
            psUser.executeUpdate();
            conn.commit();
            Main.clearConsole();
            System.out.println("Review request submitted successfully.");
            Main.pressEnterKey();
        } catch (SQLException e) {
            System.out.println("Failed to submit review request. Rolling back transaction.");
            System.out.println("Exception: " + e);
            System.exit(1);
        } finally {
            if (psReview != null) {
                try {
                    psReview.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement.");
                    System.out.println("Exception: " + e);
                    Main.pressEnterKey();
                }
            }
            if (psUser != null) {
                try {
                    psUser.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement.");
                    System.out.println("Exception: " + e);
                    Main.pressEnterKey();
                }
            }
        }
    }

    private static String getBookID(Book book) {
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT id_obra FROM OBRAS WHERE titulo = ? AND ID_UTILIZADORES = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, Book.getValue(book, "title").toString());
            Author author = (Author) Book.getValue(book, "author");
            String authorID = Database.convertUsernameToID(User.getValue(author, "login"));
            ps.setString(2, authorID);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("id_obra");
            }
        } catch (SQLException e) {
            System.out.println("Failed to get book ID.");
            System.out.println("Exception: " + e);
        }
        return null;
    }

    public static ResultSet getReviews(int page, int pageSize, String status, String order) {
        // Cálculo do offset
        int offset = (page - 1) * pageSize;
        // Criação da string de consulta SQL
        StringBuffer sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT REVISOES.ID_REVISAO, UTILIZADORES.NOME AS autor, OBRAS.TITULO AS titulo, REVISOES.DATA_SUBMISSAO AS data, REVISOES.N_SERIE AS n_serie FROM REVISOES JOIN REVISOES_UTILIZADORES ON REVISOES.ID_REVISAO = REVISOES_UTILIZADORES.ID_REVISAO JOIN UTILIZADORES ON REVISOES_UTILIZADORES.ID_UTILIZADORES = UTILIZADORES.ID_UTILIZADORES JOIN OBRAS ON REVISOES.ID_OBRA = OBRAS.ID_OBRA WHERE REVISOES.ESTADO = ? AND UTILIZADORES.TIPO = 'author' ORDER BY ? ASC LIMIT ? OFFSET ?");
        // Preparação da consulta e definição dos parâmetros
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, status);
            ps.setString(2, order);
            ps.setInt(3, pageSize);
            ps.setInt(4, offset);
            // Execução da consulta e obtenção do resultado
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Falha ao obter revisões.");
            System.out.println("Exceção: " + e);
        }
        return rs;
    }

    public static void manageReviewRequests(String reviewID, String decision) {
        sqlQuery = new StringBuffer();
        if (decision.equals("approve")) {
            sqlQuery.append("UPDATE REVISOES SET estado = 'accepted' WHERE id_revisao = ?");
        } else if (decision.equals("reject")) {
            sqlQuery.append("UPDATE REVISOES SET estado = 'archived' WHERE id_revisao = ?");
        }
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, reviewID);
            ps.executeUpdate();
            conn.commit();
            Main.clearConsole();
            System.out.println("Review request " + decision + "ed successfully.");
            Main.pressEnterKey();
        } catch (SQLException e) {
            System.out.println("Failed to " + decision + " review request. Rolling back transaction.");
            System.out.println("Exception: " + e);
            System.exit(1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement.");
                    System.out.println("Exception: " + e);
                    Main.pressEnterKey();
                }
            }
        }
    }

    public static int getReviewersCount(String reviewID) {
        int count = 0;
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT UTILIZADORES.ID_UTILIZADORES, UTILIZADORES.NOME FROM UTILIZADORES LEFT JOIN REVISOES_UTILIZADORES ON UTILIZADORES.ID_UTILIZADORES = REVISOES_UTILIZADORES.ID_UTILIZADORES WHERE UTILIZADORES.TIPO = 'reviewer' AND UTILIZADORES.ESTADO = 'active' AND (REVISOES_UTILIZADORES.ID_REVISAO != ? OR REVISOES_UTILIZADORES.ID_REVISAO IS NULL)");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, reviewID);
            rs = ps.executeQuery();
            while (rs.next()) {
                count++;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get reviewers count.");
            System.out.println("Exception: " + e);
        }
        return count;
    }

    public static ResultSet getReviewers(int page, int pageSize, String reviewID) {
        int offset = (page - 1) * pageSize;
        sqlQuery = new StringBuffer();
        sqlQuery.append("SELECT UTILIZADORES.ID_UTILIZADORES, UTILIZADORES.NOME, UTILIZADORES.AREA_ESPECIALIZACAO, UTILIZADORES.FORMACAO_ACADEMICA FROM UTILIZADORES LEFT JOIN REVISOES_UTILIZADORES ON UTILIZADORES.ID_UTILIZADORES = REVISOES_UTILIZADORES.ID_UTILIZADORES WHERE UTILIZADORES.TIPO = 'reviewer' AND UTILIZADORES.ESTADO = 'active' AND (REVISOES_UTILIZADORES.ID_REVISAO != ? OR REVISOES_UTILIZADORES.ID_REVISAO IS NULL) LIMIT ? OFFSET ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, reviewID);
            ps.setInt(2, pageSize);
            ps.setInt(3, offset);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Failed to get reviewers.");
            System.out.println("Exception: " + e);
        }
        return rs;
    }

    public static void assignReviewer(String reviewID, String reviewerID) {
        sqlQuery = new StringBuffer();
        sqlQuery.append("INSERT INTO REVISOES_UTILIZADORES (id_revisao, id_utilizadores) VALUES (?, ?)");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlQuery.toString());
            ps.setString(1, reviewID);
            ps.setString(2, reviewerID);
            ps.executeUpdate();
            conn.commit();
            Main.clearConsole();
            System.out.println("Reviewer assigned successfully.");
            Main.pressEnterKey();
        } catch (SQLException e) {
            System.out.println("Failed to assign reviewer. Rolling back transaction.");
            System.out.println("Exception: " + e);
            System.exit(1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement.");
                    System.out.println("Exception: " + e);
                    Main.pressEnterKey();
                }
            }
        }
    }
}