package nl.unionsoft.sysstate.common.converter;

import nl.unionsoft.common.converter.ConverterWithConfig;


public class StringConverter implements ConverterWithConfig<Object, String, Class<?>> {

    @Override
    public Object convert(String value, Class<?> type) {
        if (Long.class.isAssignableFrom(type)) {
            return Long.valueOf(value);
        } else if (Integer.class.isAssignableFrom(type)) {
            return Integer.valueOf(value);
        } else if (Boolean.class.isAssignableFrom(type)) {
            return Boolean.valueOf(value);
        } else if (String.class.isAssignableFrom(type)) {
            return value;
        }
        throw new IllegalArgumentException("Unsupported type [" + type + "]");

    }


}
