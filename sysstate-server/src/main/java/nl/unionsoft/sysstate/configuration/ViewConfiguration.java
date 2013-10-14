package nl.unionsoft.sysstate.configuration;

import nl.unionsoft.common.param.Param;
import nl.unionsoft.sysstate.common.extending.GroupConfiguration;

public class ViewConfiguration implements GroupConfiguration {

    private String defaultView;

    private String defaultTemplate;

    private String maintenanceMode;

    @Param(title="Default View")
    public String getDefaultView() {
        return defaultView;
    }

    public void setDefaultView(String defaultView) {
        this.defaultView = defaultView;
    }

    @Param(title="Default Template")
    public String getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(String defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    @Param(title="Enable Maintenance Mode")
    public String getMaintenanceMode() {
        return maintenanceMode;
    }

    public void setMaintenanceMode(String maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }

}
