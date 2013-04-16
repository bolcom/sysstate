package nl.unionsoft.sysstate.dto;

import java.util.ArrayList;
import java.util.List;

import nl.unionsoft.sysstate.common.dto.ViewDto;

public class EcoSystemRequest {

    private final List<Long> excludedProjectIds;
    private final List<Long> includedProjectIds;
    private final List<Long> excludedEnvironmentIds;
    private final List<Long> includedEnvironmentIds;
    private ViewDto view;

    public EcoSystemRequest () {
        excludedProjectIds = new ArrayList<Long>();
        excludedEnvironmentIds = new ArrayList<Long>();
        includedProjectIds = new ArrayList<Long>();
        includedEnvironmentIds = new ArrayList<Long>();
    }

    public EcoSystemRequest (ViewDto view) {
        excludedProjectIds = new ArrayList<Long>();
        excludedEnvironmentIds = new ArrayList<Long>();
        includedProjectIds = new ArrayList<Long>();
        includedEnvironmentIds = new ArrayList<Long>();
        this.view = view;
    }

    public List<Long> getExcludedProjectIds() {
        return excludedProjectIds;
    }

    public List<Long> getExcludedEnvironmentIds() {
        return excludedEnvironmentIds;
    }

    public List<Long> getIncludedProjectIds() {
        return includedProjectIds;
    }

    public List<Long> getIncludedEnvironmentIds() {
        return includedEnvironmentIds;
    }

    public ViewDto getView() {
        return view;
    }

    public void setView(ViewDto view) {
        this.view = view;
    }

}
