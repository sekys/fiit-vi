package sk.fiit.vi.parser;

import sk.fiit.vi.util.Configuration;
import sk.fiit.vi.util.GZIP;
import sk.fiit.vi.util.Lucene;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Seky on 1. 10. 2014.
 */
public class Persons2Lucene {
    public static void main(String[] args) throws Exception {
        Configuration.getInstance();
        ArrayList<Person> people;
        people = (ArrayList<Person>) GZIP.deserialize(new File(Configuration.getInstance().getDataDir(), "outcomePersons.gz"));
        Lucene.getInstance().addPeople(people);
    }
}
