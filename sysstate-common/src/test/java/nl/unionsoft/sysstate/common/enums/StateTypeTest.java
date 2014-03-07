package nl.unionsoft.sysstate.common.enums;

import junit.framework.Assert;

import org.junit.Test;

public class StateTypeTest {

    @Test
    public void testTransfer() {

        Assert.assertEquals(StateType.ERROR, StateType.transfer(StateType.ERROR, StateType.ERROR));
        Assert.assertEquals(StateType.ERROR, StateType.transfer(StateType.ERROR, StateType.UNSTABLE));
        Assert.assertEquals(StateType.ERROR, StateType.transfer(StateType.ERROR, StateType.STABLE));
        Assert.assertEquals(StateType.ERROR, StateType.transfer(StateType.ERROR, StateType.PENDING));
        Assert.assertEquals(StateType.ERROR, StateType.transfer(StateType.ERROR, StateType.DISABLED));

        Assert.assertEquals(StateType.ERROR, StateType.transfer(StateType.UNSTABLE, StateType.ERROR));
        Assert.assertEquals(StateType.UNSTABLE, StateType.transfer(StateType.UNSTABLE, StateType.UNSTABLE));
        Assert.assertEquals(StateType.UNSTABLE, StateType.transfer(StateType.UNSTABLE, StateType.STABLE));
        Assert.assertEquals(StateType.UNSTABLE, StateType.transfer(StateType.UNSTABLE, StateType.PENDING));
        Assert.assertEquals(StateType.UNSTABLE, StateType.transfer(StateType.UNSTABLE, StateType.DISABLED));

        Assert.assertEquals(StateType.ERROR, StateType.transfer(StateType.STABLE, StateType.ERROR));
        Assert.assertEquals(StateType.UNSTABLE, StateType.transfer(StateType.STABLE, StateType.UNSTABLE));
        Assert.assertEquals(StateType.STABLE, StateType.transfer(StateType.STABLE, StateType.STABLE));
        Assert.assertEquals(StateType.STABLE, StateType.transfer(StateType.STABLE, StateType.PENDING));
        Assert.assertEquals(StateType.STABLE, StateType.transfer(StateType.STABLE, StateType.DISABLED));

        Assert.assertEquals(StateType.ERROR, StateType.transfer(StateType.STABLE, StateType.ERROR));
        Assert.assertEquals(StateType.UNSTABLE, StateType.transfer(StateType.STABLE, StateType.UNSTABLE));
        Assert.assertEquals(StateType.STABLE, StateType.transfer(StateType.STABLE, StateType.STABLE));
        Assert.assertEquals(StateType.STABLE, StateType.transfer(StateType.STABLE, StateType.PENDING));
        Assert.assertEquals(StateType.STABLE, StateType.transfer(StateType.STABLE, StateType.DISABLED));

        Assert.assertEquals(StateType.ERROR, StateType.transfer(StateType.PENDING, StateType.ERROR));
        Assert.assertEquals(StateType.UNSTABLE, StateType.transfer(StateType.PENDING, StateType.UNSTABLE));
        Assert.assertEquals(StateType.STABLE, StateType.transfer(StateType.PENDING, StateType.STABLE));
        Assert.assertEquals(StateType.PENDING, StateType.transfer(StateType.PENDING, StateType.PENDING));
        Assert.assertEquals(StateType.PENDING, StateType.transfer(StateType.PENDING, StateType.DISABLED));

        Assert.assertEquals(StateType.ERROR, StateType.transfer(StateType.DISABLED, StateType.ERROR));
        Assert.assertEquals(StateType.UNSTABLE, StateType.transfer(StateType.DISABLED, StateType.UNSTABLE));
        Assert.assertEquals(StateType.STABLE, StateType.transfer(StateType.DISABLED, StateType.STABLE));
        Assert.assertEquals(StateType.PENDING, StateType.transfer(StateType.DISABLED, StateType.PENDING));
        Assert.assertEquals(StateType.DISABLED, StateType.transfer(StateType.DISABLED, StateType.DISABLED));

    }
}
