package nl.unionsoft.sysstate.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;

import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.dao.StateDao;
import nl.unionsoft.sysstate.domain.State;

public class MapStateDaoImpl implements StateDao {

    private Map<Long, State> stateMap;
    private long idCounter;

    public MapStateDaoImpl(Map<Long, State> stateMap) {
        this.stateMap = stateMap;
    }

    @Override
    public void createOrUpdate(State state) {
        Optional<State> optExistingState = Optional.ofNullable(state.getId()).map(stateId -> stateMap.get(stateId));
        if (optExistingState.isPresent()) {
            State existingState = optExistingState.get();
            existingState.setDescription(state.getDescription());
            existingState.setLastUpdate(state.getLastUpdate());
            existingState.setMessage(state.getMessage());
            existingState.setRating(state.getRating());
            existingState.setResponseTime(state.getResponseTime());
            existingState.setState(state.getState());
        } else {
            State newState = new State(state);
            newState.setId(nextId());
            stateMap.put(newState.getId(), newState);
        }
    }

    @Override
    public Optional<State> getLastStateForInstance(Long instanceId) {
        return sortedStreamForInstance(instanceId).findFirst();
    }

    @Override
    public Optional<State> getLastStateForInstance(Long instanceId, StateType stateType) {
        return sortedStreamForInstance(instanceId)
                .filter(state -> state.getState().equals(stateType))
                .findFirst();

    }

    @Override
    public void cleanStatesOlderThanDays(int nrOfDays) {
        DateTime minimalDateToKeep = new DateTime().minusDays(nrOfDays); 
        stateMap.entrySet().stream()
        .filter(entry -> new DateTime(entry.getValue().getLastUpdate()).isBefore(minimalDateToKeep))
        .collect(Collectors.toList())
        .forEach(entry -> stateMap.remove(entry.getKey()));
    }

    @Override
    public List<State> getStates() {
        return stateMap.values().stream()
                .map(state -> new State(state))
                .collect(Collectors.toList());
    }

    private Stream<State> sortedStreamForInstance(Long instanceId) {
        return stateMap.values().stream()
                .filter(state -> state.getInstance().getId().equals(instanceId))
                .sorted((s1, s2) -> s2.getLastUpdate().compareTo(s1.getLastUpdate()));

    }

    private long nextId() {
        return idCounter++;
    }

}
