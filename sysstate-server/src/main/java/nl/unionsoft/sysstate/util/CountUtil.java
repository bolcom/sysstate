package nl.unionsoft.sysstate.util;

import nl.unionsoft.sysstate.common.dto.CountDto;
import nl.unionsoft.sysstate.common.enums.StateType;

public class CountUtil {

    private CountUtil () {

    }

    public static final void add(CountDto count, final StateType stateType) {
        if (stateType != null) {
            switch (stateType) {
                case STABLE:
                    count.setStable(count.getStable() + 1);
                    break;
                case UNSTABLE:
                    count.setUnstable(count.getUnstable() + 1);
                    break;
                case ERROR:
                    count.setError(count.getError() + 1);
                    break;
                case DISABLED:
                    count.setDisabled(count.getDisabled() + 1);
                    break;
            }
            count.setTotal(count.getTotal() + 1);
        }
    }
}
