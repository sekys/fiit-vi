package parser;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import util.GZIP;
import util.SortedArrayList;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Seky on 30. 9. 2014.
 */
public class ParsePersons {
    private static final Pattern RDF_PATTER = Pattern.compile("^<([^>]+)>\\s<([^>]+)>\\s<([^>]+)>\\s.*$");
    private static final Pattern RDF_DATE_PATTER = Pattern.compile("^<([^>]+)>\\s<([^>]+)>\\s\"([^\"]+)\"..<([^>]+)>\\s.*$");
    private static final Pattern RDF_NAME_PATTER = Pattern.compile("^<([^>]+)>\\s<([^>]+)>\\s\"([^\"]+)\"@([^\\s]+).*$");
    private static final String SUBJECT_ID_PREFIX = "http://rdf.freebase.com/ns/m.";
    private static final Logger LOGGER = Logger.getLogger(ParsePersons.class.getName());

    private final SortedArrayList<Person> people;
    private final DateFormats dates;

    public ParsePersons() {
        people = new SortedArrayList<Person>(5000000, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Person) o1).getId().compareTo((String) o2);
            }
        });
        dates = new DateFormats();
    }

    public static void main(String[] args) throws Exception {
        ParsePersons p = new ParsePersons();
        p.parseFiles();
    }

    private void parseFile(String file, Pattern pattern, IParsing parsing) throws Exception {
        LOGGER.info("Starting parse: " + file);
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

    private IParsing parsePersonID() {
        return new IParsing() {
            @Override
            public void parseRdf(Matcher m) {
                String id = parseID(m.group(1));
                people.add(new Person(id));
            }
        };
    }

    private Person find(String id) {
        int index = people.indexOf(id);
        if (index == -1) {
            return null;
        }
        return people.get(index);
    }

    private IParsing parseBirths() {
        return new IParsing() {
            @Override
            public void parseRdf(Matcher m) {
                String id = parseID(m.group(1));
                DateTime date = dates.format(m.group(3));
                Person e = find(id);
                if (e != null) {
                    e.setBirth(date);
                } else {
                    LOGGER.warn("Uzivatel nenajdeny:" + id);
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
                Person e = find(id);
                if (e != null) {
                    e.setDeath(date);
                } else {
                    LOGGER.warn("Uzivatel nenajdeny:" + id);
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
                Person e = find(id);
                if (e != null) {
                    e.setName(name);
                } else {
                    LOGGER.warn("Uzivatel nenajdeny:" + id);
                }
            }
        };
    }

    private void parseFiles() throws Exception {
        parseFile("data/people.gz", RDF_PATTER, parsePersonID());
        parseFile("data/births.gz", RDF_DATE_PATTER, parseBirths());
        parseFile("data/deceased_persons.gz", RDF_DATE_PATTER, parseDeaths());
        parseFile("data/names.gz", RDF_NAME_PATTER, parseNames());
        serializePersons();
    }

    private void serializePersons() throws Exception {
        LOGGER.info("Starting serialize outcomePersons.");
        SerializationUtils.serialize(people, new FileOutputStream("data/outcomePersons"));
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
