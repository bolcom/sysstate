package nl.unionsoft.sysstate.dao;

import java.util.Optional;

import nl.unionsoft.sysstate.domain.Work;

public interface WorkDao {

    Optional<Work> getWork(String reference);

    void createOrUpdateWork(Work work);
}
