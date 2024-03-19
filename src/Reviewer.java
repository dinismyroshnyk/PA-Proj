import java.util.Map;
import java.util.function.Function;

public class Reviewer extends User {
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
    private String getNIF () {
        return nif;
    }

    private String getPhone () {
        return phone;
    }

    private String getAddress () {
        return address;
    }

    private String getSpecialization () {
        return specialization;
    }

    private String getAcademicBackground () {
        return academicBackground;
    }

    public static Reviewer register (byte[] salt) {
        Main.clearConsole();
        String name = Validator.validateInput("Name", false);
        String email = Validator.validateInput("Email", true);
        String nif = Validator.validateInput("NIF", true);
        String phone = Validator.validateInput("Phone number", false);
        String address = Validator.validateInput("Address", false);
        String specialization = Validator.validateInput("Specialization", false);
        String academicBackground = Validator.validateInput("Academic background", false);
        String login = Validator.validateInput("Login", true);
        String password = Validator.validatePassword(salt);
        return new Reviewer(login, password, name, email, nif, phone, address, specialization, academicBackground);
    }

    private static final Map<String, Function<Reviewer, String>> getters = Map.of(
        "nif", Reviewer::getNIF,
        "phone", Reviewer::getPhone,
        "address", Reviewer::getAddress,
        "specialization", Reviewer::getSpecialization,
        "academic background", Reviewer::getAcademicBackground
    );

    public static String getValue (Reviewer user, String value) {
        return getters.get(value.toLowerCase()).apply(user);
    }
}