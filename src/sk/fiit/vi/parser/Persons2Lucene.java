package sk.fiit.vi.parser;

import org.apache.log4j.Logger;
import sk.fiit.vi.util.Configuration;
import sk.fiit.vi.util.GZIP;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Seky on 1. 10. 2014.
 */
public class Persons2Lucene {
    private static final Logger LOGGER = Logger.getLogger(Persons2Lucene.class.getName());

    private ArrayList<Person> people;

    public static void main(String[] args) throws Exception {
        Configuration.getInstance();
        Persons2Lucene p = new Persons2Lucene();
        p.parse();
    }

    public void parse() throws Exception {
        people = (ArrayList<Person>) GZIP.deserialize(new File(StructurePeople.DATA_DIR, "outcomePersons.gz"));

    }
}
