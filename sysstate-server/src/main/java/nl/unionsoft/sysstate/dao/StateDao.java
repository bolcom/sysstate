package nl.unionsoft.sysstate.dao;

import java.util.List;

import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.domain.State;

public interface StateDao {

    public void createOrUpdate(State state);

    public State getLastStateForInstance(Long instanceId);

    public State getLastStateForInstance(Long instanceId, StateType stateType);

    public void clean();

    public List<State> getStates();

    public ListResponse<State> getStates(ListRequest listRequest);

}
