package sk.fiit.vi.parser;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seky on 30. 9. 2014.
 */
public class DateFormats {
    private final List<DateTimeFormatter> formats;

    public DateFormats() {
        formats = new ArrayList<>(10);
        add("YYYY-MM-dd");
        add("YYYY-MM");
        add("YYYY");

        DateTime found = null;
        try {
            found = format("1891-10");
        } catch (IllegalArgumentException e) {
        }

    }

    private void add(String dateFormat) {
        formats.add(DateTimeFormat.forPattern(dateFormat).withChronology(ISOChronology.getInstance()));
    }

    public DateTime format(String value) {
        boolean era = false;
        if (value.startsWith("-")) {
            value = value.substring(1, value.length());
            era = true;
        }
        // dalsie atributy datumu ignorujeme nezaujimaju nas hodiny alebo minuty
        if (value.length() > 10) {
            value = value.substring(0, 10); // orez hodiny a minuty
        }

        DateTime found = null;
        for (DateTimeFormatter fmt : formats) {
            try {
                found = fmt.parseDateTime(value);
            } catch (IllegalArgumentException e) {
            }
        }

        if (found == null) {
            throw new IllegalArgumentException("Unknow date format" + value);
        }

        if (era) {
            return found.withYear(-found.getYear());
        }
        return found;
    }
}
