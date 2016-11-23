package nl.unionsoft.sysstate.web.lov;

import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Service;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.common.logic.TemplateLogic;

@Service("templateLovResolver")
public class TemplateLovResolver implements ListOfValueResolver {

    @Inject
    @Named("templateLogic")
    private TemplateLogic templateLogic;

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {
        return templateLogic.getTemplates().stream().collect(Collectors.toMap(TemplateDto::getName, TemplateDto::getName));
    }
}
