package peopleCouldMeet.parser;

import org.apache.log4j.Logger;
import peopleCouldMeet.util.GZIP;
import peopleCouldMeet.util.Lucene;

import java.util.ArrayList;

/**
 * Created by Seky on 1. 10. 2014.
 * Trieda sa stara o nacitanie osob a ich presun o lucene.
 */
public class Persons2Lucene {
    private static final Logger LOGGER = Logger.getLogger(Persons2Lucene.class.getName());

    public static void main(String[] args) throws Exception {
        LOGGER.info("Deserializing start");
        ArrayList<Person> people;
        people = (ArrayList<Person>) GZIP.deserialize(StructurePeople.FILE_STRUCTURED_PEOPLE);

        LOGGER.info("Deserialized");
        Lucene.getInstance().addPeople(people);
        LOGGER.info("Added people");
        Lucene.getInstance().close();
        LOGGER.info("done");
    }
}
