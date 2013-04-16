package nl.unionsoft.sysstate.web.mvc.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.logic.TemplateLogic;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller()
public class StyleController {

    @Inject
    @Named("templateLogic")
    private TemplateLogic templateLogic;

    @RequestMapping(value = "/css/style.css")
    public void style(final HttpServletResponse response, @RequestParam(value = "templateId", required = false) final String templateId) throws IOException {
        final OutputStream outputStream = response.getOutputStream();
        final Template template = templateLogic.getTemplate(StringUtils.defaultIfEmpty(templateId, "default"));
        if (template != null) {
            IOUtils.write(template.getCss(), outputStream);
            IOUtils.closeQuietly(outputStream);
        }
        response.setContentType("text/css");
        response.setStatus(HttpStatus.SC_OK);
    }
}
