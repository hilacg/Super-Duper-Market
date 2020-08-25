package course.java.sdm.engine;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Order {
    private int serial;
    private Date date;
    private Map<Integer, Float> products;
    private Map<Integer,  Map<Integer, Float>> storeProducts = new HashMap<>(); //store serial, and list of products serials to buy and amounts for each
    private float price;
    private double deliveryPrice;
    private double distance;
    private double totalPrice;
    private Point customerLocation;

    public Order(int serial,Date date,Map<Integer, Float> products, Point customerLocation){
        this.serial = serial;
        this.date = date;
        this.products = products;
        this.customerLocation = customerLocation;
    }

    public Map<Integer, Map<Integer, Float>> getStoreProducts() {
        return storeProducts;
    }

    public Date getDate() {
        return date;
    }

    public int getSerial() {
        return serial;
    }

    public double getDeliveryPrice() {
        return deliveryPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public float getPrice() {
        return price;
    }

    public double getDistance() {
        return distance;
    }

    public void updateStoreProducts(int storeSerial){
        storeProducts.put(storeSerial, products);
    }
    public void calculateDistance(Map<Integer,Store>allStores) {
        for(Store store : allStores.values()) {
            distance = Math.sqrt((Math.pow(customerLocation.x - store.getLocation().getX(),2) + Math.pow(customerLocation.y - store.getLocation().getY(),2)));
            deliveryPrice = distance * store.getPPK();
        }
    }
    public void calculatePrice(Map<Integer,Store> allStores){
        for(Map.Entry<Integer, Map<Integer, Float>> storeProduct : this.storeProducts.entrySet()) {
            Store store = allStores.get(storeProduct.getKey());
            for(Integer productSerial : storeProduct.getValue().keySet()) {
                float amount = storeProduct.getValue().get(productSerial);
                int productPrice = store.getProductPrices().get(productSerial);
                this.price += amount*productPrice;
            }
        }
    }
    public void calculateTotalPrice(){
        totalPrice= price+deliveryPrice;
    }
}
