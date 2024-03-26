import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Manager extends User {
    // constructor
    public Manager(String login, String password, String name, String email) {
        super(login, password, name, email, "manager", "active");
    }

    // getters, setters, and other manager-specific methods
    public static Manager register (byte[] salt) {
        Main.clearConsole();
        String name = Validator.validateInput("Name", false);
        String email = Validator.validateInput("Email", true);
        String login = Validator.validateInput("Login", true);
        String password = Validator.validatePassword(salt, "Password");
        return new Manager(login, password, name, email);
    }

    public static void loggedUserLoop(Manager user) {
        boolean running = true;
        while (running) {
            Main.clearConsole();
            System.out.println("Logged as " + User.getValue(user, "name") + "!");
            System.out.println("1. Create new User");
            System.out.println("2. Manage new users");
            System.out.println("3. Manage existing users");
            System.out.println("4. Manage deletion requests");
            System.out.println("0. Log out");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    createActiveUser();
                    break;
                case "2":
                    manageUsersMenu(user, "pending-activation");
                    break;
                case "3":
                    manageUsersMenu(user, "active");
                    break;
                case "4":
                    // Manage account deletion requests
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    Main.clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    Main.pressEnterKey();
                    break;
            }
        }
    }

    private static void createActiveUser() {
        boolean running = true;
        while (running) {
            Main.clearConsole();
            System.out.println("Create new user: ");
            System.out.println("1. Create Author");
            System.out.println("2. Create Reviewer");
            System.out.println("3. Create Manager");
            System.out.println("0. Go back");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            byte[] salt = Security.generateSalt();
            User user = null;
            List<Object> values = null;
            switch (option) {
                case "1":
                    user = User.registerNewUser("author", salt);
                    values = Main.getUserValueList(user, salt);
                    values.set(5, "active");
                    Database.insertUserIntoDatabase(values);
                    break;
                case "2":
                    user = User.registerNewUser("reviewer", salt);
                    values = Main.getUserValueList(user, salt);
                    values.set(5, "active");
                    Database.insertUserIntoDatabase(values);
                    break;
                case "3":
                    user = User.registerNewUser("manager", salt);
                    values = Main.getUserValueList(user, salt);
                    Database.insertUserIntoDatabase(values);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    Main.clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    Main.pressEnterKey();
                    break;
            }
        }
    }

    private static void manageUsersMenu(User user, String status) {
        boolean running = true;
        while (running) {
            Main.clearConsole();
            if (status.equals("active")) {
                System.out.println("Manage existing users: ");
            } else if (status.equals("pending-activation")) {
                System.out.println("Manage new users: ");
            } else if (status.equals("pending-deletion")) {
                System.out.println("Manage deletion requests: ");
            }
            System.out.println("1. List users");
            System.out.println("0. Go back");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    paginationMenu(user, status);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    Main.clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    Main.pressEnterKey();
                    break;
            }
        }
    }

        // Display the new users in the database
    private static ArrayList<String> displayUsers (ResultSet rs) {
        Main.clearConsole();
        ArrayList<String> ids = new ArrayList<>();
        System.out.println("Next page: n | Previous page: p | Go back: 0\n");
        try {
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("id_utilizadores"));
                System.out.println("Login: " + rs.getString("username"));
                System.out.println("Type: " + rs.getString("tipo"));
                if (rs.getString("tipo").equals("author")) {
                    System.out.println("Literary style: " + rs.getString("estilo_literario"));
                    System.out.println("Start date: " + rs.getDate("data_inicio"));
                } else if (rs.getString("tipo").equals("reviewer")) {
                    System.out.println("Specialization: " + rs.getString("area_especializacao"));
                    System.out.println("Academic background: " + rs.getString("formacao_academica"));
                }
                System.out.println();
                ids.add(rs.getString("id_utilizadores"));
            }
            return ids;
        } catch (SQLException e) {
            System.out.println("Failed to display new users.");
            System.out.println("Exception: " + e);
            return null;
        }
    }

    // Handle the interaction with the pagination of new users
    private static String handlePagination(int totalUsers, int page, int pageSize, ArrayList<String> ids) {
        System.out.print("\nOption or user ID: ");
        String option = Input.readLine();
        switch (option) {
            case "n":
                if (totalUsers > page * pageSize) {
                    page++;
                    return "next";
                } else {
                    Main.clearConsole();
                    System.out.println("There are no more pages.");
                    Main.pressEnterKey();
                }
                break;
            case "p":
                if (page > 1) {
                    page--;
                    return "previous";
                } else {
                    Main.clearConsole();
                    System.out.println("There are no previous pages.");
                    Main.pressEnterKey();
                }
                break;
            case "0":
                return "exit";
            default:
                if (ids.contains(option)) {
                    return option;
                } else {
                    Main.clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    Main.pressEnterKey();
                }
                break;
        }
        return null;
    }

    private static final Map<String, Consumer<String>> managerActions = Map.of(
        "pending-activation", Manager::manageUser,
        "active", Database::manageExistingUser
        // "pending-deletion", Manager::manageDeletionRequest
    );

    // List new users in the database
    public static void paginationMenu(User user, String status) {
        int page = 1;
        int pageSize = 3;
        int totalUsers = Database.getUsersCount(status);
        ArrayList<String> ids = new ArrayList<>();
        while (true) {
            ResultSet rs = Database.getUsers(page, pageSize, status, user);
            if (rs != null) {
                ids = displayUsers(rs);
                String option = handlePagination(totalUsers, page, pageSize, ids);
                try {
                    switch (option) {
                        case "next":
                            page++;
                            break;
                        case "previous":
                            page--;
                            break;
                        case "exit":
                            return;
                        default:
                            managerActions.get(status).accept(option);
                            totalUsers = Database.getUsersCount(status);
                            break;
                    }
                } catch (NullPointerException e) {
                    continue;
                }
            } else {
                Main.clearConsole();
                System.out.println("No new users to manage.");
                Main.pressEnterKey();
                return;
            }
        }
    }

    private static void manageUser(String userID) {
        boolean running = true;
        while (running) {
            Main.clearConsole();
            System.out.println("Selected user: " + userID);
            System.out.println("1. Accept user");
            System.out.println("2. Reject user");
            System.out.println("0. Go back");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    Database.acceptRejectUser(userID, "accept");
                    running = false;
                    break;
                case "2":
                    Database.acceptRejectUser(userID, "reject");
                    running = false;
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    Main.clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    Main.pressEnterKey();
                    break;
            }
        }
    }
}