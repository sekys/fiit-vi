package parser;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Seky on 30. 9. 2014.
 */
public enum DateFormats {
    DATE("http://www.w3.org/2001/XMLSchema#gYear", "YYYY-mm-dd"),
    GYEAR("http://www.w3.org/2001/XMLSchema#gYear", "Y");

    private final String url;
    private final DateTimeFormatter fmt;

    DateFormats(String url, String dateFormat) {
        this.url = url;
        fmt = DateTimeFormat.forPattern(dateFormat);
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
