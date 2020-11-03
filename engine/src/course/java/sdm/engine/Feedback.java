package course.java.sdm.engine;

public class Feedback {
    private Integer storeId;
    private String storeName;
    private String customer;
    private Integer stars;
    private String message;
    private String date;

    public Feedback(int serialNumber,String storeName,String customerName, int stars, String message, String date) {
        this.storeId = serialNumber;
        this.storeName = storeName;
        this.customer= customerName;
        this.stars = stars;
        this.message = message;
        this.date = date;

    }

    @Override
    public String toString() {
        return  customer + " has sent a feedback about " + storeName +
                "\ngiven stars:" + stars  +
                "\nmessage:" + message  +
                "\norder date:" + date  ;
    }
}
