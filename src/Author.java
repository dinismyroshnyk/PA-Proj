import java.sql.Date;

public class Author extends User{
    private String nif;
    private String phone;
    private String address;
    private String literaryStyle;
    private Date startDate;

    // constructor
    public Author(String login, String password, byte[] salt, String name, String email, String nif, String phone, String address, String literaryStyle, Date startDate) {
        super(login, password, salt, name, email, "author", "inactive");
        this.nif = nif;
        this.phone = phone;
        this.address = address;
        this.literaryStyle = literaryStyle;
        this.startDate = startDate;
    }

    // getters, setters, and other author-specific methods
    public static Author register () {
        Main.clearConsole();
        String name = Validator.validateInput("Name", false);
        String email = Validator.validateInput("Email", true);
        String nif = Validator.validateInput("NIF", true);
        String phone = Validator.validateInput("Phone number", false);
        String address = Validator.validateInput("Address", false);
        String style = Validator.validateInput("Literary style", false);
        Date startDate = Validator.validateDate();
        String login = Validator.validateInput("Login", true);
        byte[] salt = Security.generateSalt();
        String password = Validator.validatePassword(salt);
        return new Author(login, password, salt, name, email, nif, phone, address, style, startDate);
    }
}