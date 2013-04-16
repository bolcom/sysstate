package nl.unionsoft.sysstate.web.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;

public class CustomDateTimeEditor extends PropertyEditorSupport {
    private final DateTimeFormatter dateTimeFormatter;

    private final boolean allowEmpty;

    public CustomDateTimeEditor (final DateTimeFormatter dateTimeFormatter, final boolean allowEmpty) {
        this.dateTimeFormatter = dateTimeFormatter;
        this.allowEmpty = allowEmpty;
    }

    /**
     * Parse the Date from the given text, using the specified DateFormat.
     */
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (allowEmpty && !StringUtils.hasText(text)) {
            // Treat empty String as null value.
            setValue(null);
        } else {
            setValue(dateTimeFormatter.parseDateTime(text));
        }
    }

    @Override
    public String getAsText() {
        final DateTime value = (DateTime) getValue();
        return (value != null ? dateTimeFormatter.print(value) : "");
    }
}
