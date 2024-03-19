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
            System.out.println("1. Create new manager");
            System.out.println("2. Manage new users");
            System.out.println("3. Manage existing users");
            System.out.println("4. Manage account deletion");
            System.out.println("0. Log out");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    byte[] salt = Security.generateSalt();
                    User manager = User.registerNewUser("manager", salt);
                    List<Object> values = Main.getUserValueList(manager, salt);
                    Database.insertUserIntoDatabase(values);
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
}