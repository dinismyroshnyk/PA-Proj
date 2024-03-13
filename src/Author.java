import java.util.Scanner;

public class Author extends User{
    private String nif;
    private String phone;
    private String address;
    private String literaryStyle;
    private String startDate;

    // constructor
    public Author(String login, String password, String name, String email, String nif, String phone, String address, String literaryStyle, String startDate) {
        super(login, password, name, email, "author", "inactive");
        this.nif = nif;
        this.phone = phone;
        this.address = address;
        this.literaryStyle = literaryStyle;
        this.startDate = startDate;
    }

    // getters, setters, and other author-specific methods
    public static Author register (Scanner scanner) {
        Main.clearConsole();
        String name = User.validateName(scanner);
        String email = User.validateInputInDatabase(scanner, "Email", User::isValidEmail);
        String nif = User.validateInputInDatabase(scanner, "NIF", User::isValidNIF);
        String phone = validatePhone(scanner);
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Literary Style: ");
        String style = scanner.nextLine();
        System.out.print("Start Date: ");
        String startDate = scanner.nextLine();
        String login = User.validateInputInDatabase(scanner, "Login", null);
        String password = validatePassword(scanner);
        return new Author(login, password, name, email, nif, phone, address, style, startDate);
    }
}