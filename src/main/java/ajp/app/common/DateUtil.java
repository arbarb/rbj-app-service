package ajp.app.common;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.trimToNull;

public class DateUtil {

    private DateUtil() {
    }

    private final static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS");
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private final static ZoneId ZONE = ZoneId.systemDefault();

    public static Date now() {
        return toDate(LocalDate.now());
    }

    public static Date currentDate() {
        return toDate(LocalDateTime.now());
    }

    public static LocalDate currentLocalDate() {
        return LocalDate.now();
    }

    public static LocalDateTime currentLocalDateTime() {
        return LocalDateTime.now();
    }

    public static String formatDateTime(TemporalAccessor temp, String format) {
        ZonedDateTime zdt = withZone(temp);
        if (zdt == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern(format).format(zdt);
    }

    public static String formatDateTime(TemporalAccessor temp) {
        ZonedDateTime zdt = withZone(temp);
        if (zdt == null) {
            return null;
        }
        return DATETIME_FORMATTER.format(zdt);
    }

    public static String formatDateTime(Date date, String format) {
        ZonedDateTime zdt = withZone(date);
        if (zdt == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern(format).format(zdt);
    }

    public static String formatDateTime(Date date) {
        ZonedDateTime zdt = withZone(date);
        if (zdt == null) {
            return null;
        }
        return DATETIME_FORMATTER.format(zdt);
    }

    public static String formatDate(TemporalAccessor temp, String format) {
        ZonedDateTime zdt = withZone(temp);
        if (zdt == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern(format).format(zdt);
    }

    public static String formatDate(TemporalAccessor temp) {
        ZonedDateTime zdt = withZone(temp);
        if (zdt == null) {
            return null;
        }
        return DATE_FORMATTER.format(zdt);
    }

    public static String formatDate(Date date, String format) {
        ZonedDateTime zdt = withZone(date);
        if (zdt == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern(format).format(zdt);
    }

    public static String formatDate(Date date) {
        ZonedDateTime zdt = withZone(date);
        if (zdt == null) {
            return null;
        }
        return DATE_FORMATTER.format(zdt);
    }

    public static Date toDate(TemporalAccessor temp) {
        ZonedDateTime zdt = withZone(temp);
        if (zdt == null) {
            return null;
        }
        return Date.from(zdt.toInstant());
    }

    public static Date toDate(String dateTime) {
        ZonedDateTime zdt = withZone(dateTime);
        if (zdt == null) {
            return null;
        }
        return Date.from(zdt.toInstant());
    }

    public static ZonedDateTime withZone(String dateTime, String format) {
        String dt = trimToNull(dateTime);
        if (dt == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        TemporalAccessor temp = (dt.length() > 10) ?
                formatter.parse(dt, LocalDateTime::from) :
                formatter.parse(dt, LocalDate::from);
        return withZone(temp);
    }

    public static ZonedDateTime withZone(String dateTime) {
        String dt = trimToNull(dateTime);
        if (dt == null) {
            return null;
        }
        TemporalAccessor temp = (dt.length() > 10) ?
                DATETIME_FORMATTER.parse(dt, LocalDateTime::from) :
                DATE_FORMATTER.parse(dt, LocalDate::from);
        return withZone(temp);
    }

    public static ZonedDateTime withZone(TemporalAccessor temp) {
        if (temp == null) {
            return null;
        } else if (temp instanceof LocalDateTime) {
            return ((LocalDateTime) temp).atZone(ZONE);
        } else if (temp instanceof LocalDate) {
            return ((LocalDate) temp).atStartOfDay(ZONE);
        }
        return Instant.from(temp).atZone(ZONE);
    }

    public static ZonedDateTime withZone(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZONE);
    }

}
