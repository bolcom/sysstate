package nl.unionsoft.sysstate.template;

public class WriterException extends Exception {

    private static final long serialVersionUID = -1812838312201761342L;

    public WriterException(String message) {
        super(message);
    }
    
    public WriterException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
