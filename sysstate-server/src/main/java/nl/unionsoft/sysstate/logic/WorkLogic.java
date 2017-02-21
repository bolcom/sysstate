package nl.unionsoft.sysstate.logic;

import java.util.Collection;
import java.util.Optional;

import nl.unionsoft.sysstate.common.dto.WorkDto;

public interface WorkLogic {

    public Optional<String> aquireWorkLock(String reference);

    public Collection<WorkDto> getMyWork();

    public void release(String workLock);

}
