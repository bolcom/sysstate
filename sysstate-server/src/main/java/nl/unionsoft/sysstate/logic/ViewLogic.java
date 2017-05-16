package nl.unionsoft.sysstate.logic;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.commons.list.model.ListRequest;
import nl.unionsoft.commons.list.model.ListResponse;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.dto.ViewResultDto;

public interface ViewLogic {

    public List<ViewDto> getViews();

    public void createOrUpdateView(ViewDto view);

    public void delete(String name);

    public Optional<ViewDto> getView(String name);
    
    public ViewDto getBasicView();
    
    public ViewResultDto getViewResults(final ViewDto view);

}
