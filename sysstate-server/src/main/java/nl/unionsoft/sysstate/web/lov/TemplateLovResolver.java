package nl.unionsoft.sysstate.web.lov;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Service;

import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.logic.TemplateLogic;

@Service("templateLovResolver")
public class TemplateLovResolver implements ListOfValueResolver {

    @Inject
    @Named("templateLogic")
    private TemplateLogic templateLogic;

    public Properties getListOfValues() {
        Properties results = new Properties();
        for (Template template : templateLogic.getTemplates()) {
            results.setProperty(template.getId(), template.getId());
        }

        return results;
    }
}
