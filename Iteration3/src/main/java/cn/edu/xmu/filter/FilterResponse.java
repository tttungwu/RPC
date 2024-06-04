package cn.edu.xmu.filter;

public class FilterResponse {

    private final Boolean isAccepted;

    private final Exception exception;

    public FilterResponse(Boolean isAccepted) {
        this.isAccepted = isAccepted;
        this.exception = null;
    }

    public FilterResponse(Exception exception) {
        this.isAccepted = null;
        this.exception = exception;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public Exception getException() {
        return exception;
    }
}
