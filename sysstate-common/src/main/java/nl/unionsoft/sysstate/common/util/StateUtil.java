package nl.unionsoft.sysstate.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

public class StateUtil {

    public static final String exceptionAsMessage(final Exception exception) {

        return stackTraceToString(exception);
    }

    public static String stackTraceToString(final Throwable e) {
        String retValue = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            retValue = sw.toString();
        } finally {
            IOUtils.closeQuietly(pw);
            IOUtils.closeQuietly(sw);
        }
        return retValue;
    }
}
