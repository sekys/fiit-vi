package sk.fiit.vi.parser;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seky on 30. 9. 2014.
 * Trieda sa stara ocistenie a konverziu datumu vo formate
 * retazca do strukturovanych dat.
 */
public class DateFormats {
    private final List<DateTimeFormatter> formats;

    /**
     * V konstruktore sa nacitaju rozne formaty datumov
     */
    public DateFormats() {
        formats = new ArrayList<>(10);
        add("YYYY-MM-dd");
        add("YYYY-MM");
        add("YYYY");
    }

    private void add(String dateFormat) {
        formats.add(DateTimeFormat.forPattern(dateFormat).withChronology(ISOChronology.getInstance()));
    }

    /**
     * Oparsuj datum.
     *
     * @param value
     * @return
     */
    public DateTime parse(String value) {
        boolean era = false;

        // Ide o datumm, ktory vznikol pred erou?
        if (value.startsWith("-")) {
            value = value.substring(1, value.length());
            era = true;
        }

        // Dalsie atributy datumu ignorujeme, tzv nezaujimaju nas hodiny alebo minuty
        if (value.length() > 10) {
            value = value.substring(0, 10); // orez hodiny a minuty
        }

        // Najdi, ktory format datumu vyhovuje retazcu
        DateTime found = null;
        for (DateTimeFormatter fmt : formats) {
            try {
                found = fmt.parseDateTime(value);
            } catch (IllegalArgumentException e) {
            }
        }

        // Ide o neznamy format
        if (found == null) {
            throw new IllegalArgumentException("Unknow date format" + value);
        }

        if (era) {
            return found.withYear(-found.getYear());
        }
        return found;
    }
}
