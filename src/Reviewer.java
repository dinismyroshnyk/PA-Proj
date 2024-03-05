public class Reviewer extends User{
    private static String nif;
    private static String phone;
    private static String address;
    private static String specialization;
    private static String academicBackground;

    // constructor
    public Reviewer(String login, String password, String name, String email, String nif, String phone, String address, String specialization, String academicBackground) {
        super(login, password, name, email, "reviewer", "active");
        this.nif = nif;
        this.phone = phone;
        this.address = address;
        this.specialization = specialization;
        this.academicBackground = academicBackground;
    }

    // getters, setters, and other reviewer-specific methods
    public static Reviewer register (String login, String password, String name, String email, String nif, String phone, String address, String specialization, String academicBackground) {
        return new Reviewer(login, password, name, email, nif, phone, address, specialization, academicBackground);
    }
}