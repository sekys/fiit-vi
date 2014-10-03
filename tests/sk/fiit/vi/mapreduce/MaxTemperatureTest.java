package sk.fiit.vi.mapreduce;

import junit.framework.TestCase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.lib.IdentityMapper;
import org.junit.Before;
import org.junit.Test;

public class MaxTemperatureTest extends TestCase {

    private Mapper mapper;
    //private MapDriver driver;

    @Before
    public void setUp() {
        mapper = new IdentityMapper();
        //driver = new MapDriver(mapper);
    }

    @Test
    public void testIdentityMapper() {

    }
}