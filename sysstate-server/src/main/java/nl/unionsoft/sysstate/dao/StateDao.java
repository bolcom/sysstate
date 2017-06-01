package nl.unionsoft.sysstate.dao;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.commons.list.model.ListRequest;
import nl.unionsoft.commons.list.model.ListResponse;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.domain.State;

public interface StateDao {

    public void createOrUpdate(State state);

    public Optional<State> getLastStateForInstance(Long instanceId);

    public Optional<State> getLastStateForInstance(Long instanceId, StateType stateType);

    public void cleanStatesOlderThanDays(int nrOfDays);

    public List<State> getStates();


}
