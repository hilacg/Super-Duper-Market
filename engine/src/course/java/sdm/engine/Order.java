package course.java.sdm.engine;
import java.util.*;

public class Order {
    private Date date;
    private  Map<Integer, Float> products;
    private float price;
    private float deliveryPrice;
    private float totalPrice;

    public Order(Date date,Map<Integer, Float> products){
        this.date = date;
        this.products = products;
    }

}
