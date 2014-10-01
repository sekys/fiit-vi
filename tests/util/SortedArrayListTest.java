package util;

import junit.framework.TestCase;
import org.junit.Assert;
import parser.ParsePersons;
import parser.Person;

import java.util.List;

public class SortedArrayListTest extends TestCase {

    public void testIndexOf() throws Exception {
        List<Person> list = new SortedArrayList<Person>(100, new ParsePersons.PersonFinderComparator());
        list.add(new Person("A"));
        list.add(new Person("Z"));
        list.add(new Person("C"));
        list.add(new Person("G"));
        list.add(new Person("G"));
        list.add(new Person("F"));
        list.add(new Person("68"));
        Assert.assertTrue(list.size() == 7);
        Assert.assertTrue(list.indexOf("68") == 0);
        Assert.assertTrue(list.indexOf("A") == 1);
        Assert.assertTrue(list.indexOf("C") == 2);
        Assert.assertTrue(list.indexOf("F") == 3);
        Assert.assertTrue(list.indexOf("G") == 4);
        Assert.assertTrue(list.indexOf("G") == 4);
        Assert.assertTrue(list.indexOf("Z") == 6);
    }


}