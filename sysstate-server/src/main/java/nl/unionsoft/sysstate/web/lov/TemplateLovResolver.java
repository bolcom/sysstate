package nl.unionsoft.sysstate.web.lov;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.logic.TemplateLogic;

import org.springframework.stereotype.Service;

@Service("templateLovResolver")
public class TemplateLovResolver implements ListOfValueResolver {

    @Inject
    @Named("templateLogic")
    private TemplateLogic templateLogic;

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {
        Map<String, String> results = new LinkedHashMap<String, String>();
        for (Template template : templateLogic.getTemplates()) {
            results.put(template.getId(), template.getId());
        }
        return results;
    }
}
