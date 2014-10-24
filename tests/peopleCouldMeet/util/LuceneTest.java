package peopleCouldMeet.util;


import junit.framework.TestCase;
import org.junit.Assert;
import peopleCouldMeet.parser.Person;

import java.util.Set;

public class LuceneTest extends TestCase {

    public void testGetTextAnalyzer() throws Exception {
        Assert.assertTrue(Lucene.getInstance().getTextAnalyzer() != null);
    }

    public void testFind() throws Exception {
        Set<Person> people;
        people = Lucene.getInstance().find("Lukas");
        Assert.assertTrue(!people.isEmpty());
        Assert.assertTrue( people.size() == 2 );
        Object[] pole = people.toArray();
        Assert.assertTrue(! pole[0].equals(pole[1]) );

        people = Lucene.getInstance().find("asdadas");
        Assert.assertTrue(people.isEmpty());
    }

}
