package nl.unionsoft.sysstate.common.dto;

import java.io.Serializable;

public class CountDto implements Serializable {

    private static final long serialVersionUID = -3909506894037864215L;
    private int stable;
    private int unstable;
    private int error;
    private int pending;
    private int disabled;
    private int total;

    public int getStable() {
        return stable;
    }

    public void setStable(int stable) {
        this.stable = stable;
    }

    public int getUnstable() {
        return unstable;
    }

    public void setUnstable(int unstable) {
        this.unstable = unstable;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public int getDisabled() {
        return disabled;
    }

    public void setDisabled(int disabled) {
        this.disabled = disabled;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
