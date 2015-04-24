package nl.unionsoft.sysstate.logic;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.common.dto.ViewDto;

public interface ViewLogic {
    public ListResponse<ViewDto> getViews(ListRequest listRequest);

    public List<ViewDto> getViews();

    public void createOrUpdateView(ViewDto view);

    public void delete(Long id);

    public Optional<ViewDto> getView(Long viewId);
    
    public ViewDto getBasicView();
}
