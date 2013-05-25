package nl.unionsoft.sysstate.common.extending;

import java.util.Properties;

import nl.unionsoft.sysstate.common.dto.StateDto;

public interface RatingResolver {
    public Rating rating(StateDto state, Properties properties);

    public static class Rating {
        private int rating;
        private String message;

        public Rating() {
            rating = 100;
        }

        public void setRating(final int rating) {
            if (rating > 100) {
                this.rating = 100;
            } else {
                this.rating = rating;
            }
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public int getRating() {
            return rating;
        }

        public String getMessage() {
            return message;
        }

    }
}
