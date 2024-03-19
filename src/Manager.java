import java.util.List;

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
        String password = Validator.validatePassword(salt);
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
            System.out.println("4. Manage account deletion");
            System.out.println("0. Log out");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    createActiveUser();
                    break;
                case "2":
                    // Manage new users
                    break;
                case "3":
                    // Manage existing users
                    break;
                case "4":
                    // Manage account deletion
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    Main.clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    Main.pressAnyKey();
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
                    Main.pressAnyKey();
                    break;
            }
        }
    }
}