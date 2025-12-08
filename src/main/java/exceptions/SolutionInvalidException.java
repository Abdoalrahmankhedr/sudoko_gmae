package exceptions;

public class SolutionInvalidException extends RuntimeException {
    public SolutionInvalidException(String message) {
        super(message);
    }
}
