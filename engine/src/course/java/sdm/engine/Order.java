package course.java.sdm.engine;
import javafx.scene.control.RadioButton;

import java.awt.*;
import java.time.LocalDate;
import java.util.*;

public class Order {
    private int serial;
    private String date;
    private Map<Integer, Double> products;
    private Map<Integer,  Map<Integer, Double>> storeProducts = new HashMap<>(); //store serial, and list of products serials to buy and amounts for each
    private double price = 0;
    private double discountsPrice;
    private Map<Integer, Double> deliveryPrices = new HashMap<>();
    private Map<Integer, Double> distance = new HashMap<>();
    private double totalPrice;
    private int customerId;
    private Point customerLocation;
    private Map<Integer,  Map<Integer, Double>> discountsProducts = new HashMap<>();

    public Order(int serial, String date, Map<Integer, Map<Integer, Double>> storeProductsToOrder, Point location,int customerId){
        this.serial = serial;
        this.date = date;
        this.storeProducts = storeProductsToOrder;
        this.customerLocation = location;
        this.customerId = customerId;
    }

    public Map<Integer, Map<Integer, Double>> getStoreProducts() {
        return storeProducts;
    }

    public String getDate() {
        return date;
    }

    public Map<Integer, Double> getDeliveryPrices() {
        return deliveryPrices;
    }

    public double getDeliveryPrice() {
        double deliveryPrice = 0;
        for(double price : deliveryPrices.values()) {
            deliveryPrice += price;
        }
        return deliveryPrice;
    }

    public Map<Integer, Map<Integer, Double>> getDiscountsProducts() {
        return discountsProducts;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getPrice() {
        return price;
    }

    public Double getDistance(int storeSerial) {
        return distance.get(storeSerial);
    }

    public int getCustomerId() {
        return customerId;
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
        for(Map.Entry<Integer, Map<Integer, Double>> storeProduct : this.storeProducts.entrySet()) {
            Store store = allStores.get(storeProduct.getKey());
            for(Integer productSerial : storeProduct.getValue().keySet()) {
                Double amount = storeProduct.getValue().get(productSerial);
                int productPrice = store.getProductPrices().get(productSerial);
                this.price += amount*productPrice;
            }
        }
    }
    public Double calculateStorePrice(int storeSerial){
        return storeProducts.get(storeSerial).values().stream().mapToDouble(Double::doubleValue).sum();
    }


    public void calculateTotalPrice(){
        totalPrice = price;
        for(Double deliveryPrice : deliveryPrices.values()){
            totalPrice += deliveryPrice;
        }
    }

    public void saveDiscounts(Discount selectedDiscount, String radio, Integer productId) {
        Map<Integer, Double> productsFromStore = discountsProducts.get(selectedDiscount.getStoreSerial());
        if(productsFromStore == null) {
            productsFromStore = new HashMap<>();
            discountsProducts.put(selectedDiscount.getStoreSerial(),productsFromStore);
        }
        if(selectedDiscount.getOperator() != Discount.Operator.ONE_OF){
            for(Offer offer : selectedDiscount.getOffers()) {
                productsFromStore.put(offer.itemId, productsFromStore.getOrDefault(offer.itemId, 0.0) + offer.quantity);
                discountsPrice += offer.quantity* offer.forAdditional;
            }
        }
        else{
            String[] parts = radio.split(" ");
            Double quantity = Double.parseDouble(parts[0]);
            productsFromStore.put(productId,productsFromStore.getOrDefault(productId, 0.0 ) + quantity);
            discountsPrice += quantity* Double.parseDouble(parts[parts.length - 2]);
        }
    }

    public void addDiscounts() {
        for( Map.Entry<Integer,  Map<Integer, Double>> discountsProducts : discountsProducts.entrySet()){
            Map<Integer, Double> productsFromStore = storeProducts.get(discountsProducts.getKey());
            for( Map.Entry<Integer, Double> product : discountsProducts.getValue().entrySet()){
                productsFromStore.put(product.getKey(), productsFromStore.getOrDefault(product.getKey(), 0.0) + product.getValue());
            }
        }
        price += discountsPrice;
    }
}