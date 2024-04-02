import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
public class Author extends User{
    private String nif;
    private String phone;
    private String address;
    private String literaryStyle;
    private Date startDate;

    // constructor
    public Author(String login, String password, String name, String email,String status, String nif, String phone, String address, String literaryStyle, Date startDate) {
        super(login, password, name, email, "author", status);
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
        String password = Validator.validatePassword(salt, "Password");
        String status = "pending-activation";
        return new Author(login, password, name, email, status, nif, phone, address, style, startDate);
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

    public static void loggedUserLoop(Author user) {
        boolean running = true;
        while (running) {
            Main.clearConsole();
            System.out.println("Logged as " + User.getValue(user, "name") + "!");
            System.out.println("1. Delete account");
            System.out.println("2. Edit profile");
            System.out.println("3. Insert new book");
            System.out.println("4. Ask for review");
            System.out.println("5. List reviews");
            System.out.println("0. Log out");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    if (Database.requestAccountDeletion(user)) {
                        running = false;
                    }
                    break;
                case "2":
                    String userID = Database.convertUsernameToID(User.getValue(user, "login"));
                    if (userID != null) {
                        Database.manageExistingUserByID(userID, User.getValue(user, "type"));
                    } else {
                        System.out.println("An error occurred while trying to edit your profile.");
                        Main.pressEnterKey();
                    }
                    break;
                case "3":
                    Book book = Book.createBook(user);
                    Database.insertBookIntoDatabase(book);
                    break;
                case "4":
                    initiateReviewProcess(user);
                    break;
                case "5":
                    Review.listReviews(user);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    Main.clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    Main.pressEnterKey();
                    break;
            }
        }
    }

    private static void initiateReviewProcess(Author author) {
        boolean running = true;
        while (running) {
            Main.clearConsole();
            System.out.println("Initiate review process:");
            System.out.println("1. List books");
            System.out.println("0. Go back");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    paginationMenu(author);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    Main.clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    Main.pressEnterKey();
                    break;
            }
        }
    }

    public static void paginationMenu(Author author) {
        int page = 1;
        int pageSize = 10;
        int totalBooks = Database.getBookCount(author);
        ArrayList<String> ids = new ArrayList<>();
        while (true) {
            ResultSet rs = Database.getBooks(page, pageSize, author);
            if (totalBooks > 0) {
                ids = displayBooks(rs);
                String option = handlePagination(totalBooks, page, pageSize, ids);
                try {
                    switch (option) {
                        case "next":
                            page++;
                            break;
                        case "previous":
                            page--;
                            break;
                        case "exit":
                            return;
                        default:
                            Review review = Review.startReviewProcess(author, Database.getBookByID(author, option));
                            Database.insertReviewIntoDatabase(review);
                            totalBooks = Database.getBookCount(author);
                            break;
                    }
                } catch (NullPointerException e) {
                    continue;
                }
            } else {
                Main.clearConsole();
                System.out.println("No books found.");
                Main.pressEnterKey();
                return;
            }
        }
    }

    private static ArrayList<String> displayBooks(ResultSet rs) {
        Main.clearConsole();
        ArrayList<String> ids = new ArrayList<>();
        System.out.println("Next page: n | Previous page: p | Go back: 0\n");
        try {
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("id_obra"));
                System.out.println("Title: " + rs.getString("titulo"));
                if (rs.getString("subtitulo") != null) {
                    System.out.println("Subtitle: " + rs.getString("subtitulo"));
                }
                System.out.println("Literary style: " + rs.getString("estilo_literario"));
                System.out.println("Publication type: " + rs.getString("tipo_publicacao"));
                System.out.println("Number of pages: " + rs.getInt("n_paginas"));
                System.out.println("Number of words: " + rs.getInt("n_palavras"));
                System.out.println("Edition: " + rs.getInt("n_edicao"));
                System.out.println("ISBN: " + rs.getString("codigo_isbn"));
                System.out.println();
                ids.add(rs.getString("id_obra"));
            }
            return ids;
        } catch (Exception e) {
            System.out.println("Failed to display books.");
            System.out.println("Exception: " + e);
            return null;
        }
    }

    private static String handlePagination(int totalBooks, int page, int pageSize, ArrayList<String> ids) {
        System.out.print("\nOption or book ID: ");
        String option = Input.readLine();
        switch (option) {
            case "n":
                if (totalBooks > page * pageSize) {
                    page++;
                    return "next";
                } else {
                    Main.clearConsole();
                    System.out.println("There are no more pages.");
                    Main.pressEnterKey();
                }
                break;
            case "p":
                if (page > 1) {
                    page--;
                    return "previous";
                } else {
                    Main.clearConsole();
                    System.out.println("There are no previous pages.");
                    Main.pressEnterKey();
                }
                break;
            case "0":
                return "exit";
            default:
                if (ids.contains(option)) {
                    return option;
                } else {
                    Main.clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    Main.pressEnterKey();
                }
                break;
        }
        return null;
    }
}