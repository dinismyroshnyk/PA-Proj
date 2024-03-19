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
        String name = Validator.validateInput(scanner, "Name", false);
        String email = Validator.validateInput(scanner, "Email", true);
        String nif = Validator.validateInput(scanner, "NIF", true);
        String phone = Validator.validateInput(scanner, "Phone number", false);
        String address = Validator.validateInput(scanner, "Address", false);
        String style = Validator.validateInput(scanner, "Literary style", false);
        System.out.print("Start Date: ");
        String startDate = scanner.nextLine();
        String login = Validator.validateInput(scanner, "Login", true);
        byte[] salt = Security.generateSalt();
        String password = Validator.validatePassword(salt);
        return new Author(login, password, salt, name, email, nif, phone, address, style, startDate);
    }
}