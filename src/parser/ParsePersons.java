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
    private static final Pattern RDF_DATE_PATTER = Pattern.compile("^<([^>]+)>\\s<([^>]+)>\\s\"([^\"]+)\"..<([^>]+)>\\s.*$");
    private static final Pattern RDF_NAME_PATTER = Pattern.compile("^<([^>]+)>\\s<([^>]+)>\\s\"([^\"]+)\"@([^\\s]+).*$");
    private static final String SUBJECT_ID_PREFIX = "http://rdf.freebase.com/ns/m.";
    private static final Logger LOGGER = Logger.getLogger(ParsePersons.class.getName());


    private final Map<String, Person> people;
    private final DateFormats dates;

    public ParsePersons() {
        people = new HashMap<>(1000000);
        dates = new DateFormats();
    }

    public static void main(String[] args) throws Exception {
        ParsePersons p = new ParsePersons();
        p.parseFiles();
    }

    private void parseFile(String file, Pattern pattern, IParsing parsing) throws Exception {
        BufferedReader in = GZIP.read(file);
        String line;
        while ((line = in.readLine()) != null) {
            try {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    parsing.parseRdf(matcher);
                } else {
                    throw new IllegalArgumentException("Line is bad formatted.");
                }
            } catch (Exception e) {
                LOGGER.warn(file + " ignoring " + line);
            }
        }
    }

    private IParsing parseBirths() {
        return new IParsing() {
            @Override
            public void parseRdf(Matcher m) {
                String id = parseID(m.group(1));
                DateTime date = dates.format(m.group(3));

                Person e = people.get(id);
                if (e == null) {
                    e = new Person();
                    e.setBirth(date);
                    people.put(id, e);
                } else {
                    LOGGER.error("uzivatel je tu 2x");
                }
            }
        };
    }

    private IParsing parseDeaths() {
        return new IParsing() {
            @Override
            public void parseRdf(Matcher m) {
                String id = parseID(m.group(1));
                DateTime date = dates.format(m.group(3));

                Person e = people.get(id);
                if (e != null) {
                    e.setDeath(date);
                } else {
                    LOGGER.warn("Uzivatel nenajdeny: " + id);
                }
            }
        };
    }

    private IParsing parseNames() {
        return new IParsing() {
            @Override
            public void parseRdf(Matcher m) {
                String id = parseID(m.group(1));
                if (id == null) {
                    return;
                }
                String name = m.group(3);
                String language = m.group(4);

                Person e = people.get(id);
                if (e != null) {
                    e.setName(name);
                } else {
                    LOGGER.warn("Uzivatel nenajdeny: " + id);
                }
            }
        };
    }

    private void parseFiles() throws Exception {
        parseFile("data/zbirths.gz", RDF_DATE_PATTER, parseBirths());
        parseFile("data/deceased_persons.gz", RDF_DATE_PATTER, parseDeaths());
        parseFile("data/name.gz", RDF_NAME_PATTER, parseNames());
    }

    protected String parseID(String subject) {
        // like http://rdf.freebase.com/ns/m.0100kt2c
        if (!subject.startsWith(SUBJECT_ID_PREFIX)) {
            return null; // prefix nesedi
        }
        return subject.substring(SUBJECT_ID_PREFIX.length(), subject.length());
    }

    private interface IParsing {
        void parseRdf(Matcher m);
    }
}
