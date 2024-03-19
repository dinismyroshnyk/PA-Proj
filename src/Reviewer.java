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
        String name = Validator.validateInput(scanner, "Name", false);
        String email = Validator.validateInput(scanner, "Email", true);
        String nif = Validator.validateInput(scanner, "NIF", true);
        String phone = Validator.validateInput(scanner, "Phone number", false);
        String address = Validator.validateInput(scanner, "Address", false);
        String specialization = Validator.validateInput(scanner, "Specialization", false);
        String academicBackground = Validator.validateInput(scanner, "Academic background", false);
        String login = Validator.validateInput(scanner, "Login", true);
        byte[] salt = Security.generateSalt();
        String password = Validator.validatePassword(salt);
        return new Reviewer(login, password, salt, name, email, nif, phone, address, specialization, academicBackground);
    }
}