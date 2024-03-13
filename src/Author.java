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
    public static Author register (String login, String password, String name, String email, String nif, String phone, String address, String literaryStyle, String startDate) {
        return new Author(login, password, name, email, nif, phone, address, literaryStyle, startDate);
    }
}