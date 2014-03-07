package nl.unionsoft.sysstate.common.enums;

public enum StateType {
    STABLE(6), UNSTABLE(8), ERROR(10), DISABLED(2), PENDING(4);
    private final int order;

    private StateType(final int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public static StateType transfer(StateType from, StateType to) {
        StateType result = from;
        if (to.order > from.order) {
            result = to;
        }
        return result;
    }

}
