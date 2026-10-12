package exceptions;

/**
 * Exception thrown when high-intensity exercise or unsafe scheduling is assigned to a user with cardiac conditions.
 */
public class CardiacRiskException extends Exception {
    public CardiacRiskException(String message) {
        super(message);
    }
    public String toString()
    {    
        return "Patient cannot be assigned high intense exercies";
    }
}
