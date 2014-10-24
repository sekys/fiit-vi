package peopleCouldMeet.parser;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import peopleCouldMeet.util.GZIP;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Seky on 30. 9. 2014.
 * Triedama na starosti spojit jednotlive atributy ludi.
 * Kazdy atribut je v  ssamostatnom subore. Ako atribut
 * sa berie ID, MENO, DATUM NARODENIA, DATUM SMRTI.
 * Poznamka: ID atribut sa negeneruje ale berie sa povodny z RDF
 */
public class StructurePeople {
    /**
     * Subor, kde sa zapise vystup.
     */
    public static final File FILE_STRUCTURED_PEOPLE = new File(ParseDump2Parts.DIR, "outcomePersons.gz");
    /**
     * Pattern pre ID atribut.
     */
    private static final Pattern RDF_PATTER = Pattern.compile("^<([^>]+)>\\s<([^>]+)>\\s<([^>]+)>.*");
    /**
     * Pattern pre atributy datumu.
     */
    private static final Pattern RDF_DATE_PATTER = Pattern.compile("^<([^>]+)>\\s<([^>]+)>\\s\"([^\"]+)\"..<([^>]+)>\\s.*$");
    /**
     * Pattern pre mena ludi.
     */
    private static final Pattern RDF_NAME_PATTER = Pattern.compile("^<([^>]+)>\\s<([^>]+)>\\s\"([^\"]+)\"@([^\\s]+).*$");
    /**
     * Prefix pred ID atributom.
     */
    private static final String SUBJECT_ID_PREFIX = "http://rdf.freebase.com/ns/m.";
    /**
     * Zaznamenavanie udalosti
     */
    private static final Logger LOGGER = Logger.getLogger(StructurePeople.class.getName());
    private final ArrayList<Person> people;
    private final DateFormats dates;

    public StructurePeople() {
        people = new ArrayList<Person>(5000000);
        dates = new DateFormats();
    }

    public static void main(String[] args) throws Exception {
        StructurePeople p = new StructurePeople();
        p.parseFiles();
    }

    /**
     * Vseobecna metoda na parsovanie RDF suboru.
     *
     * @param file    Cesta k suboru.
     * @param pattern RDF pattern, ktory sa pouzije na filtraciu.
     * @param parsing Vysledok sa posiela do parsing.
     * @throws Exception
     */
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

    /**
     * Metoda, ktora zachyti ID atribut cloveka. Atribut sa prida do struktury.
     *
     * @return
     */
    private IParsing parsePersonID() {
        return new IParsing() {
            @Override
            public void parseRdf(Matcher m) {
                String id = parseID(m.group(1));
                people.add(new Person(id));
            }
        };
    }

    /**
     * Pomocna metoda, ktora na zaklade ID atributu vrati objekt, ktory zoskupuje atributy.
     *
     * @param id
     * @return Vrati objekt Person s rovnakym ID
     */
    private Person find(String id) {
        int index = Collections.binarySearch(people, new Person(id));
        if (index < 0) {
            return null;
        }
        return people.get(index);
    }

    /**
     * Metoda zachytava atribut datumu narodenia.
     * Atribut sa prida do struktury.
     */
    private IParsing parseBirths() {
        return new IParsing() {
            @Override
            public void parseRdf(Matcher m) {
                String id = parseID(m.group(1));
                Person e = find(id);
                if (e != null) {
                    DateTime date = dates.parse(m.group(3));
                    e.setBirth(date);
                } else {
                    LOGGER.warn("Uzivatel nenajdeny:" + id);
                }
            }
        };
    }

    /**
     * Metoda zachytava atributu datumu smrti.
     * Atribut sa prida do struktury.
     *
     * @return
     */
    private IParsing parseDeaths() {
        return new IParsing() {
            @Override
            public void parseRdf(Matcher m) {
                String id = parseID(m.group(1));
                Person e = find(id);
                if (e != null) {
                    DateTime date = dates.parse(m.group(3));
                    e.setDeath(date);
                } else {
                    LOGGER.warn("Uzivatel nenajdeny:" + id);
                }
            }
        };
    }

    /**
     * Metoda zachyatva atribut mena cloveka.
     * Atribut sa prida do struktury.
     *
     * @return
     */
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

    /**
     * Metoda organizuje postupnost parsovania suborov.
     */
    private void parseFiles() throws Exception {
        // Nacitaj zoznam ID pre ludi a zorad tento zoznam
        parseFile(ParseDump2Parts.FILE_PEOPLE, RDF_PATTER, parsePersonID());
        LOGGER.info("Sorting");
        Collections.sort(people);

        // Nacitaj jednotlive atributy k osobe
        parseFile(ParseDump2Parts.FILE_BIRTHS, RDF_DATE_PATTER, parseBirths());
        parseFile(ParseDump2Parts.FILE_DEATH, RDF_DATE_PATTER, parseDeaths());
        parseFile(ParseDump2Parts.FILE_OBJECTS, RDF_NAME_PATTER, parseNames());

        // Uloz vysledok
        LOGGER.info("Starting serialize: " + FILE_STRUCTURED_PEOPLE);
        GZIP.serialize(people, FILE_STRUCTURED_PEOPLE);
    }

    /**
     * Pomocna metoda, ktora oddeli namespace, ako prefix, od atributu ID.
     *
     * @param subject
     * @return ID bez namespacu
     */
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

    /**
     * Porovnavac pre ludi. Tzv ludi chceme mat zoradenych
     * podla atributu ID. Aby sme vyuzili neskor binarne vyhladavanie.
     */
    public static class PersonFinderComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            if (o2 instanceof Person) {
                return ((Person) o1).compareTo((Person) o2);
            }
            return ((Person) o1).getId().compareTo((String) o2); // toto sa pouzije pri indexOf
        }
    }
}
