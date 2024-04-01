import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

public class License {
    private String licenseNumber;
    private Date validFrom;
    private Date validTo;
    private int usageLimit;
    private int usageCount;
    private String comments;

    // constructor
    public License(String licenseNumber, Date validFrom, Date validTo, int usageLimit, int usageCount, String comments) {
        this.licenseNumber = licenseNumber;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.usageLimit = usageLimit;
        this.usageCount = usageCount;
        this.comments = comments;
    }

    // getters, setters, and other license-specific methods
    private String getLicenseNumber() {
        return licenseNumber;
    }

    private Date getValidFrom() {
        return validFrom;
    }

    private Date getValidTo() {
        return validTo;
    }

    private int getUsageLimit() {
        return usageLimit;
    }

    private int getUsageCount() {
        return usageCount;
    }

    private String getComments() {
        return comments;
    }

    private static final Map<String, Function<License, Object>> values = Map.of(
        "licenseNumber", License::getLicenseNumber,
        "validFrom", License::getValidFrom,
        "validTo", License::getValidTo,
        "usageLimit", License::getUsageLimit,
        "usageCount", License::getUsageCount,
        "comments", License::getComments
    );

    public static Object getValue(License license, String key) {
        if (values.containsKey(key)) {
            return values.get(key).apply(license);
        } else {
            return null;
        }
    }

    public static License insertLicense() {
        Main.clearConsole();
        String licenseNumber = Validator.validateInput("License number", false);
        String comments = Validator.validateInput("Comments (optional)", false);
        Date validFrom = Main.getCurrentDate();
        Date validTo = addYears(validFrom, 1);
        int usageLimit = Validator.validateInt("Usage limit");
        return new License(licenseNumber, validFrom, validTo, usageLimit, 0, comments);
    }

    private static Date addYears(Date date, int years) {
        LocalDate localDate = date.toLocalDate();
        localDate = localDate.plusYears(years);
        return Date.valueOf(localDate);
    }
}
