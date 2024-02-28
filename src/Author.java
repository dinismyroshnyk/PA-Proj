public class Author extends User{
    private String NIF;
    private String address;
    private String phone;
    private String LiteraryGenre;
    private Date activeSince;

    // constructor
    public Author(String login, String password, String name, String email, String NIF, String address, String phone, String LiteraryGenre, Date activeSince) {
        super(login, password, name, email, NIF, address, phone, "active", "Author");
        this.NIF = NIF;
        this.address = address;
        this.phone = phone;
        this.LiteraryGenre = LiteraryGenre;
        this.activeSince = activeSince;
    }

    // getters, setters, and other author-specific methods
    public static Author register(String login, String password, String name, String email, String NIF, String address, String phone, String LiteraryGenre, Date activeSince) {
        return new Reviewer(login, password, name, email, NIF, address, phone, LiteraryGenre, activeSince);
    }
}