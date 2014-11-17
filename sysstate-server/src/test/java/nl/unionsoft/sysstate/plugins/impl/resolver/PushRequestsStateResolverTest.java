package nl.unionsoft.sysstate.plugins.impl.resolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;
import mockit.Expectations;
import mockit.Mocked;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.logic.PushStateLogic;
import nl.unionsoft.sysstate.logic.StateLogic;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class PushRequestsStateResolverTest {

    private PushRequestsStateResolverImpl pushRequestsStateResolverPlugin;

    @Mocked
    private PushStateLogic pushStateLogic;

    @Mocked
    private StateLogic stateLogic;

    @Before
    public void before() {
        pushRequestsStateResolverPlugin = new PushRequestsStateResolverImpl();
        pushRequestsStateResolverPlugin.setPushStateLogic(pushStateLogic);
        pushRequestsStateResolverPlugin.setStateLogic(stateLogic);
    }

    @Test
    public void testSetState() {
        new Expectations() {
            {
                // @formatter:off
                pushStateLogic.fetch(1L);
                result = null;
                stateLogic.getLastStateForInstance(1L);
                result = null;
                // @formatter:on
            }
        };
        StateDto state = new StateDto();
        InstanceDto instance = new InstanceDto();
        instance.setId(1L);
        pushRequestsStateResolverPlugin.setState(instance, state);
        assertPending(state);

    }

    @Test
    public void testSetStateFetched() {

        final StateDto fetched = new StateDto();
        fetched.setDescription("Twaalf!");
        fetched.appendMessage("12!");
        fetched.setState(StateType.ERROR);
        new Expectations() {
            {
                // @formatter:off
                pushStateLogic.fetch(1L);
                result = fetched;
                // @formatter:on
            }
        };
        StateDto state = new StateDto();
        InstanceDto instance = new InstanceDto();
        instance.setId(1L);
        pushRequestsStateResolverPlugin.setState(instance, state);
        assertTwaalf(state);

    }

    @Test
    public void testSetStateNoResultsButPrevious() {

        final StateDto lastState = new StateDto();
        lastState.setCreationDate(new DateTime().minusMinutes(5));
        lastState.setDescription("Twaalf!");
        lastState.appendMessage("12!");
        lastState.setState(StateType.ERROR);
        new Expectations() {
            {
                // @formatter:off
                pushStateLogic.fetch(1L);
                result = null;
                stateLogic.getLastStateForInstance(1L);
                result = lastState;
                // @formatter:on
            }
        };
        StateDto state = new StateDto();
        InstanceDto instance = new InstanceDto();
        instance.setId(1L);
        pushRequestsStateResolverPlugin.setState(instance, state);
        assertTwaalf(state);

    }



    @Test
    public void testSetStateNoResultsButPreviousIsExpiredDefaultTimeout() {

        final StateDto lastState = new StateDto();
        lastState.setCreationDate(new DateTime().minusMinutes(15));
        lastState.setDescription("Twaalf!");
        lastState.appendMessage("12!");
        lastState.setState(StateType.STABLE);
        new Expectations() {
            {
                // @formatter:off
                pushStateLogic.fetch(1L);
                result = null;
                stateLogic.getLastStateForInstance(1L);
                result = lastState;
                // @formatter:on
            }
        };
        StateDto state = new StateDto();
        InstanceDto instance = new InstanceDto();
        instance.setId(1L);
        pushRequestsStateResolverPlugin.setState(instance, state);
        assertMissing(state);
    }

    private void assertPending(final StateDto state) {
        Assert.assertEquals(StateType.PENDING, state.getState());
        Assert.assertNull(state.getDescription());
        Assert.assertEquals("", state.getMessage());
        Assert.assertEquals(0L, state.getResponseTime());
    }

    private void assertMissing(final StateDto state) {
        Assert.assertEquals(StateType.PENDING, state.getState());
        Assert.assertEquals("Missing", state.getDescription());
        Assert.assertTrue(StringUtils.startsWith(state.getMessage(), "Instance hasn't reported in since "));
        Assert.assertEquals(0L, state.getResponseTime());
    }

    @Test
    public void testSetStateNoResultsButPreviousIsExpiredCustomTimeout() {

        final StateDto lastState = new StateDto();
        lastState.setCreationDate(new DateTime().minusMinutes(25));
        lastState.setDescription("Twaalf!");
        lastState.appendMessage("12!");
        lastState.setState(StateType.STABLE);
        new Expectations() {
            {
                // @formatter:off
                pushStateLogic.fetch(1L);
                result = null;
                stateLogic.getLastStateForInstance(1L);
                result = lastState;
                // @formatter:on
            }
        };
        StateDto state = new StateDto();
        InstanceDto instance = new InstanceDto();

        Map<String,String> properties = new HashMap<String, String>();
        properties.put("timeOut", String.valueOf(1000 * 60 * 20));
        instance.setConfiguration(properties);
        instance.setId(1L);
        pushRequestsStateResolverPlugin.setState(instance, state);
        assertMissing(state);
    }

    @Test
    public void testSetStateNoResultsButPreviousIsExpiredButNeverTimesOus() {

        final StateDto lastState = new StateDto();
        lastState.setCreationDate(new DateTime().minusMinutes(25));
        lastState.setDescription("Twaalf!");
        lastState.appendMessage("12!");
        lastState.setState(StateType.ERROR);
        new Expectations() {
            {
                // @formatter:off
                pushStateLogic.fetch(1L);
                result = null;
                stateLogic.getLastStateForInstance(1L);
                result = lastState;
                // @formatter:on
            }
        };
        StateDto state = new StateDto();
        InstanceDto instance = new InstanceDto();
        Map<String,String> properties = new HashMap<String, String>();
        properties.put("timeOut", "-1");
        instance.setConfiguration(properties);
        instance.setId(1L);
        pushRequestsStateResolverPlugin.setState(instance, state);
        assertTwaalf(state);
    }

    private void assertTwaalf(final StateDto state) {
        Assert.assertEquals(StateType.ERROR, state.getState());
        Assert.assertEquals("Twaalf!", state.getDescription());
        Assert.assertEquals("12!", state.getMessage());
    }

}
