package assignment.exception;

public class InvalidEmployeeCodeException extends RuntimeException {

    public InvalidEmployeeCodeException(final String message) {
        super(message);
    }

}
