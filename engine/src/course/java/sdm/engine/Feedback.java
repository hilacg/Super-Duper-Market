package course.java.sdm.engine;

public class Feedback {
    private Integer storeId;
    private String customer;
    private Integer stars;
    private String message;
    private String date;

    public Feedback(int serialNumber,String customerName, int stars, String message, String date) {
        this.storeId = serialNumber;
        this.customer= customerName;
        this.stars = stars;
        this.message = message;
        this.date = date;

    }
}
