package nl.unionsoft.sysstate.plugins.jenkins;

import org.apache.commons.lang.StringUtils;

public class JenkinsUtils {
    public static final String API_XML = "/api/xml";
    
    public static String appendApi(String uri)
    {
        final StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(uri);
        if (!StringUtils.endsWith(uri, API_XML)) {
            uriBuilder.append(API_XML);
        }
        return uriBuilder.toString();
    }
}
