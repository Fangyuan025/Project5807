/**
 * Custom exception for the Quadratic Equation Solver
 * Thrown when precision is lost during calculation
 */
public class NotEnoughPrecisionException extends Exception {
    
    public NotEnoughPrecisionException() {
        super("Not enough precision to calculate an accurate solution");
    }
    
    public NotEnoughPrecisionException(String message) {
        super(message);
    }
}