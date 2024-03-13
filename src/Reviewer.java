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
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        if (Database.Exists(email, "email") || !User.isEmailValid(email)) {
            System.out.println("Email already in use. Please try again.");
            email=User.validateEmail(scanner);
        }
        System.out.print("NIF: ");
        String nif = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Specialization: ");
        String specialization = scanner.nextLine();
        System.out.print("Academic Background: ");
        String academicBackground = scanner.nextLine();
        System.out.print("Login: ");
        String login = scanner.nextLine();
        if (Database.Exists(login, "login")) {
            System.out.println("Email already in use. Please try again.");
            login=User.getLogin(scanner, login);   
        }
        System.out.print("Password: ");
        String password = scanner.nextLine();
        password = Security.hashPassword(password);
        return new Reviewer(login, password, name, email, nif, phone, address, specialization, academicBackground);
    }
}