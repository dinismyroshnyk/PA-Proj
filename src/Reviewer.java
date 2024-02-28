public class Reviewer extends User{
    private String NIF;
    private String address;
    private String phone;
    private String specialty;
    private Date academicDegree;

    // constructor
    public Reviewer(String login, String password, String name, String email, String NIF, String address, String phone, String specialty, Date academicDegree) {
        super(login, password, name, email, NIF, address, phone, "active", "Reviewer");
        this.NIF = NIF;
        this.address = address;
        this.phone = phone;
        this.specialty = specialty;
        this.academicDegree = academicDegree;
    }

    // getters, setters, and other reviewer-specific methods
    public static Reviewer register(String login, String password, String name, String email, String NIF, String address, String phone, String specialty, Date academicDegree) {
        return new Reviewer(login, password, name, email, NIF, address, phone, specialty, academicDegree);
    }
}
