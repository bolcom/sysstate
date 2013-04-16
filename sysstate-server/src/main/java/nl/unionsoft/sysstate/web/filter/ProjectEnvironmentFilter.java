package nl.unionsoft.sysstate.web.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.logic.MessageLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component("projectEnvironmentFilter")
public class ProjectEnvironmentFilter implements Filter, ServletContextAware {

    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

    @Inject
    @Named("messageLogic")
    private MessageLogic messageLogic;

    @Inject
    @Named("environmentLogic")
    private EnvironmentLogic environmentLogic;

    @Inject
    @Named("viewLogic")
    private ViewLogic viewLogic;

    public void setServletContext(ServletContext servletContext) {

    }

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setAttribute("environments", environmentLogic.getEnvironments());
        request.setAttribute("projects", projectLogic.getProjects());
        request.setAttribute("views", viewLogic.getViews());
        request.setAttribute("messages", messageLogic.getMessages());
        chain.doFilter(request, response);
    }

    public void destroy() {

    }

}
