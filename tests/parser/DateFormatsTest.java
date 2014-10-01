package parser;


import junit.framework.TestCase;
import org.junit.Assert;

public class DateFormatsTest extends TestCase {

    public void testFormat() throws Exception {
        DateFormats fmt = new DateFormats();
        Assert.assertTrue(fmt.format("-0219-04-29") != null);
        Assert.assertTrue(fmt.format("-0044-03") != null);
        Assert.assertTrue(fmt.format("-0045") != null);
        Assert.assertTrue(fmt.format("1992-10-14T16:10:54") != null);
        Assert.assertTrue(fmt.format("1992-10-14T16:10") != null);
        Assert.assertTrue(fmt.format("1992-10-14T16") != null);
        Assert.assertTrue(fmt.format("1992-10-14sadasdsa") != null);
        Assert.assertTrue(fmt.format("1992-10-14") != null);
        Assert.assertTrue(fmt.format("1992-10") != null);
        Assert.assertTrue(fmt.format("1992") != null);
    }
}