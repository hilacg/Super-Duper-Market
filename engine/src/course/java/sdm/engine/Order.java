package course.java.sdm.engine;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Order {
    private Date date;
    private Map<Integer, Float> products;
    private Map<Integer,  Map<Integer, Float>> storeProducts = new HashMap<>(); //store serial, and list of products serials to buy and amounts for each
    private float price;
    private Map<Integer, Double> deliveryPrices = new HashMap<>();
    private Map<Integer, Double> distance = new HashMap<>();
    private double totalPrice;
    private Point customerLocation;

    public Order(int serial,Date date,Map<Integer, Float> products, Point customerLocation){
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


    public double getDeliveryPrice() {
        double deliveryPrice = 0;
        for(double price : deliveryPrices.values()) {
            deliveryPrice += price;
        }
        return deliveryPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public float getPrice() {
        return price;
    }

    public Map<Integer, Double> getDistance() {
        return distance;
    }

    public void updateStoreProducts(int storeSerial){
        storeProducts.put(storeSerial, products);
    }

    public void calculateDistance(Map<Integer,Store>allStores) {
        for(Integer storeSerial : storeProducts.keySet()) {
            Store store = allStores.get(storeSerial);
            double storeDistance = Math.sqrt((Math.pow(customerLocation.x - store.getLocation().getX(),2) + Math.pow(customerLocation.y - store.getLocation().getY(),2)));
            distance.put(storeSerial,storeDistance);
            deliveryPrices.put(storeSerial, storeDistance * store.getPPK());
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
        totalPrice = price;
        for(Double deliveryPrice : deliveryPrices.values()){
            totalPrice += deliveryPrice;
        }
    }
}
