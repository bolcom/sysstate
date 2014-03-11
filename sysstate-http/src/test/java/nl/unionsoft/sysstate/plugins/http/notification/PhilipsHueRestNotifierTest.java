package nl.unionsoft.sysstate.plugins.http.notification;

import java.util.HashMap;
import java.util.Map;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.extending.Notification;
import nl.unionsoft.sysstate.common.logic.HttpClientLogic;
import nl.unionsoft.sysstate.plugins.http.JSoupStateResolverImpl;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PhilipsHueRestNotifierTest {
    @Mocked
    private HttpClientLogic httpClientLogic;

    // @Mocked
    private DefaultHttpClient defaultHttpClient;

    @Mocked
    private Notification notification;

    private PhilipsHueRestNotifierImpl philipsHueRestNotifier;

    @Before
    public void before() {
        philipsHueRestNotifier = new PhilipsHueRestNotifierImpl();
        philipsHueRestNotifier.setHttpClientLogic(httpClientLogic);
        defaultHttpClient = new DefaultHttpClient();
        //@formatter:off
        new NonStrictExpectations() {{
                httpClientLogic.getHttpClient("default");result = defaultHttpClient;
        }};
        //@formatter:on
    }

    @Test
    public void testHue() {

        //@formatter:off
        new NonStrictExpectations() {{
            notification.getPreviousState();result = StateType.PENDING;
            notification.getCurrentState();result = StateType.UNSTABLE;
            
        }};
        //@formatter:on

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("light", "2");
        properties.put("userName", "newdeveloper");
        properties.put("url", "http://10.10.132.181/");
        philipsHueRestNotifier.notify(notification, properties);
    }
}
