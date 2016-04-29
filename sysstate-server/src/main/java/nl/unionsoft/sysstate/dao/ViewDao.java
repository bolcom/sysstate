package nl.unionsoft.sysstate.dao;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.sysstate.domain.View;

public interface ViewDao {

    public void createOrUpdateView(View view);

    public void delete(Long id);

    public Optional<View> getView(Long viewId);

    public List<View> getViews();

}
