import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

public class Reviewer extends User {
    private String nif;
    private String phone;
    private String address;
    private String specialization;
    private String academicBackground;

    // constructor
    public Reviewer(String login, String password, String name, String email, String status, String nif, String phone, String address, String specialization, String academicBackground) {
        super(login, password, name, email, "reviewer", status);
        this.nif = nif;
        this.phone = phone;
        this.address = address;
        this.specialization = specialization;
        this.academicBackground = academicBackground;
    }

    // getters, setters, and other reviewer-specific methods
    private String getNIF () {
        return nif;
    }

    private String getPhone () {
        return phone;
    }

    private String getAddress () {
        return address;
    }

    private String getSpecialization () {
        return specialization;
    }

    private String getAcademicBackground () {
        return academicBackground;
    }

    public static Reviewer register (byte[] salt) {
        Main.clearConsole();
        String name = Validator.validateInput("Name", false);
        String email = Validator.validateInput("Email", true);
        String nif = Validator.validateInput("NIF", true);
        String phone = Validator.validateInput("Phone number", false);
        String address = Validator.validateInput("Address", false);
        String specialization = Validator.validateInput("Specialization", false);
        String academicBackground = Validator.validateInput("Academic background", false);
        String login = Validator.validateInput("Login", true);
        String password = Validator.validatePassword(salt, "Password");
        String status = "pending-activation";
        return new Reviewer(login, password, name, email, status, nif, phone, address, specialization, academicBackground);
    }

    private static final Map<String, Function<Reviewer, String>> getters = Map.of(
        "nif", Reviewer::getNIF,
        "phone", Reviewer::getPhone,
        "address", Reviewer::getAddress,
        "specialization", Reviewer::getSpecialization,
        "academic background", Reviewer::getAcademicBackground
    );

    public static String getValue (Reviewer user, String value) {
        return getters.get(value.toLowerCase()).apply(user);
    }

    public static void loggedUserLoop(Reviewer user) {
        boolean running = true;
        while (running) {
            Main.clearConsole();
            System.out.println("Logged as " + User.getValue(user, "name") + "!");
            System.out.println("1. Delete account");
            System.out.println("2. Edit profile");
            System.out.println("3. Manage review requests");
            System.out.println("0. Log out");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    if (Database.requestAccountDeletion(user)) {
                        running = false;
                    }
                    break;
                case "2":
                    String userID = Database.convertUsernameToID(User.getValue(user, "login"));
                    if (userID != null) {
                        Database.manageExistingUserByID(userID, User.getValue(user, "type"));
                    } else {
                        System.out.println("An error occurred while trying to edit your profile.");
                        Main.pressEnterKey();
                    }
                    break;
                case "3":
                    manageReviewRequests(user);
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

    private static void manageReviewRequests(Reviewer user) {
        boolean running = true;
        while (running) {
            Main.clearConsole();
            System.out.println("1. List assigned requests");
            System.out.println("2. Manage reviews");
            System.out.println("0. Go back");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    listAssignedRequests(user);
                    break;
                case "2":
                    manageReviews(user);
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

    private static void listAssignedRequests(Reviewer user) {
        int page = 1;
        int pageSize = 10;
        int totalReviews = Database.getReviewsCount(user, "accepted");
        ArrayList<String> ids = new ArrayList<>();
        while (true) {
            ResultSet rs = Database.getReviews(page, pageSize, "accepted", "", user);
            if (totalReviews > 0) {
                ids = displayReviews(rs);
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
                            acceptOrRejectReview(option, user);
                            totalReviews = Database.getReviewsCount(user, "accepted");
                            break;
                    }
                } catch (NullPointerException e) {
                    continue;
                }
            } else {
                Main.clearConsole();
                System.out.println("No reviews found.");
                Main.pressEnterKey();
                return;
            }
        }
    }

    private static ArrayList<String> displayReviews(ResultSet rs) {
        Main.clearConsole();
        ArrayList<String> ids = new ArrayList<>();
        System.out.println("Next page: n | Previous page: p | Go back: 0\n");
        try {
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("id_revisao"));
                System.out.println("Book: " + rs.getString("titulo"));
                System.out.println("Author: " + rs.getString("autor"));
                System.out.println("Request date: " + rs.getString("data"));
                System.out.println("Serial number: " + rs.getString("n_serie"));
                System.out.println("Review status: " + rs.getString("estado"));
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

    private static String handlePagination(int totalReviews, int page, int pageSize, ArrayList<String> ids) {
        System.out.print("\nOption or user ID: ");
        String option = Input.readLine();
        switch (option) {
            case "n":
                if (totalReviews > page * pageSize) {
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

    private static void acceptOrRejectReview(String id, Reviewer user) {
        Main.clearConsole();
        System.out.println("1. Accept review");
        System.out.println("2. Reject review");
        System.out.println("0. Go back");
        System.out.print("\nOption: ");
        String option = Input.readLine();
        switch (option) {
            case "1":
                Database.updateReviewStatus(user, id, "accepted");
                break;
            case "2":
                Database.updateReviewStatus(user, id, "rejected");
                break;
            case "0":
                break;
            default:
                Main.clearConsole();
                System.out.println("Invalid option. Please try again.");
                Main.pressEnterKey();
                break;
        }
    }

    private static void manageReviews(Reviewer user) {
        int page = 1;
        int pageSize = 10;
        int totalReviews = Database.getReviewsCount(user, "ongoing");
        ArrayList<String> ids = new ArrayList<>();
        while (true) {
            ResultSet rs = Database.getReviews(page, pageSize, "ongoing", "", user);
            if (totalReviews > 0) {
                ids = displayReviews(rs);
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
                            manageReview(option, user);
                            totalReviews = Database.getReviewsCount(user, "ongoing");
                            break;
                    }
                } catch (NullPointerException e) {
                    continue;
                }
            } else {
                Main.clearConsole();
                System.out.println("No reviews found.");
                Main.pressEnterKey();
                return;
            }
        }
    }

    private static void manageReview(String id, Reviewer user) {
        Main.clearConsole();
        while (true) {
            System.out.println("1. Submit review");
            System.out.println("2. Use license");
            System.out.println("0. Go back");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    submitReview(id, user);
                    break;
                case "2":
                    //useLicense(id, user);
                    break;
                case "0":
                    break;
                default:
                    Main.clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    Main.pressEnterKey();
                    break;
            }
        }
    }

    private static void submitReview(String id, Reviewer user) {
        Database.sqlQuery = new StringBuffer();
        Database.sqlQuery.append("UPDATE REVISOES SET estado = 'completed' WHERE id_revisao = ?");
        PreparedStatement ps = null;
        try {
            ps = Database.conn.prepareStatement(Database.sqlQuery.toString());
            ps.setString(1, id);
            ps.executeUpdate();
            Main.clearConsole();
            System.out.println("Review submitted successfully.");
            Main.pressEnterKey();
        } catch (SQLException e) {
            System.out.println("Failed to submit review.");
            System.out.println("Exception: " + e);
            Main.pressEnterKey();
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