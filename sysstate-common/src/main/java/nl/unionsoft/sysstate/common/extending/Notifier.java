package nl.unionsoft.sysstate.common.extending;

import java.util.Map;

public interface Notifier {
    public void notify(Notification notification, Map<String, String> properties);

}
