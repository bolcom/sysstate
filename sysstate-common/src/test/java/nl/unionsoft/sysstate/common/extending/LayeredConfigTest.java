package nl.unionsoft.sysstate.common.extending;

import junit.framework.Assert;

import org.junit.Test;

public class LayeredConfigTest {
    @Test
    public void testLayeredConfig() {
        LayeredMap<String, String> layeredConfig = new LayeredMap<String, String>();
        layeredConfig.put("harry", "potter");
        Assert.assertEquals("potter", layeredConfig.get("harry"));
    }

    @Test
    public void testLayeredConfigParent() {
        LayeredMap<String, String> parentLayeredConfig = new LayeredMap<String, String>();
        parentLayeredConfig.put("harry", "mulisch");
        parentLayeredConfig.put("leo", "vroman");

        LayeredMap<String, String> layeredConfig = new LayeredMap<String, String>(parentLayeredConfig);
        layeredConfig.put("harry", "potter");

        Assert.assertEquals("potter", layeredConfig.get("harry"));
        Assert.assertEquals("vroman", layeredConfig.get("leo"));
        Assert.assertEquals("mulisch", layeredConfig.getParent().get("harry"));
    }
}
