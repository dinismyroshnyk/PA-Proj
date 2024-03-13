import java.util.Scanner;

public class Manager extends User {
    // constructor
    public Manager(String login, String password, String name, String email) {
        super(login, password, name, email, "manager", "active");
    }

    // getters, setters, and other manager-specific methods
    public static Manager register (Scanner scanner) {
        Main.clearConsole();
        String name = User.validateName(scanner);
        String email = User.validateInputInDatabase(scanner, "Email", User::isValidEmail);
        String login = User.validateInputInDatabase(scanner, "Login", null);
        String password = validatePassword(scanner);
        return new Manager(login, password, name, email);
    }
}