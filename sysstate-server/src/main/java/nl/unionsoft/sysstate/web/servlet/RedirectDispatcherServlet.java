package nl.unionsoft.sysstate.web.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

public class RedirectDispatcherServlet extends DispatcherServlet {

    private static final Logger LOG = LoggerFactory.getLogger(RedirectDispatcherServlet.class);

    private static final String REDIR_URL = "rUrl";
    private static final long serialVersionUID = -6382306789398765848L;

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {

        final RedirectHttpServletResponse responseWrapper = new RedirectHttpServletResponse(response);
        final HttpSession session = request.getSession();
        captureRedirectUrl(request, session);
        super.doService(request, responseWrapper);
        handleRedirect(responseWrapper, session);
    }

    private void handleRedirect(RedirectHttpServletResponse responseWrapper, HttpSession session) throws IOException {
        if (responseWrapper.isRedirect()) {
            LOG.info("Response contains a redirect, checking for possible redirUrl...");
            final String redirUrl = (String) session.getAttribute(REDIR_URL);
            if (StringUtils.isNotEmpty(redirUrl)) {
                LOG.info("RedirUrl set, redirecting to '{}' instead!", redirUrl);
                responseWrapper.setLocation(redirUrl);
            }
            // Clear attribute from session (no need anymore)
            session.removeAttribute(REDIR_URL);
            // Commit Redirect
            responseWrapper.commitRedirect();
        }
    }

    private void captureRedirectUrl(HttpServletRequest request, HttpSession session) {
        final String redirUrl = request.getParameter(REDIR_URL);

        if (StringUtils.isNotEmpty(redirUrl)) {
            LOG.info("Caught redirUrl param value '{}' in GET request, storing in session...", redirUrl);
            session.setAttribute(REDIR_URL, redirUrl);
        }
    }

    private class RedirectHttpServletResponse extends HttpServletResponseWrapper {

        private boolean redirect = false;
        private String location;

        public RedirectHttpServletResponse (HttpServletResponse response) {
            super(response);
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            LOG.info("Captured sendRedirect to location: {}", location);
            redirect = true;
            this.location = location;
        }

        public void commitRedirect() throws IOException {
            if (StringUtils.isNotEmpty(location)) {
                super.sendRedirect(location);
            }
        }

        public boolean isRedirect() {
            return redirect;
        }

        public void setLocation(String location) {
            this.location = location;
        }

    }

}
