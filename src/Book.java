import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class Book {
    private Author author;
    private String title;
    private String subtitle;
    private String literaryStyle;
    private String publicationType;
    private int numberOfPages;
    private int nuberOfWords;
    private String isbn;
    private int edition;
    private Date submissionDate;
    private Date approvalDate;

    // constructor
    public Book(Author author, String title, String subtitle, String literaryStyle, String publicationType, int numberOfPages, int nuberOfWords, String isbn, int edition, Date submissionDate, Date approvalDate) {
        this.author = author;
        this.title = title;
        this.subtitle = subtitle;
        this.literaryStyle = literaryStyle;
        this.publicationType = publicationType;
        this.numberOfPages = numberOfPages;
        this.nuberOfWords = nuberOfWords;
        this.isbn = isbn;
        this.edition = edition;
        this.submissionDate = submissionDate;
        this.approvalDate = approvalDate;
    }

    // getters, setters, and other book-specific methods
    private Author getAuthor() {
        return author;
    }

    private String getTitle() {
        return title;
    }

    private String getSubtitle() {
        return subtitle;
    }

    private String getLiteraryStyle() {
        return literaryStyle;
    }

    private String getPublicationType() {
        return publicationType;
    }

    private int getNumberOfPages() {
        return numberOfPages;
    }

    private int getNuberOfWords() {
        return nuberOfWords;
    }

    private String getISBN() {
        return isbn;
    }

    private int getEdition() {
        return edition;
    }

    private Date getSubmissionDate() {
        return submissionDate;
    }

    private Date getApprovalDate() {
        return approvalDate;
    }

    private static final Map<String, Function<Book, Object>> values = Map.ofEntries(
        Map.entry("author", Book::getAuthor),
        Map.entry("title", Book::getTitle),
        Map.entry("subtitle", Book::getSubtitle),
        Map.entry("literaryStyle", Book::getLiteraryStyle),
        Map.entry("publicationType", Book::getPublicationType),
        Map.entry("numberOfPages", Book::getNumberOfPages),
        Map.entry("numberOfWords", Book::getNuberOfWords),
        Map.entry("isbn", Book::getISBN),
        Map.entry("edition", Book::getEdition),
        Map.entry("submissionDate", Book::getSubmissionDate),
        Map.entry("approvalDate", Book::getApprovalDate)
    );

    public static Object getValue(Book book, String key) {
        if (values.containsKey(key)) {
            return values.get(key).apply(book);
        } else {
            throw new IllegalArgumentException("Invalid book key: " + key);
        }
    }

    public static Book createBook(Author author) {
        Main.clearConsole();
        String title = Validator.validateInput("Title", true);
        String subtitle = Validator.validateInput("Subtitle (optional)", false);
        String literaryStyle = Validator.validateInput("Literary style", false);
        String publicationType = Validator.validateInput("Publication type", false);
        int numberOfPages = Validator.validateInt("Number of pages");
        int nuberOfWords = Validator.validateInt("Number of words");
        int edition = Validator.validateInt("Edition");
        return new Book(author, title, subtitle, literaryStyle, publicationType, numberOfPages, nuberOfWords, generateISBN(), edition, Main.getCurrentDate(), null);
    }

    private static String generateISBN() {
        Random random = new Random();
        String isbn = random.nextBoolean() ? "978" : "979";
        do {
            for (int i = 0; i < 9; i++) {
                isbn += random.nextInt(10);
            }
            int checksum = 0;
            for (int i = 0; i < 12; i++) {
                checksum += Character.getNumericValue(isbn.charAt(i)) * (i % 2 == 0 ? 1 : 3);
            }
            checksum = 10 - (checksum % 10);
            if (checksum == 10) {
                checksum = 0;
            }
            isbn += checksum;
        } while (doesBookExist(isbn));
        return isbn;
    }

    private static boolean doesBookExist(String isbn) {
        Database.sqlQuery = new StringBuffer();
        Database.sqlQuery.append("SELECT * FROM OBRAS WHERE codigo_isbn = ?");
        PreparedStatement ps = null;
        try {
            ps = Database.conn.prepareStatement(Database.sqlQuery.toString());
            ps.setString(1, isbn);
            Database.rs = ps.executeQuery();
            if (Database.rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Failed to check if book exists.");
            System.out.println("Exception: " + e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement.");
                    System.out.println("Exception: " + e);
                }
            }
        }
        return false;
    }
}