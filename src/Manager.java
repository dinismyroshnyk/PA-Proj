import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.print.DocFlavor.STRING;

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
            System.out.println("2. Manage existing users");
            System.out.println("3. Manage registration requests " + "\033[33m" + "[" + Database.getUsersCount("pending-activation") + "]" + "\033[0m");
            System.out.println("4. Manage deletion requests " + "\033[33m" + "[" + Database.getUsersCount("pending-deletion") + "]" + "\033[0m");
            System.out.println("5. Manage review requests " + "\033[33m" + "[" + Database.getReviewsCount(null, "initiated") + "]" + "\033[0m");
            System.out.println("6. Insert license");
            System.out.println("0. Log out");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    createActiveUser();
                    break;
                case "2":
                    manageUsersMenu(user, "active");
                    break;
                case "3":
                    manageUsersMenu(user, "pending-activation");
                    break;
                case "4":
                    manageUsersMenu(user, "pending-deletion");
                    break;
                case "5":
                    manageReviewMenu(user);
                    break;
                case "6":
                    License license = License.insertLicense();
                    Database.insertLicenseIntoDatabase(license);
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
            System.out.println("2. Search user");
            System.out.println("0. Go back");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    paginationMenu(user, status);
                    break;
                case "2":
                    searchUser(user, status);
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
    //search users by name, login or type
    public static void searchUser(User user, String status) {
        int page = 1;
        int pageSize = 10;
        int totalUsers = Database.getUsersCount(status);
        System.out.println("Enter search criteria (nome, username ou tipo):");
        String searchCriteria = Input.readLine();
        System.out.println("Enter the value to search:");
        String searchValue = Input.readLine();
        while (true) {
            ResultSet rs = Database.searchUser(searchCriteria, searchValue, status);
            ArrayList<String> ids = displayUsers(rs);
            if (totalUsers > 0) {
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
                            managerActions.get(status).accept(option, User.getValue(user, "type"));
                            totalUsers = Database.getUsersCount(status);
                            break;
                    }
                } catch (NullPointerException e) {
                    continue;
                }
            } else {
                Main.clearConsole();
                System.out.println("No users to manage.");
                Main.pressEnterKey();
                return;
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
                System.out.println("Name: " + rs.getString("nome"));
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

    private static final Map<String, BiConsumer<String, String>> managerActions = Map.of(
        "pending-activation", (userID, callerType) -> Manager.manageUserRequests(userID),
        "active", (userID, callerType) -> Database.manageExistingUserByID(userID, callerType),
        "pending-deletion", (userID, callerType) -> Manager.manageUserRequests(userID)
    );

    // List new users in the database
    public static void paginationMenu(User user, String status) {
        int page = 1;
        int pageSize = 10;
        int totalUsers = Database.getUsersCount(status);
        ArrayList<String> ids = new ArrayList<>();
        while (true) {
            ResultSet rs = Database.getUsers(page, pageSize, status, user);
            if (totalUsers > 0) {
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
                            managerActions.get(status).accept(option, User.getValue(user, "type"));
                            totalUsers = Database.getUsersCount(status);
                            break;
                    }
                } catch (NullPointerException e) {
                    continue;
                }
            } else {
                Main.clearConsole();
                System.out.println("No users to manage.");
                Main.pressEnterKey();
                return;
            }
        }
    }

    private static void manageUserRequests(String userID) {
        boolean running = true;
        while (running) {
            Main.clearConsole();
            System.out.println("Selected user: " + userID);
            System.out.println("1. Approve request");
            System.out.println("2. Reject request");
            System.out.println("0. Go back");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    Database.manageUserRequests(userID, "approve");
                    running = false;
                    break;
                case "2":
                    Database.manageUserRequests(userID, "reject");
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

    private static void manageReviewMenu(Manager manager) {
        boolean running = true;
        while (running) {
            Main.clearConsole();
            System.out.println("Manage review requests: ");
            System.out.println("1. List new reviews");
            System.out.println("2. List accepted reviews");
            System.out.println("3. List the review of any work from the title.");
            System.out.println("4. list all review requests not yet finalized.");
            System.out.println("5. Search review");
            System.out.println("0. Go back");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    reviewPaginationMenu("initiated");
                    break;
                case "2":
                    reviewPaginationMenu("accepted");
                    break;
                case "3":
                    searchReview(true);
                    break;
                case "4":
                    reviewPaginationMenu("completed");
                    break;
                case "5":
                    searchReview(false);
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

    public static void searchReview(boolean titlesearch) {
        int page = 1;
        int pageSize = 10;
        int totalReviews = Database.getReviewsCount("all");
        String searchValue= null;
        String startDate = null;
        String endDate = null;
        String searchCriteria = null;
        if (titlesearch) {
            System.out.println("Enter the title of the work:");
            searchValue = Input.readLine();
            searchCriteria = "o.titulo";
        } else {
            System.out.println("Enter search criteria (identifier, state, author or within a time interval(time interval)):");
            searchCriteria = Input.readLine();
            switch (searchCriteria) {
                case "state":
                  searchCriteria = "r.estado";
                  System.out.println("Enter the value to search:");
                  searchValue = Input.readLine();
                  break;
                case "author":
                  searchCriteria = "u.nome";
                  System.out.println("Enter the value to search:");
                  searchValue = Input.readLine();
                  break;
                case "identifier":
                  searchCriteria = "r.id_revisao";
                  System.out.println("Enter the value to search:");
                  searchValue = Input.readLine();
                  break;
                case "time interval":
                  searchCriteria = "r.data_submissao";
                  System.out.println("Enter the start date (YYYY-MM-DD):");
                  startDate = Input.readLine();
                  System.out.println("Enter the end date (YYYY-MM-DD):");
                  endDate = Input.readLine();
                  break;
                default:
                  System.out.println("Invalid search criteria.");
                  Main.pressEnterKey();
                  return;
              }
              
            } 
        while (true) {
            ResultSet rs = Database.searchReview(searchCriteria, searchValue, startDate, endDate);
            if (totalReviews > 0) {
                ArrayList<String> ids = displayReviews(rs, "all");
                String option = handlePagination(totalReviews, page, pageSize, ids);
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
                            manageReviewRequests(option, "all");
                            totalReviews = Database.getReviewsCount("all");
                            break;
                    }
                } catch (NullPointerException e) {
                    continue;
                }
            } else {
                Main.clearConsole();
                System.out.println("No reviews to manage.");
                Main.pressEnterKey();
                return;
            }
        }
    }

    private static void reviewPaginationMenu(String status) {
        int page = 1;
        int pageSize = 10;
        int totalReviews = Database.getReviewsCount(null, status);
        ArrayList<String> ids = new ArrayList<>();
        boolean orderCheck = false;
        while (true) {
            if (totalReviews > 0) {
                String order = "";
                while (!orderCheck) {
                    Main.clearConsole();
                    if(status.equals("completed")){
                        order = "date";
                    } else{
                        System.out.println("You want to sort by date, title or author?");
                        System.out.print("\nOption: ");
                        order = Input.readLine();
                    }
                    if (order.equals("date")) {
                        order = "REVISOES.DATA_SUBMISSAO";
                        orderCheck = true;
                    } else if (order.equals("title")) {
                        order = "OBRAS.TITULO";
                        orderCheck = true;
                    } else if (order.equals("author")) {
                        order = "autor";
                        orderCheck = true;
                    } else {
                        Main.clearConsole();
                        System.out.println("Invalid option. Please try again.");
                        Main.pressEnterKey();
                    }
                }
                ResultSet rs = Database.getReviews(page, pageSize, status, order, null);
                ids = displayReviews(rs, status);
                String option = handlePagination(totalReviews, page, pageSize, ids);
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
                            manageReviewRequests(option, status);
                            totalReviews = Database.getReviewsCount(null, status);
                            break;
                    }
                } catch (NullPointerException e) {
                    continue;
                }
            } else {
                Main.clearConsole();
                System.out.println("No reviews to manage.");
                Main.pressEnterKey();
                return;
            }
        }
    }

    private static ArrayList<String> displayReviews(ResultSet rs, String status) {
        Main.clearConsole();
        ArrayList<String> ids = new ArrayList<>();
        System.out.println("Next page: n | Previous page: p | Go back: 0\n");
        try {
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("id_revisao"));
                System.out.println("Author: " + rs.getString("autor"));
                if (status.equals("accepted")) {
                    Database.sqlQuery = new StringBuffer();
                    Database.sqlQuery.append("SELECT REVISOES_UTILIZADORES.ID_UTILIZADORES FROM REVISOES_UTILIZADORES JOIN UTILIZADORES ON REVISOES_UTILIZADORES.ID_UTILIZADORES = UTILIZADORES.ID_UTILIZADORES WHERE REVISOES_UTILIZADORES.ID_REVISAO = ? AND UTILIZADORES.TIPO = 'reviewer'");
                    PreparedStatement ps = null;
                    try {
                        ps = Database.conn.prepareStatement(Database.sqlQuery.toString());
                        ps.setString(1, rs.getString("id_revisao"));
                        Database.rs = ps.executeQuery();
                        System.out.print("Reviewers: ");
                        while (Database.rs.next()) {
                            System.out.print(Database.rs.getString("id_utilizadores") + " ");
                        }
                        System.out.println();
                    } catch (SQLException e) {
                        System.out.println("Failed to display reviewer.");
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
                System.out.println("Book: " + rs.getString("titulo"));
                System.out.println("Request date: " + rs.getDate("data"));
                System.out.println("Serial number: " + rs.getString("n_serie"));
                System.out.println();
                ids.add(rs.getString("id_revisao"));
            }
            return ids;
        } catch (SQLException e) {
            System.out.println("Failed to display reviews.");
            System.out.println("Exception: " + e);
            return null;
        }
    }

    private static void manageReviewRequests(String reviewID, String status) {
        boolean running = true;
        while (running) {
            Main.clearConsole();
            System.out.println("Selected review: " + reviewID);
            if (status.equals("initiated")) {
                System.out.println("1. Approve request");
                System.out.println("2. Reject request");
            } else if (status.equals("accepted")) {
                System.out.println("1. Assign reviewers");
            }
            System.out.println("0. Go back");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    if (status.equals("initiated")) {
                        Database.manageReviewRequests(reviewID, "approve");
                    } else if (status.equals("accepted")) {
                        reviewerPaginationMenu(reviewID);
                    }
                    running = false;
                    break;
                case "2":
                    Database.manageReviewRequests(reviewID, "reject");
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

    private static void reviewerPaginationMenu(String reviewID) {
        int page = 1;
        int pageSize = 10;
        int totalReviewers = Database.getReviewersCount(reviewID);
        ArrayList<String> ids = new ArrayList<>();
        while (true) {
            ResultSet rs = Database.getReviewers(page, pageSize, reviewID);
            if (totalReviewers > 0) {
                ids = displayReviewers(rs);
                String option = handlePagination(totalReviewers, page, pageSize, ids);
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
                            Database.assignReviewer(reviewID, option);
                            totalReviewers = Database.getReviewersCount(reviewID);
                            break;
                    }
                } catch (NullPointerException e) {
                    continue;
                }
            } else {
                Main.clearConsole();
                System.out.println("No reviewers to manage.");
                Main.pressEnterKey();
                return;
            }
        }
    }

    private static ArrayList<String> displayReviewers(ResultSet rs) {
        Main.clearConsole();
        ArrayList<String> ids = new ArrayList<>();
        System.out.println("Next page: n | Previous page: p | Go back: 0\n");
        try {
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("id_utilizadores"));
                System.out.println("Name: " + rs.getString("nome"));
                System.out.println("Specialization: " + rs.getString("area_especializacao"));
                System.out.println("Academic background: " + rs.getString("formacao_academica"));
                System.out.println();
                ids.add(rs.getString("id_utilizadores"));
            }
            return ids;
        } catch (SQLException e) {
            System.out.println("Failed to display reviewers.");
            System.out.println("Exception: " + e);
            return null;
        }
    }
}