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