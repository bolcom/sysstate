package nl.unionsoft.sysstate.converter;

import java.util.Optional;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ConverterWithConfig;

public class OptionalConverter {

    public static <T, F, E> Optional<T> convert(Optional<F> from, Converter<T, F> converter) {
        if (from.isPresent()) {
            return Optional.ofNullable(converter.convert(from.get()));
        } else {
            return Optional.empty();
        }
    }

    public static <T, F, E> T fromOptional(Optional<F> from, Converter<T, F> converter) {
        if (from.isPresent()) {
            return converter.convert(from.get());
        }
        return null;
    }
    
    public static <T, F, E> T fromOptional(Optional<F> from, Converter<T, F> converter, T defaultValue) {
        if (from.isPresent()) {
            return converter.convert(from.get());
        } else {
            return defaultValue;
        }
    }
    
    public static <T, F, E,C> T fromOptional(Optional<F> from, ConverterWithConfig<T, F, C> converter, C config, T defaultValue) {
        if (from.isPresent()) {
            return converter.convert(from.get(), config);
        } else {
            return defaultValue;
        }
    }


    public static <T, F, E> Optional<T> toOptional(F from, Converter<T, F> converter) {
        if (from == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(converter.convert(from));
    }

}
