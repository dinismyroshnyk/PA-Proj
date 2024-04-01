import java.sql.Date;

public class Note {
    private String description;
    private Date date;
    private int page;
    private int paragraph;

    // constructor
    public Note (String description, Date date, int page, int paragraph) {
        this.description = description;
        this.date = date;
        this.page = page;
        this.paragraph = paragraph;
    }

    // getters, setters, and other note-specific methods
}
