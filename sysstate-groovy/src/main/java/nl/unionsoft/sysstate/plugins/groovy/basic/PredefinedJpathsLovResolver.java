package nl.unionsoft.sysstate.plugins.groovy.basic;

import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.dto.TextDto;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.common.logic.TextLogic;

import org.springframework.stereotype.Service;

@Service("predefinedJpathsLovResolver")
public class PredefinedJpathsLovResolver implements ListOfValueResolver {

    protected final TextLogic textLogic;

    @Inject
    public PredefinedJpathsLovResolver(@Named("textLogic") TextLogic textLogic) {
        this.textLogic = textLogic;
    }

    @Override
    public Map<String, String> getListOfValues(PropertyMetaValue propertyMetaValue) {
        //@formatter:off
        return textLogic.getTexts("jpath", "jPathStateResolver").stream()
                .collect(Collectors.toMap((t) -> t.getName(), TextDto::getName));
        //@formatter:off
    }

}
