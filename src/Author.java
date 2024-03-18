import java.util.Scanner;

public class Author extends User{
    private String nif;
    private String phone;
    private String address;
    private String literaryStyle;
    private String startDate;

    // constructor
    public Author(String login, String password, byte[] salt, String name, String email, String nif, String phone, String address, String literaryStyle, String startDate) {
        super(login, password, salt, name, email, "author", "inactive");
        this.nif = nif;
        this.phone = phone;
        this.address = address;
        this.literaryStyle = literaryStyle;
        this.startDate = startDate;
    }

    // getters, setters, and other author-specific methods
    public static Author register (Scanner scanner) {
        Main.clearConsole();
        String name = Validator.validateName(scanner);
        String email = Validator.validateInputInDatabase(scanner, "Email", Validator::isValidEmail);
        byte[] salt = Security.generateSalt();
        String nif = Validator.validateInputInDatabase(scanner, "NIF", Validator::isValidNIF);
        String phone = Validator.validatePhone(scanner);
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Literary Style: ");
        String style = scanner.nextLine();
        System.out.print("Start Date: ");
        String startDate = scanner.nextLine();
        String login = Validator.validateInputInDatabase(scanner, "Login", null);
        String password = Validator.validatePassword(scanner, salt);
        return new Author(login, password, salt, name, email, nif, phone, address, style, startDate);
    }
}