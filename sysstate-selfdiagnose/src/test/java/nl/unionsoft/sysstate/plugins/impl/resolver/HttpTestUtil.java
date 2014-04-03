package nl.unionsoft.sysstate.plugins.impl.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import mockit.Expectations;
import mockit.Mocked;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.extending.StateResolver;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpTestUtil {
    public static StateDto doCall(final StateResolver plugin, final DefaultHttpClient defaultHttpClient, final String stream) throws IOException {
        return doCall(plugin, defaultHttpClient, stream, new LinkedHashMap<String, String>());
    }

    public static StateDto doCall(final StateResolver plugin, final DefaultHttpClient defaultHttpClient, final String stream,
            final Map<String, String> configuration) throws IOException {

        final StateDto state = new StateDto();
        final InstanceDto instance = new InstanceDto();
        configuration.put("url", "SomeUrl");
        final InputStream inputStream = HttpTestUtil.class.getResourceAsStream(stream);
        new Expectations() {

            @Mocked
            private HttpResponse httpResponse;

            @Mocked
            private StatusLine statusLine;

            @Mocked
            private HttpEntity httpEntity;

            {
                //@formatter:off
                defaultHttpClient.execute((HttpUriRequest) any);  result = httpResponse;
                httpResponse.getStatusLine();  result = statusLine;
                statusLine.getStatusCode(); result = 200;
                httpResponse.getEntity(); result = httpEntity;
                statusLine.getStatusCode(); result = 200;
                httpEntity.getContent(); result = inputStream;
                httpEntity.isStreaming(); result = false;
                //@formatter:on
            }
        };
        plugin.setState(instance, state, configuration);
        return state;

    }
}
