package nl.unionsoft.sysstate.web.tags.viewresult;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.CountDto;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.dto.ViewResultDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.domain.State;
import nl.unionsoft.sysstate.util.CountUtil;
import nl.unionsoft.sysstate.util.SysStateStringUtils;

import org.apache.commons.lang.StringUtils;

public class ProjectEnvironmentTag extends TagSupport {

    private static final long serialVersionUID = 6920399763848801496L;

    private String var;
    private ViewResultDto viewResult;
    private ProjectDto project;
    private EnvironmentDto environment;

    @Inject
    @Named("stateConverter")
    private Converter<StateDto, State> stateConverter;

    @Override
    public int doStartTag() throws JspException {

        final ProjectEnvironmentDto result = new ProjectEnvironmentDto();
        final CountDto count = result.getCount();
        final List<InstanceDto> prjEnvInstances = result.getInstances();
        final List<String> commonDescriptions = new ArrayList<String>();
        if (viewResult != null && project != null && environment != null && StringUtils.isNotEmpty(var)) {
            int prjEnvRating = 100;
            int instanceCount = 0;
            final List<InstanceDto> instances = viewResult.getInstances();
            if (instances != null) {
                final ViewDto view = viewResult.getView();
                for (final InstanceDto instance : instances) {
                    final ProjectEnvironmentDto projectEnvironment = instance.getProjectEnvironment();
                    if (projectEnvironment != null) {

                        final ProjectDto project = projectEnvironment.getProject();
                        final EnvironmentDto environment = projectEnvironment.getEnvironment();
                        if (isMatchProjectAndEnvironment(project, environment)) {
                            // Match!
                            prjEnvInstances.add(instance);
                            result.setId(projectEnvironment.getId());
                            final StateDto state = instance.getState();
                            final String description = state.getDescription();
                            if (instance.isEnabled()) {
                                if (view != null && SysStateStringUtils.isTagMatch(instance.getTags(), view.getCommonTags()) && !commonDescriptions.contains(description)) {
                                    commonDescriptions.add(description);
                                }
                                CountUtil.add(count, state.getState());
                            }
                            final int instanceRating = state.getRating();
                            if (instanceRating >= 0) {
                                instanceCount++;
                                if (instanceRating < prjEnvRating) {
                                    prjEnvRating = instanceRating;
                                }
                            }
                        }
                    }
                }
            }
            if (instanceCount > 0) {
                result.setRating(prjEnvRating);
            } else {
                // No (enabled) instances
                result.setRating(-1);
            }

            if (commonDescriptions.size() == 1) {
                result.setDescription(commonDescriptions.get(0));
            }
            result.setState(stateTypeForCount(count));
        }
        pageContext.setAttribute(var, result);
        return super.doStartTag();
    }

    private StateType stateTypeForCount(CountDto count) {
        StateType result = StateType.DISABLED;
        if (count.getError() > 0) {
            result = StateType.ERROR;
        } else if (count.getUnstable() > 0) {
            result = StateType.UNSTABLE;
        } else if (count.getDisabled() > 0) {
            result = StateType.DISABLED;
        } else if (count.getStable() > 0) {
            result = StateType.STABLE;
        } else if (count.getPending() > 0) {
            result = StateType.PENDING;
        }
        return result;
    }

    private boolean isMatchProjectAndEnvironment(ProjectDto project, EnvironmentDto environment) {
        //@formatter:off
        return project != null && project.getId().equals(this.project.getId()) && 
                environment != null && environment.getId().equals(this.environment.getId());
        //@formatter:on
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public ViewResultDto getViewResult() {
        return viewResult;
    }

    public void setViewResult(ViewResultDto viewResult) {
        this.viewResult = viewResult;
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public EnvironmentDto getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentDto environment) {
        this.environment = environment;
    }

}
