import java.sql.Date;
import java.util.Map;
import java.util.function.Function;
public class Author extends User{
    private String nif;
    private String phone;
    private String address;
    private String literaryStyle;
    private Date startDate;

    // constructor
    public Author(String login, String password, String name, String email, String nif, String phone, String address, String literaryStyle, Date startDate) {
        super(login, password, name, email, "author", "inactive");
        this.nif = nif;
        this.phone = phone;
        this.address = address;
        this.literaryStyle = literaryStyle;
        this.startDate = startDate;
    }

    // getters, setters, and other author-specific methods
    private String getNIF () {
        return nif;
    }

    private String getPhone () {
        return phone;
    }

    private String getAddress () {
        return address;
    }

    private String getLiteraryStyle () {
        return literaryStyle;
    }

    private Date getStartDate () {
        return startDate;
    }

    public static Author register (byte[] salt) {
        Main.clearConsole();
        String name = Validator.validateInput("Name", false);
        String email = Validator.validateInput("Email", true);
        String nif = Validator.validateInput("NIF", true);
        String phone = Validator.validateInput("Phone number", false);
        String address = Validator.validateInput("Address", false);
        String style = Validator.validateInput("Literary style", false);
        Date startDate = Validator.validateDate();
        String login = Validator.validateInput("Login", true);
        String password = Validator.validatePassword(salt);
        return new Author(login, password, name, email, nif, phone, address, style, startDate);
    }

    private static final Map<String, Function<Author, String>> getters = Map.of(
        "nif", Author::getNIF,
        "phone", Author::getPhone,
        "address", Author::getAddress,
        "style", Author::getLiteraryStyle
    );

    public static String getValue (Author user, String value) {
        if (value.equals("date")) {
            return user.getStartDate().toString();
        } else {
            return getters.get(value.toLowerCase()).apply(user);
        }
    }
}