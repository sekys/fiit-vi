package sk.fiit.vi.parser;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import sk.fiit.vi.util.Configuration;
import sk.fiit.vi.util.GZIP;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Seky on 30. 9. 2014.
 */
public class StructurePeople {
    private static final Pattern RDF_PATTER = Pattern.compile("^<([^>]+)>\\s<([^>]+)>\\s<([^>]+)>.*");
    private static final Pattern RDF_DATE_PATTER = Pattern.compile("^<([^>]+)>\\s<([^>]+)>\\s\"([^\"]+)\"..<([^>]+)>\\s.*$");
    private static final Pattern RDF_NAME_PATTER = Pattern.compile("^<([^>]+)>\\s<([^>]+)>\\s\"([^\"]+)\"@([^\\s]+).*$");
    private static final String SUBJECT_ID_PREFIX = "http://rdf.freebase.com/ns/m.";
    private static final Logger LOGGER = Logger.getLogger(StructurePeople.class.getName());
    public static final File FILE_STRUCTURED_PEOPLE = new File(Configuration.getInstance().getDataDir(), "outcomePersons.gz");

    private final ArrayList<Person> people;
    private final DateFormats dates;


    public static class PersonFinderComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            if (o2 instanceof Person) {
                return ((Person) o1).compareTo((Person) o2);
            }
            return ((Person) o1).getId().compareTo((String) o2); // toto sa pouzije pri indexOf
        }
    }

    public StructurePeople() {
        people = new ArrayList<Person>(5000000);
        dates = new DateFormats();
    }

    public static void main(String[] args) throws Exception {
        Configuration.getInstance();
        StructurePeople p = new StructurePeople();
        p.parseFiles();
    }

    private void parseFile(File file, Pattern pattern, IParsing parsing) throws Exception {
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
        int index = Collections.binarySearch(people, new Person(id));
        if (index < 0) {
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
                    e.addName(name);
                }
            }
        };
    }

    private void parseFiles() throws Exception {
        parseFile(ParseDump2Parts.FILE_PEOPLE, RDF_PATTER, parsePersonID());
        LOGGER.info("Sorting");
        Collections.sort(people);
        parseFile(ParseDump2Parts.FILE_BIRTHS, RDF_DATE_PATTER, parseBirths());
        parseFile(ParseDump2Parts.FILE_DEATH, RDF_DATE_PATTER, parseDeaths());
        parseFile(ParseDump2Parts.FILE_OBJECTS, RDF_NAME_PATTER, parseNames());
        LOGGER.info("Starting serialize outcomePersons.");
        GZIP.serialize(people, FILE_STRUCTURED_PEOPLE);
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
