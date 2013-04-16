package nl.unionsoft.sysstate.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class SysStateStringUtils {
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
}
