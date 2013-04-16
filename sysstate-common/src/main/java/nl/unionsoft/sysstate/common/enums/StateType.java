package nl.unionsoft.sysstate.common.enums;

public enum StateType {
    STABLE(6), UNSTABLE(8), ERROR(10), DISABLED(2), PENDING(4);
    private final int order;

    private StateType (final int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

}
