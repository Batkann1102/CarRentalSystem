package mn.edu.num.carrental.core.exception;

public class CarDeletionNotAllowedException extends RuntimeException {

    public CarDeletionNotAllowedException(String message) {
        super(message);
    }
}

