package nl.unionsoft.sysstate.common.extending;

public class ScriptExecutionResult {

    private final Object executionResult;

    private final String output;

    private final Exception exception;
    
    public ScriptExecutionResult(Object executionResult, String output) {
        this.executionResult = executionResult;
        this.output = output;
        this.exception = null;
    }

    public ScriptExecutionResult(Exception exception, String output) {
        this.executionResult = null;
        this.output = output;
        this.exception = exception;
    }
    
    public String getOutput() {
        return output;
    }

    public Object getExecutionResult() {
        return executionResult;
    }

    public Exception getException() {
        return exception;
    }

}
