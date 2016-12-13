package nl.unionsoft.sysstate.web.lov;

import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Service;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.common.logic.TemplateLogic;

@Service("templateWriterLovResolver")
public class TemplateWriterLovResolver implements ListOfValueResolver {

    @Inject
    @Named("templateLogic")
    private TemplateLogic templateLogic;

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {
        return templateLogic.getTemplateWriters().stream().collect(Collectors.toMap(String::toString, String::toString));
    }
}
