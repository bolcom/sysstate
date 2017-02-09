package nl.unionsoft.sysstate.logic;

import java.util.Optional;

public interface WorkLogic {

    public Optional<String> aquireWorkLock(String reference);
    
    public void release(String workLock);

}
