package peopleCouldMeet.parser;

import peopleCouldMeet.util.GZIP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Seky on 30. 9. 2014.
 * Pomocny parser jenotlivych atributov do roznych suborov.
 * V rieseni sme pouzili rozne metody na parsovanie.
 */
public class ParseDump2Parts {
    // Files
    public static final File DIR = new File("D:\\workspace\\vi\\data\\");
    public static final File FILE_PEOPLE = new File(DIR, "people.gz");
    public static final File FILE_DEATH = new File(DIR, "deaths.gz");
    public static final File FILE_OBJECTS = new File(DIR, "objects.gz");
    public static final File FILE_BIRTHS = new File(DIR, "births.gz");
    public static final File FILE_DUMP = new File(DIR, "dump.gz");

    // Regex for specific file
    private final static Pattern REGEX_DEAD_PEOPLE = Pattern.compile(".*<http://rdf\\.freebase\\.com/ns/people\\.deceased_person>.*");
    private final static Pattern REGEX_OBJECTS = Pattern.compile(".*<http://rdf\\.freebase\\.com/ns/type\\.object\\.name>.*");
    private final static Pattern REGEX_DATE_OF_BIRTH = Pattern.compile(".*<http://rdf\\.freebase\\.com/ns/people\\.person\\.date_of_birth>.*");
    private final static int FLUSH_LIMIT = 10000; // po kolkych riadkov, sa zapisuju udaje
    private final static double TOTAL_LINES = 2746142741L; // pomocny udaj re vyhodnotenie stavu spracovania

    // List of atributes
    private List<String> m_deads;
    private List<String> m_objects;
    private List<String> m_births;

    public ParseDump2Parts() {
        m_deads = new ArrayList<>(FLUSH_LIMIT);
        m_objects = new ArrayList<>(FLUSH_LIMIT);
        m_births = new ArrayList<>(FLUSH_LIMIT);
    }

    public static void main(String[] args) throws Exception {
        ParseDump2Parts p = new ParseDump2Parts();
        p.parse();
    }

    public void parse() throws Exception {
        BufferedWriter writerForDeaths = GZIP.write(FILE_DEATH);
        BufferedWriter writerForObjects = GZIP.write(FILE_OBJECTS);
        BufferedWriter writerForBirths = GZIP.write(FILE_BIRTHS);
        BufferedReader in = GZIP.read(FILE_DUMP);

        String line;
        int processed = 0;
        double percent;
        while ((line = in.readLine()) != null) {
            processed++;

            if (REGEX_DEAD_PEOPLE.matcher(line).matches()) {
                if (m_deads.size() == FLUSH_LIMIT - 5) {
                    write(writerForDeaths, m_deads);
                    m_deads = new ArrayList<>(FLUSH_LIMIT);
                }
                m_deads.add(line);
            } else if (REGEX_OBJECTS.matcher(line).matches()) {
                if (m_objects.size() == FLUSH_LIMIT - 5) {
                    write(writerForObjects, m_objects);
                    m_objects = new ArrayList<>(FLUSH_LIMIT);
                }
                m_objects.add(line);
            } else if (REGEX_DATE_OF_BIRTH.matcher(line).matches()) {
                if (m_births.size() == FLUSH_LIMIT - 5) {
                    write(writerForBirths, m_births);
                    m_births = new ArrayList<>(FLUSH_LIMIT);
                }
                m_births.add(line);
            }

            // Show status
            if (processed % 1000000 == 0) {
                percent = (double) 100 * (((double) processed) / TOTAL_LINES);
                System.out.println(Double.toString(percent));
            }
        }

        // Flush rest data
        write(writerForDeaths, m_deads);
        write(writerForObjects, m_objects);
        write(writerForBirths, m_births);

        // Close streams
        writerForDeaths.close();
        writerForObjects.close();
        writerForBirths.close();
        in.close();
    }

    /**
     * Write data to the file
     *
     * @param writer
     * @param data
     * @throws IOException
     */
    private void write(BufferedWriter writer, List<String> data) throws IOException {
        for (String record : data) {
            writer.write(record);
            writer.newLine();
        }
        System.out.println("flushing");
        writer.flush();
    }
}
