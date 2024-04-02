import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Review {
    private int id;
    private Book book;
    private Author author;
    private Manager manager;
    private Reviewer reviewer;
    private String serialNumber;
    private Timestamp submissionDate;
    private Time elapsedTime;
    private List<Note> notes;
    private String generalComments;
    private String status;
    private float cost;

    // constructor
    public Review(int id, Book book, Author author, Manager manager, Reviewer reviewer, String serialNumber, Timestamp reviewDate, Time elapsedTime, List<Note> notes, String generalComments, String status, float cost) {
        this.id = id;
        this.book = book;
        this.author = author;
        this.manager = manager;
        this.reviewer = reviewer;
        this.serialNumber = serialNumber;
        this.submissionDate = reviewDate;
        this.elapsedTime = elapsedTime;
        this.notes = notes;
        this.generalComments = generalComments;
        this.status = status;
        this.cost = cost;
    }

    // getters, setters, and other review-specific methods
    private int getId() {
        return id;
    }

    private Book getBook() {
        return book;
    }

    private Author getAuthor() {
        return author;
    }

    private Manager getManager() {
        return manager;
    }

    private Reviewer getReviewer() {
        return reviewer;
    }

    private String getSerialNumber() {
        return serialNumber;
    }

    private Timestamp getSubmissionDate() {
        return submissionDate;
    }

    private Time getElapsedTime() {
        return elapsedTime;
    }

    private List<Note> getNotes() {
        return notes;
    }

    private String getGeneralComments() {
        return generalComments;
    }

    private String getStatus() {
        return status;
    }

    private float getCost() {
        return cost;
    }

    private static final Map<String, Function<Review, Object>> values = Map.ofEntries(
        Map.entry("id", Review::getId),
        Map.entry("book", Review::getBook),
        Map.entry("author", Review::getAuthor),
        Map.entry("manager", Review::getManager),
        Map.entry("reviewer", Review::getReviewer),
        Map.entry("serialNumber", Review::getSerialNumber),
        Map.entry("submissionDate", Review::getSubmissionDate),
        Map.entry("elapsedTime", Review::getElapsedTime),
        Map.entry("notes", Review::getNotes),
        Map.entry("generalComments", Review::getGeneralComments),
        Map.entry("status", Review::getStatus),
        Map.entry("cost", Review::getCost)
    );

    public static Object getValue(Review review, String key) {
        if (values.containsKey(key)) {
            return values.get(key).apply(review);
        } else {
            return null;
        }
    }

    public static Review startReviewProcess(Author author, Book book) {
        String serialNumber = generateUniqueSerialNumber();
        int id = generateUniqueID();
        return new Review(id, book, author, null, null, serialNumber, null, null, null, null, "initiated", 0);
    }

    private static String generateUniqueSerialNumber() {
        int currReviewID = Database.getReviewsCount("all") + 1;
        String serialNumber = "";
        boolean isUnique = false;
        do {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            serialNumber = currReviewID + now.format(formatter);
            Database.sqlQuery = new StringBuffer();
            Database.sqlQuery.append("SELECT * FROM REVISOES WHERE n_serie = ?");
            PreparedStatement ps = null;
            try {
                ps = Database.conn.prepareStatement(Database.sqlQuery.toString());
                ps.setString(1, serialNumber);
                Database.rs = ps.executeQuery();
                if (Database.rs.next()) {
                    isUnique = false;
                } else {
                    isUnique = true;
                }
            } catch (SQLException e) {
                System.out.println("Failed to check if serial number exists.");
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
        } while (!isUnique);
        return serialNumber;
    }

    private static int generateUniqueID() {
        int id = 0;
        boolean isUnique = false;
        do {
            id = (int) (Math.random() * 1000000) + 1;
            Database.sqlQuery = new StringBuffer();
            Database.sqlQuery.append("SELECT * FROM REVISOES WHERE id_revisao = ?");
            PreparedStatement ps = null;
            try {
                ps = Database.conn.prepareStatement(Database.sqlQuery.toString());
                ps.setInt(1, id);
                Database.rs = ps.executeQuery();
                if (Database.rs.next()) {
                    isUnique = false;
                } else {
                    isUnique = true;
                }
            } catch (SQLException e) {
                System.out.println("Failed to check if ID exists.");
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
        } while (!isUnique);
        return id;
    }

    
}