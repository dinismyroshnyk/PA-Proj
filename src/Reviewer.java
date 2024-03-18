import java.util.Scanner;

public class Reviewer extends User{
    private String nif;
    private String phone;
    private String address;
    private String specialization;
    private String academicBackground;

    // constructor
    public Reviewer(String login, String password, byte[] salt, String name, String email, String nif, String phone, String address, String specialization, String academicBackground) {
        super(login, password, salt, name, email, "reviewer", "inactive");
        this.nif = nif;
        this.phone = phone;
        this.address = address;
        this.specialization = specialization;
        this.academicBackground = academicBackground;
    }

    // getters, setters, and other reviewer-specific methods
    public static Reviewer register (Scanner scanner) {
        Main.clearConsole();
        String name = Validator.validateName(scanner);
        String email = Validator.validateInputInDatabase(scanner, "Email", Validator::isValidEmail);
        byte[] salt = Security.generateSalt();
        String nif = Validator.validateInputInDatabase(scanner, "NIF", Validator::isValidNIF);
        String phone = Validator.validatePhone(scanner);
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Specialization: ");
        String specialization = scanner.nextLine();
        System.out.print("Academic Background: ");
        String academicBackground = scanner.nextLine();
        String login = Validator.validateInputInDatabase(scanner, "Login", null);
        String password = Validator.validatePassword(scanner, salt);
        return new Reviewer(login, password, salt, name, email, nif, phone, address, specialization, academicBackground);
    }
}