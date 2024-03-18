import java.util.Scanner;

public class Manager extends User {
    // constructor
    public Manager(String login, String password, byte[] salt, String name, String email) {
        super(login, password, salt, name, email, "manager", "active");
    }

    // getters, setters, and other manager-specific methods
    public static Manager register (Scanner scanner) {
        Main.clearConsole();
        String name = Validator.validateName(scanner);
        String email = Validator.validateInputInDatabase(scanner, "Email", Validator::isValidEmail);
        byte[] salt = Security.generateSalt();
        String login = Validator.validateInputInDatabase(scanner, "Login", null);
        String password = Validator.validatePassword(scanner, salt);
        return new Manager(login, password, salt, name, email);
    }
}