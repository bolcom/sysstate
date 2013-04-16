package nl.unionsoft.sysstate.plugins.impl.rating;

import java.util.Properties;

import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.plugins.RatingPlugin;

@PluginImplementation
public class StabilityRatingPluginImpl implements RatingPlugin {

    public Rating rating(StateDto state, Properties properties) {

        final Rating rating = new Rating();
        final StateType stateType = state.getState();
        switch (stateType) {
            case PENDING:
            case STABLE:
                rating.setRating(100);
                break;
            case UNSTABLE:
                rating.setRating(50);
                rating.setMessage("State reported unstable.");
                break;
            case DISABLED:
                rating.setRating(75);
                rating.setMessage("State reported disabled, should this job still be enabled?");
                break;
            case ERROR:
                rating.setRating(0);
                rating.setMessage("State reported error.");

        }
        return rating;
    }

    @Capabilities
    public String[] capabilities() {
        return new String[] { "stabilityRatingPlugin" };
    }

    public void stop() {

    }

    public void start() {

    }

    public void setActionId(Long id) {

    }

    public void config(Properties configuration) {

    }

}
