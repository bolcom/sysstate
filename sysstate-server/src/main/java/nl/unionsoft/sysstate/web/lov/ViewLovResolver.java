package nl.unionsoft.sysstate.web.lov;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.logic.ViewLogic;

import org.springframework.stereotype.Service;

@Service("viewLovResolver")
public class ViewLovResolver implements ListOfValueResolver {

    @Inject
    @Named("viewLogic")
    private ViewLogic viewLogic;

    public Properties getListOfValues() {
        Properties results = new Properties();
        for (ViewDto view : viewLogic.getViews()) {
            results.setProperty(String.valueOf(view.getId()), view.getName());
        }
        return results;
    }

}
