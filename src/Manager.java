import java.util.Scanner;

public class Manager extends User {
    // constructor
    public Manager(String login, String password, String name, String email) {
        super(login, password, name, email, "manager", "active");
    }

    // getters, setters, and other manager-specific methods
    public static Manager register (Scanner scanner) {
        Main.clearConsole();
        System.out.println("No users found. Creating a manager...");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        String email = User.validateEmail(scanner);
        String login = validateLogin(scanner);
        String password = scanner.nextLine();
        password = Security.hashPassword(password);
        return new Manager(login, password, name, email);
    }
}