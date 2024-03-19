public abstract class User {
    private String login;
    private String password;
    private byte[] salt;
    private String name;
    private String email;
    private String type;
    private String status;

    // constructor
    public User(String login, String password, byte[] salt, String name, String email, String type, String status) {
        this.login = login;
        this.password = password;
        this.salt = salt;
        this.name = name;
        this.email = email;
        this.type = type;
        this.status = status;
    }

    // getters, setters, and other methods
    public static User createUser(String type) {
        switch (type) {
            case "manager":
                return Manager.register();
            case "author":
                return Author.register();
            case "reviewer":
                return Reviewer.register();
            default:
                throw new IllegalArgumentException("Invalid user type: " + type);
        }
    }
}