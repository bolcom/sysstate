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

import nl.unionsoft.sysstate.common.logic.StatisticsLogic;
import nl.unionsoft.sysstate.logic.MessageLogic;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component("requestFilter")
public class RequestFilter implements Filter, ServletContextAware {

    @Inject
    @Named("messageLogic")
    private MessageLogic messageLogic;

    @Inject
    @Named("statisticsLogic")
    private StatisticsLogic statisticsLogic;

    public void setServletContext(ServletContext servletContext) {

    }

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        request.setAttribute("messages", messageLogic.getMessages());
        request.setAttribute("sysstateVersion", statisticsLogic.getApplicationVersion());
        chain.doFilter(request, response);
    }

    public void destroy() {

    }

}
