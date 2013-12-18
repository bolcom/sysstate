package nl.unionsoft.sysstate.common.util;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class PropertyGroupUtilTest {

    private Properties properties;

    @Before
    public void before() {
        properties = new Properties();
        properties.put("global.test1.blaat1", "blaat1Test1");
        properties.put("global.test1.blaat2", "blaat2Test1");
        properties.put("global.test2.blaat1", "blaat1Test2");
        properties.put("global.test2.blaat2", "blaat2Test2");

    }

    @Test
    public void testIds() {
        Set<String> results = PropertyGroupUtil.getGroupIds(properties, "global");
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains("test1"));
        Assert.assertTrue(results.contains("test2"));
    }

    @Test
    public void testGroupIdResolved() {
        Properties results = PropertyGroupUtil.getGroupProperties(properties, "global", "test1");
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("blaat1Test1", results.get("blaat1"));
        Assert.assertEquals("blaat2Test1", results.get("blaat2"));
    }
}


