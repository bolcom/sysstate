package nl.unionsoft.sysstate.plugins.http.notification;

import java.util.HashMap;
import java.util.Map;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.extending.Notification;
import nl.unionsoft.sysstate.common.logic.HttpClientLogic;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class RestNotifierTest {
    @Mocked
    private HttpClientLogic httpClientLogic;

    // @Mocked
    private DefaultHttpClient defaultHttpClient;

    @Mocked
    private Notification notification;

    private RestNotifierImpl restNotifierImpl;

    @Before
    public void before() {
        restNotifierImpl = new RestNotifierImpl();
        restNotifierImpl.setHttpClientLogic(httpClientLogic);
        defaultHttpClient = new DefaultHttpClient();
        //@formatter:off
        new NonStrictExpectations() {{
                httpClientLogic.getHttpClient("default");result = defaultHttpClient;
        }};
        //@formatter:on
    }

    @Test
    @Ignore
    public void testRest() {

        //@formatter:off
        new NonStrictExpectations() {{
            notification.getPreviousState();result = StateType.PENDING;
            notification.getCurrentState();result = StateType.UNSTABLE;
            
        }};
        //@formatter:on

        for (int i = 0; i < 65535; i += 100) {
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("body", "{\"on\":true, \"sat\":255, \"bri\":255,\"hue\":" + i + "}");
            properties.put("url", "http://10.10.132.181/api/newdeveloper/lights/1/state");
            properties.put("method", "put");
            restNotifierImpl.notify(notification, properties);
        }

    }
}
