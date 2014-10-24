package peopleCouldMeet.parser;

import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.junit.Assert;

public class PersonTest extends TestCase {

    public void testEquals() throws Exception {
        Person a = new Person("4");
        Person b = new Person("4");
        Assert.assertTrue(a.equals(b));
        Assert.assertTrue(a.equals(a));
        Assert.assertTrue(b.equals(b));
    }

    public void testCompareTo() throws Exception {
        Person a = new Person("4");
        Person b = new Person("4");
        Assert.assertTrue(a.compareTo(b) == 0);
        Assert.assertTrue(a.compareTo(a) == 0);
        Assert.assertTrue(b.compareTo(b) == 0);
    }

    public void testCheckIntersection() throws Exception {
        Person a = new Person("4");
        Person b = new Person("5");
        Assert.assertTrue(a.checkIntersection(b) == null);
        a.setBirth(new DateTime(2000, 12, 10, 0, 0));
        a.setDeath(new DateTime(2020, 12, 10, 0, 0));
        Assert.assertTrue(a.checkIntersection(b) == null);
        b.setBirth(new DateTime(2002, 12, 10, 0, 0));
        Assert.assertTrue(a.checkIntersection(b) == true);
        b.setBirth(new DateTime(1992, 12, 10, 0, 0));
        Assert.assertTrue(a.checkIntersection(b) == false);
        b.setBirth(new DateTime(3000, 12, 10, 0, 0));
        Assert.assertTrue(a.checkIntersection(b) == false);
    }

    public void testToString() throws Exception {
        Person a = new Person("4");
        Assert.assertTrue(a.toString() != null);
    }
}
