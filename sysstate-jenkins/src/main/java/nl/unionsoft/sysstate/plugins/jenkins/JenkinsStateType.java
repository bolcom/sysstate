package nl.unionsoft.sysstate.plugins.jenkins;

import nl.unionsoft.sysstate.common.enums.StateType;

public enum JenkinsStateType {

    GREEN(StateType.STABLE, "STABLE"), BLUE(StateType.STABLE, "STABLE"), YELLOW(StateType.UNSTABLE, "UNSTABLE"), RED(StateType.ERROR, "FAILED"), ABORTED(StateType.STABLE,
            "ABORTED"), DISABLED(StateType.DISABLED, "DISABLED", "Project is disabled, visit project homepage for more information!"), GREY(StateType.PENDING, "PENDING",
            "Project is awaiting (first) build..."), NOTBUILT(StateType.PENDING, "NOTBUILT","Project has never been built.");

    public final StateType stateType;
    public final String description;

    public final String message;

    private JenkinsStateType(StateType stateType, String description) {
        this(stateType, description, null);
    }

    private JenkinsStateType(StateType stateType, String description, String message) {
        this.stateType = stateType;
        this.description = description;
        this.message = message;
    }

}
