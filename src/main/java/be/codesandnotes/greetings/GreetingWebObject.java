package be.codesandnotes.greetings;

public class GreetingWebObject {
    private String message;

    public GreetingWebObject() {
    }

    public GreetingWebObject(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
