package peopleCouldMeet.parser;

import junit.framework.TestCase;
import org.junit.Assert;
import peopleCouldMeet.util.GZIP;

import java.util.ArrayList;

public class Persons2LuceneTest extends TestCase {

    public void testMain() throws Exception {
        ArrayList<Person> people;
        people = (ArrayList<Person>) GZIP.deserialize(StructurePeople.FILE_STRUCTURED_PEOPLE);
        Assert.assertTrue(!people.isEmpty());
    }
}
