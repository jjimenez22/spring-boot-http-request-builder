package ve.jj.requestbuilder;

public class RequestBuilderException extends RuntimeException {
    private BuildErrorType state;

    public RequestBuilderException() {
        state = null;
    }

    public RequestBuilderException(BuildErrorType state) {
        this.state = state;
    }

    public RequestBuilderException(String message, BuildErrorType state) {
        super(message);
        this.state = state;
    }

    public RequestBuilderException(String message, Throwable cause, BuildErrorType state) {
        super(message, cause);
        this.state = state;
    }

    public RequestBuilderException(Throwable cause, BuildErrorType state) {
        super(cause);
        this.state = state;
    }

    public RequestBuilderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, BuildErrorType state) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.state = state;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\nStatus: " + state;
    }

    public BuildErrorType getState() {
        return state;
    }

    public void setState(BuildErrorType state) {
        this.state = state;
    }

    public enum BuildErrorType {
        NO_REQUEST_BUILT,
        REQUEST_FAILED,
        RESPONSE_READING_FAILED,
        ERROR_READING_FAILED
    }
}
