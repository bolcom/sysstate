package nl.unionsoft.sysstate.plugins.impl.resolver;

import java.util.Random;

import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.plugins.StateResolverPlugin;

import org.apache.commons.lang.StringUtils;

@PluginImplementation
public class MockStateResolverPluginImpl implements StateResolverPlugin {

    private Random random;

    public MockStateResolverPluginImpl () {
        random = new Random();
    }

    public void setState(final InstanceDto instance, StateDto state) {
        final String configuration = instance.getConfiguration();
        final String[] parts = StringUtils.split(configuration, ',');

        String stateStr = StringUtils.upperCase(getPart(parts, 0, "stable"));
        if (StringUtils.equalsIgnoreCase("RANDOM", stateStr)) {
            int pick = random.nextInt(StateType.values().length);
            state.setState(StateType.values()[pick]);
        } else {
            state.setState(StateType.valueOf(stateStr));
        }

        state.setDescription(stateStr);
        try {
            Thread.sleep(Long.valueOf(getPart(parts, 1, "0")));
        } catch(final NumberFormatException e) {
            e.printStackTrace();
        } catch(final InterruptedException e) {
            e.printStackTrace();
        }
        if (StringUtils.equalsIgnoreCase("exception", getPart(parts, 2, ""))) {
            throw new IllegalStateException("Exception exception!");
        }

    }

    @Capabilities
    public String[] capabilities() {
        return new String[] { "mockStateResolver" };
    }

    public static String getPart(String[] parts, int index, String defaultValue) {
        String result = defaultValue;
        if (parts.length > index) {
            result = parts[index];
        }
        return result;
    }

    public String generateHomePageUrl(InstanceDto instance) {
        return "http://localhost/";
    }

}
