package nl.unionsoft.sysstate.plugins.impl.rating;

import java.util.Properties;

import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.extending.RatingResolver;

// @PluginImplementation
public class ResponseTimeRatingImpl implements RatingResolver {

    private static final String UPPER_BOUND = "25000";
    private static final String LOWER_BOUND = "5000";

    private Properties configuration;

    public Rating rating(final StateDto state, final Properties properties) {
        final Rating rating = new Rating();

        final double lowerBound = Double.valueOf(PropertiesUtil.getProperty(properties, configuration, "lowerBound", LOWER_BOUND));
        final double upperBound = Double.valueOf(PropertiesUtil.getProperty(properties, configuration, "upperBound", UPPER_BOUND));
        final Long responseTime = state.getResponseTime();
        if (responseTime < lowerBound) {
            rating.setRating(100);
        } else if (responseTime < upperBound) {
            rating.setRating((int) ((1 - (responseTime - lowerBound) / (upperBound - lowerBound)) * 100));
            rating.setMessage(getHighMessage(state, lowerBound, upperBound, responseTime, "high"));
        } else {
            rating.setRating(0);
            rating.setMessage(getHighMessage(state, lowerBound, upperBound, responseTime, "out of bounds"));
        }
        return rating;
    }

    private String getHighMessage(final StateDto state, final double lowerBound, final double upperBound, final Long responseTime, final String text) {
        final StringBuilder messageBuilder = new StringBuilder(400);
        messageBuilder.append("Instance '");
        messageBuilder.append(state.getInstance());
        messageBuilder.append("' has a ");
        messageBuilder.append(text);
        messageBuilder.append(" responsetime. ResponseTime=");
        messageBuilder.append(responseTime);
        messageBuilder.append(", lowerBoundary=");
        messageBuilder.append(lowerBound);
        messageBuilder.append(", upperBoundary=");
        messageBuilder.append(upperBound);
        messageBuilder.append(".");
        return messageBuilder.toString();
    }



    public void setActionId(final Long id) {

    }

    public void config(final Properties configuration) {
        this.configuration = configuration;
    }

}
