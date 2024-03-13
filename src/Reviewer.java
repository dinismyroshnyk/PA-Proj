import java.util.Scanner;

public class Reviewer extends User{
    private String nif;
    private String phone;
    private String address;
    private String specialization;
    private String academicBackground;

    // constructor
    public Reviewer(String login, String password, String name, String email, String nif, String phone, String address, String specialization, String academicBackground) {
        super(login, password, name, email, "reviewer", "inactive");
        this.nif = nif;
        this.phone = phone;
        this.address = address;
        this.specialization = specialization;
        this.academicBackground = academicBackground;
    }

    // getters, setters, and other reviewer-specific methods
    public static Reviewer register (Scanner scanner) {
        Main.clearConsole();
        String name = User.validateName(scanner);
        String email = User.validateInputInDatabase(scanner, "Email", User::isValidEmail);
        String nif = User.validateInputInDatabase(scanner, "NIF", User::isValidNIF);
        String phone = validatePhone(scanner);
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Specialization: ");
        String specialization = scanner.nextLine();
        System.out.print("Academic Background: ");
        String academicBackground = scanner.nextLine();
        String login = User.validateInputInDatabase(scanner, "Login", null);
        String password = validatePassword(scanner);
        return new Reviewer(login, password, name, email, nif, phone, address, specialization, academicBackground);
    }
}