package nl.unionsoft.sysstate.plugins.impl.rating;

import java.util.Properties;

import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.extending.RatingResolver;

// @PluginImplementation
public class StabilityRatingImpl implements RatingResolver {

    public Rating rating(final StateDto state, final Properties properties) {

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

    public void stop() {

    }

    public void start() {

    }

    public void setActionId(final Long id) {

    }

    public void config(final Properties configuration) {

    }

}
