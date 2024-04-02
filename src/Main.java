import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.lang.reflect.Field;
import java.time.*;

public class Main {
    public static void main(String[] args) {
        // Prepare and connect to the database
        Database.setUpDatabase();
        // Application and SQL handling
        try {
            doUsersExist();
            mainLoop();
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
        Input.closeScanner();
        System.exit(0);
    }

    // Create a manager if no users exist
    private static void doUsersExist() {
        // Check if there are any users in the database
        Database.rs = null;
        Database.sqlQuery = new StringBuffer();
        Database.sqlQuery.append(" SELECT tipo FROM UTILIZADORES WHERE tipo = 'manager'");
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
                pressEnterKey();
                byte[] salt = Security.generateSalt();
                User manager = User.registerNewUser("manager", salt);
                List<Object> values = getUserValueList(manager, salt);
                Database.insertUserIntoDatabase(values);
            }
        } catch (SQLException e) {
            System.out.println("Failed to check if users exist.");
            System.out.println("Exception: " + e);
            System.exit(1);
        }
    }

    // Extract the values of the fields of a given object
    public static List<Object> getUserValueList(User user, byte[] salt) {
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
                if (!Map.class.isAssignableFrom(field.getType())) {
                    values.add(field.get(user));
                }
            } catch (IllegalAccessException e) {
                System.out.println("Failed to access field.");
                System.out.println("Exception: " + e);
            }
        }
        values.add(salt);
        return values;
    }

    // Main loop of the application
    private static void mainLoop() {
        boolean running = true;
        LocalDateTime startTime = LocalDateTime.now();
        while (running) {
            clearConsole();
            System.out.println("Menu:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    User user = loginUser();
                    if (user != null) {
                        User.loggedUserLoop(user);
                    }
                    break;
                case "2":
                    List<Object> values = registerUser();
                    if (values != null) {
                        Database.insertUserIntoDatabase(values);
                    }
                    break;
                case "0":
                    clearConsole();
                    presentation(startTime);
                    running = false;
                    break;
                default:
                    clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    pressEnterKey();
                    break;
            }
        }
    }

    private static void presentation(LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("EEEE; yyyy-MM-dd HH:mm:ss");
        long executionTimeMillis = Duration.between(startTime, endTime).toMillis();
        Duration duration = Duration.ofMillis(executionTimeMillis);
        String tempoExecucaoFormatado = String.format("%d Milissegundos (%d Segundos; %d Minutos; %d Horas)", executionTimeMillis, duration.toSecondsPart(), duration.toMinutesPart(), duration.toHours());
        System.out.println("Data de Início: " + startTime.format(date));
        System.out.println("Data de Fim: " + endTime.format(date));
        System.out.println("Tempo de Execução: " + tempoExecucaoFormatado);
        System.out.println("Press Enter to exit...");
        Input.readLine();
        
    }

    // Register a new user
    private static List<Object> registerUser() {
        clearConsole();
        System.out.println("Register a new user: ");
        System.out.println("1. Register as Author");
        System.out.println("2. Register as Reviewer");
        System.out.println("0. Go back");
        System.out.print("\nOption: ");
        String option = Input.readLine();
        byte[] salt = Security.generateSalt();
        switch (option) {
            case "1":
                User author = User.registerNewUser("author", salt);
                return getUserValueList(author, salt);
            case "2":
                User reviewer = User.registerNewUser("reviewer", salt);
                return getUserValueList(reviewer, salt);
            case "0":
                break;
            default:
                clearConsole();
                System.out.println("Invalid option. Please try again.");
                pressEnterKey();
                break;
        }
        return null;
    }

    private static final Map<String, String> userStatusErrorMessage = Map.of(
        "inactive", "This account was deactivated. Please contact a manager.",
        "pending-activation", "Your registration request is being reviewed. Please try again later.",
        "pending-deletion", "A deletion request is pending for this account. Access denied.",
        "deleted", "This account was deleted. Your login credentials will soon be deactivated."
    );

    // Attempt to login a user
    private static User loginUser() {
        clearConsole();
        System.out.print("Login: ");
        String login = Input.readLine();
        String password = Security.maskPassword("Password");
        User user = Database.getUserValues(login, password);
        if (user != null) {
            if (User.getValue(user, "status").equals("active")) {
                clearConsole();
                System.out.println("Login successful.");
                System.out.println("Welcome, " + User.getValue(user, "name") + "!");
                pressEnterKey();
                return user;
            } else {
                clearConsole();
                System.out.println(userStatusErrorMessage.get(User.getValue(user, "status")));
                pressEnterKey();
                return null;
            }
        } else {
            System.out.println("\nInvalid credentials. Please try again.");
            pressEnterKey();
            return null;
        }
    }

    // Clear the console
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Press any key to continue
    public static void pressEnterKey() {
        System.out.print("Press Enter to continue...");
        Input.readLine();
    }

    // Get current date
    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
}