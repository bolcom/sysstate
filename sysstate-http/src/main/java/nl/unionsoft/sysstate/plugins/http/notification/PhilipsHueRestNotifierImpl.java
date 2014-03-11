package nl.unionsoft.sysstate.plugins.http.notification;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.extending.InstanceNotification;
import nl.unionsoft.sysstate.common.extending.Notification;
import nl.unionsoft.sysstate.common.logic.HttpClientLogic;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.enums.EnumUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhilipsHueRestNotifierImpl extends HttpClientNotifierImpl {

    @Inject
    @Named("httpClientLogic")
    private HttpClientLogic httpClientLogic;
    private static final Logger LOG = LoggerFactory.getLogger(PhilipsHueRestNotifierImpl.class);

    @Override
    protected void notify(Notification notification, Map<String, String> properties, HttpClient httpClient) {

        // if (notification.getPreviousState() != notification.getCurrentState()) {
        StateType stateType = notification.getCurrentState();
        String light = properties.get("light");
        String baseUrl = properties.get("url");
        String user = properties.get("userName");

        StringBuilder urlBuilder = new StringBuilder();
        // http://<bridge ip address>/api/newdeveloper/lights/1/state
        urlBuilder.append(baseUrl);
        if (!StringUtils.endsWith(baseUrl, "/")) {
            urlBuilder.append("/");
        }
        if (!StringUtils.endsWith(baseUrl, "api/")) {
            urlBuilder.append("api/");
        }
        urlBuilder.append(user);
        urlBuilder.append("/lights/");
        urlBuilder.append(light);
        urlBuilder.append("/state");
        LOG.info("Target url is: " + urlBuilder.toString());
        final HttpPut httpPut = new HttpPut(urlBuilder.toString());

        // changed...

        // {"on":true, "sat":255, "bri":255,"hue":10000}

        int brightness = 100;
        int saturation = 255;
        int hue = 0;
        switch (stateType) {
            case STABLE:
                hue = 25718;
                break;
            case UNSTABLE:
                hue = 18310;
                // body = "{\"on\":true, \"sat\":255, \"bri\":100,\"hue\":18310}";
                break;
            case ERROR:
                hue = 0;
                break;
            default:
                hue = 46920;
                break;
        }
        String body = "{\"on\":true, \"sat\":" + saturation + ", \"bri\":" + brightness + ",\"hue\":" + hue + "}";

        LOG.info("Body is set to:" + body);

        addEntity(httpPut, body);
        handleRequest(httpPut, httpClient);
        // }

    }

}
