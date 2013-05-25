package nl.unionsoft.sysstate.plugins.impl.resolver;

import java.util.Random;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.extending.StateResolver;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service("mockStateResolverPlugin")
public class MockStateResolverImpl implements StateResolver {

    private final Random random;

    public MockStateResolverImpl() {
        random = new Random();
    }

    public void setState(final InstanceDto instance, final StateDto state) {
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
        } catch (final NumberFormatException e) {
            e.printStackTrace();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        if (StringUtils.equalsIgnoreCase("exception", getPart(parts, 2, ""))) {
            throw new IllegalStateException("Exception exception!");
        }

    }

    public static String getPart(final String[] parts, final int index, final String defaultValue) {
        String result = defaultValue;
        if (parts.length > index) {
            result = parts[index];
        }
        return result;
    }

    public String generateHomePageUrl(final InstanceDto instance) {
        return "http://localhost/";
    }

}
