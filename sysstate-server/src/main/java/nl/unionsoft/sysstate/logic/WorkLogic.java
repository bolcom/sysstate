package nl.unionsoft.sysstate.logic;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import nl.unionsoft.sysstate.common.dto.WorkDto;

public interface WorkLogic {

    /**
     * Acquires a lock for the given reference. If a lock is already set by a different process, then the lock will only be acquired if the previous entry has
     * timed out according to the 'timeoutSeconds' param.
     * 
     * @param reference
     *            The reference to get the lock for
     * @param timeoutSeconds
     *            The max amount of time in seconds that a previous entry may hold a lock to the reference.
     * @return An Optional workId, depending if the the lock could be acquired or not.
     */
    public Optional<String> aquireWorkLock(String reference, int timeoutSeconds);

    /**
     * Get all the work that belongs to this instance.
     * 
     * @return
     */
    public Collection<WorkDto> getMyWork();

    /**
     * Releases work for the given workId
     * 
     * @param workId
     *            the workId to release the work for
     */
    public void release(String workId);

    /**
     * Acquires work for the given reference, if acquired then the given Runnable will be executed.
     * 
     * @param reference
     *            The reference to get the lock for
     * @param timeoutSeconds
     *            The max amount of time in seconds that a previous entry may hold a lock to the reference.
     * @param runnable
     *            the Runnable to run if the work for the given reference can be acquired.
     */
    public void process(String reference, int timeoutSeconds, Runnable runnable);

}
