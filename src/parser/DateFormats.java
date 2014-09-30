package parser;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Seky on 30. 9. 2014.
 */
public enum DateFormats {
    DATE("http://www.w3.org/2001/XMLSchema#date", "YYYY-MM-dd"),
    GYEAR("http://www.w3.org/2001/XMLSchema#gYear", "YYYY") {
        public DateTime format(String date) {
            if (date.startsWith("-")) {
                date = date.substring(1, date.length());
                DateTime parsed = fmt.parseDateTime(date);
                return parsed.withYear(-parsed.getYear());
            }
            return fmt.parseDateTime(date);
        }
    },
    GMONTH("http://www.w3.org/2001/XMLSchema#gYearMonth", "YYYY-MM"),
    DATETIME("http://www.w3.org/2001/XMLSchema#dateTime", "YYYY-MM-dd") {
        public DateTime format(String date) {
            return fmt.parseDateTime(date.substring(0, 10));
        }
    };

    private final String url;
    protected final DateTimeFormatter fmt;

    DateFormats(String url, String dateFormat) {
        this.url = url;
        fmt = DateTimeFormat.forPattern(dateFormat).withChronology(ISOChronology.getInstance());
    }

    @Override
    public String toString() {
        return url;
    }

    public DateTime format(String date) {
        return fmt.parseDateTime(date);
    }

    public String getUrl() {
        return url;
    }
}
