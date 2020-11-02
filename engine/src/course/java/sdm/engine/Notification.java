package course.java.sdm.engine;

public class Notification {
    private boolean sent = false;
    private String message = "";
    private Notification.Type type;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        ORDER{public String toString(){return "new order";}},
        FEEDBACK{public String toString(){return "new feedback";}},
        STORE{public String toString(){return "new store";}};

        public abstract String toString();
    }
}
