package nl.unionsoft.sysstate.logic

import nl.unionsoft.sysstate.common.enums.WorkStatus
import nl.unionsoft.sysstate.converter.WorkConverter
import nl.unionsoft.sysstate.dao.WorkDao
import nl.unionsoft.sysstate.domain.Work
import nl.unionsoft.sysstate.logic.impl.WorkLogicImpl

import org.joda.time.DateTime
import org.junit.Assert

import spock.lang.Specification

class WorkLogicSpec extends Specification{

    WorkDao workDao = Mock();

    WorkLogicImpl workLogic;


    void setup() {
        workLogic = new WorkLogicImpl(workDao, new WorkConverter(), "001", 10)
    }

    def "when I acquire work, I should be able to get it"() {

        given:
        def reference = "abc123"

        when:
        Optional<String> results = workLogic.aquireWorkLock(reference);

        then:
        1 *  workDao.getWork(reference) >> Optional.ofNullable(createWork(reference,null));
        1 * workDao.createOrUpdateWork(_) >> { Work work ->
            assert work.nodeId == "001"
            assert work.state == WorkStatus.ACQUIRING
            assert work.reference == "abc123"
        }

        then:
        1 *  workDao.getWork(reference) >> Optional.ofNullable(createWork(reference,"001",WorkStatus.ACQUIRING));
        1 * workDao.createOrUpdateWork(_) >> { Work work ->
            assert work.nodeId == "001"
            assert work.state == WorkStatus.ACQUIRED
            assert work.reference == "abc123"
        }

        results.isPresent();
    }



    def "when I acquire work, but another node acquired it before me during the same process, I should not be able to get it"() {

        given:
        def reference = "abc123"

        when:
        Optional<String> results = workLogic.aquireWorkLock(reference);

        then:
        1 *  workDao.getWork(reference) >> Optional.ofNullable(createWork(reference,null));

        then:
        1 *  workDao.getWork(reference) >> Optional.ofNullable(createWork(reference,"002",WorkStatus.ACQUIRING));

        !results.isPresent();
    }

    def "when I acquire work, but another node acquired it before me, I should not be able to get it"() {

        given:
        def reference = "abc123"

        when:
        Optional<String> results = workLogic.aquireWorkLock(reference);

        then:
        1 *  workDao.getWork(reference) >> Optional.ofNullable(createWork(reference,"002",WorkStatus.ACQUIRING));

        !results.isPresent();
    }

    def "when I acquire work and another node acquired it before me, but it took to long, I should be able to get it"() {

        given:
        def reference = "abc123"

        when:
        Optional<String> results = workLogic.aquireWorkLock(reference);

        then:
        1 *  workDao.getWork(reference) >> Optional.ofNullable(createWork(reference,"002",WorkStatus.ACQUIRING, new DateTime().minusMinutes(20).toDate()));
        then:
        1 *  workDao.getWork(reference) >> Optional.ofNullable(createWork(reference,"001",WorkStatus.ACQUIRING));

        results.isPresent();
    }

    def "when I acquire work and it does not exist yet, I will create it and should be able to get it"() {

        given:
        def reference = "abc123"

        when:
        Optional<String> results = workLogic.aquireWorkLock(reference);

        then:
        1 * workDao.getWork(reference) >> Optional.empty();
        1 * workDao.createOrUpdateWork(_)

        then:
        1 *  workDao.getWork(reference) >> Optional.ofNullable(createWork(reference,"001",WorkStatus.ACQUIRING));
        1 * workDao.createOrUpdateWork(_)

        results.isPresent();
    }

    def "when I acquire work and it does not exist yet, I will create it but if someone else creates it before me, I should not be able to get it"() {

        given:
        def reference = "abc123"

        when:
        Optional<String> results = workLogic.aquireWorkLock(reference);

        then:
        1 * workDao.getWork(reference) >> Optional.empty();
        1 * workDao.createOrUpdateWork(_)

        then:
        1 *  workDao.getWork(reference) >> Optional.ofNullable(createWork(reference,"002",WorkStatus.ACQUIRING));


        !results.isPresent();
    }

    Work createWork(String reference, String nodeId, WorkStatus workStatus = WorkStatus.NEW, Date initialized = new Date()){
        return new Work(reference:reference, state:workStatus, nodeId:nodeId, initialized:initialized);
    }
}
