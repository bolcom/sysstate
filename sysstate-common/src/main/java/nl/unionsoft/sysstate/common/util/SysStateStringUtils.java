package nl.unionsoft.sysstate.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class SysStateStringUtils {

    private static Pattern htmlEscapePattern = Pattern.compile("\\<.*?\\>");

    public static String[] tagMatches(String tagStr, String matchesOnStr) {
        final List<String> results = new ArrayList<String>();
        final String[] tags = StringUtils.split(tagStr, ' ');
        final String[] matchesOn = StringUtils.split(matchesOnStr, ' ');
        if (tags != null && matchesOn != null) {

            for (final String tag : tags) {
                if (ArrayUtils.contains(matchesOn, tag)) {
                    results.add(tag);
                }
            }
        }
        return results.toArray(new String[] {});
    }

    public static boolean isTagMatch(String tagStr, String matchesOnStr) {
        return tagMatches(tagStr, matchesOnStr).length > 0;
    }

    public static String stripHtml(String message) {
        if (message == null) {
            return null;
        }
        Matcher matcher = htmlEscapePattern.matcher(message);
        return StringUtils.trim(matcher.replaceAll(""));
    }

}
