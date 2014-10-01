package parser;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import util.GZIP;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Seky on 30. 9. 2014.
 */
public class ParsePersons {
    private static final Logger LOGGER = Logger.getLogger(ParsePersons.class.getName());
    private static final Pattern RDF_PATTER = Pattern.compile("^<([^>]+)>\\s<([^>]+)>\\s\"([^\"]+)\"..<([^>]+)>\\s.*$");

    private final Map<String, Person> people;
    private final DateFormats dates = new DateFormats();

    public ParsePersons() {
        people = new HashMap<>(1000000);
    }

    public static void main(String[] args) throws Exception {
        ParsePersons p = new ParsePersons();
        p.parseBirth();
    }

    public void parseBirth() throws Exception {
        BufferedReader in = GZIP.read("data/deceased_persons.gz");
        //BufferedReader in = GZIP.read("data/zbirths.gz");

        String line;
        while ((line = in.readLine()) != null) {
            try {
                parseLine(line);
            } catch (Exception e) {
                LOGGER.warn("ignoring " + line);
            }
        }
    }

    protected void parseLine(String line) throws Exception {
        // Parse line
        Matcher matcher = RDF_PATTER.matcher(line);
        if (matcher.matches()) {
            parseRdf(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
        } else {
            throw new Exception("Line is bad formatted.");
        }
    }

    private static final String SUBJECT_ID_PREFIX = "http://rdf.freebase.com/ns/";

    protected String parseID(String subject) throws Exception {
        // like http://rdf.freebase.com/ns/m.0100kt2c
        if (!subject.startsWith(SUBJECT_ID_PREFIX)) {
            throw new Exception("prefix nesedi");
        }
        return subject.substring(SUBJECT_ID_PREFIX.length(), subject.length());
    }

    protected void parseRdf(String subject, String predicate, String value, String object) throws Exception {
        String id = parseID(subject);
        DateTime date = dates.format(value);

        Person e = people.get(id);
        if (e == null) {
            e = new Person();
            e.setBirth(date);
            people.put(id, e);
        } else {
            LOGGER.error("uzivatel je tu 2x");
        }
    }
}
