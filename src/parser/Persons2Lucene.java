package parser;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import util.Configuration;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
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
        people = (ArrayList<Person>) SerializationUtils.deserialize(new BufferedInputStream(new FileInputStream("data/outcomePersons")));

    }
}
