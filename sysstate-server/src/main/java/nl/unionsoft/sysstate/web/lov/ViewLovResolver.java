package nl.unionsoft.sysstate.web.lov;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.logic.ViewLogic;

import org.springframework.stereotype.Service;


@Service("viewLovResolver")
public class ViewLovResolver implements ListOfValueResolver {

    @Inject
    @Named("viewLogic")
    private ViewLogic viewLogic;

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {
        Map<String, String> results = new LinkedHashMap<String, String>();
        for (ViewDto view : viewLogic.getViews()) {
            results.put(String.valueOf(view.getName()), view.getName());
        }
        return results;
    }

}
