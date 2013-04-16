package nl.unionsoft.sysstate.dao;

import nl.unionsoft.sysstate.common.dto.ViewDto;

public interface ViewDao {

    public void createOrUpdateView(ViewDto view);

    public void delete(Long id);

    public ViewDto getView(Long viewId);

}
