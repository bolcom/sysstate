package nl.unionsoft.sysstate.logic

import nl.unionsoft.sysstate.common.enums.StateType
import nl.unionsoft.sysstate.dao.impl.MapStateDaoImpl
import nl.unionsoft.sysstate.domain.Instance
import nl.unionsoft.sysstate.domain.State
import spock.lang.Specification

class MapStateDaoSpec extends Specification{

    def "when I ask for the state for an instance, I expect to get the newest."() {
        given:

        def map = [1:createState(1, Date.parse("dd-MM-yyyy", "01-01-2017")), 2:createState(1, Date.parse("dd-MM-yyyy", "02-01-2017"))]
        def dao = new MapStateDaoImpl(map)

        when:
        Optional<State> optState = dao.getLastStateForInstance(1L)

        then:
        optState.isPresent()
        optState.get().lastUpdate.format("dd-MM-yyyy") == "02-01-2017"
    }


    def "when I ask for the state for an instance with a specific stateType, I expect to get the newest."() {
        given:

        def map = [
            1:createState(1, Date.parse("dd-MM-yyyy", "01-01-2017")),
            2:createState(1, Date.parse("dd-MM-yyyy", "02-01-2017")),
            3:createState(1, Date.parse("dd-MM-yyyy", "03-01-2017"), StateType.ERROR),
        ]
        def dao = new MapStateDaoImpl(map)

        when:
        Optional<State> optState = dao.getLastStateForInstance(1L, StateType.STABLE)

        then:
        optState.isPresent()
        optState.get().lastUpdate.format("dd-MM-yyyy") == "02-01-2017"
        optState.get().state == StateType.STABLE
    }

    def createState(long instanceId, Date lastUpdate, stateType = StateType.STABLE){
        return new State(instance : new Instance(id:instanceId), lastUpdate: lastUpdate, state:stateType)
    }
}
