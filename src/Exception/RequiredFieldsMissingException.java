package Exception;

public class RequiredFieldsMissingException extends Exception{
    public RequiredFieldsMissingException(String message) {
        super(message);
    }
}
