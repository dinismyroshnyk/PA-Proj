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
        System.out.print("Name: ");
        String name = scanner.nextLine();
        String email = validateEmail(scanner);
        System.out.print("NIF: ");
        String nif = scanner.nextLine();
        if (Database.Exists(nif, "NIF") ) {
            System.out.println("NIF already in use . Please try again.");
            nif=User.validateNIF(scanner);   
        }
        if (!User.isValidNIF(nif)) {
            System.out.println("Invalid NIF. Please try again.");
            nif=User.validateNIF(scanner);
        } 
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        if (!User.isValidPhoneNumber(phone)) {
            System.out.println("Invalid phone number. Please try again.");
            phone=User.validatePhone(scanner);
            
        }
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Literary Style: ");
        String style = scanner.nextLine();
        System.out.print("Start Date: ");
        String startDate = scanner.nextLine();
        String login = validateLogin(scanner);
        System.out.print("Password: ");
        String password = scanner.nextLine();
        password = Security.hashPassword(password);
        return new Author(login, password, name, email, nif, phone, address, style, startDate);
    }
}