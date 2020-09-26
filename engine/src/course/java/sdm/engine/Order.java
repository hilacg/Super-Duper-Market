package course.java.sdm.engine;
import javafx.scene.control.RadioButton;

import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class Order {
    private LocalDate date;
    private Map<Integer, Double> products;
    private Map<Integer,  Map<Integer, Double>> storeProducts = new HashMap<>(); //store serial, and list of products serials to buy and amounts for each
    private double price;
    private Map<Integer, Double> deliveryPrices = new HashMap<>();
    private Map<Integer, Double> distance = new HashMap<>();
    private double totalPrice;
    private Point customerLocation;
    private List<Discount> discounts = new ArrayList<>();

    public Order(int serial, LocalDate date, Map<Integer, Map<Integer, Double>> storeProductsToOrder, Point location){
        this.date = date;
        this.storeProducts = storeProductsToOrder;
        this.customerLocation = location;
    }

    public Map<Integer, Map<Integer, Double>> getStoreProducts() {
        return storeProducts;
    }

    public LocalDate getDate() {
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

    public double getPrice() {
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

    public void saveDiscounts(Discount selectedDiscount, RadioButton radio) {

    }

    public void addDiscounts(Discount selectedDiscount, RadioButton radio) {
        Map<Integer, Double> productsFromStore = storeProducts.get(selectedDiscount.getStoreSerial());
        if(radio == null){
            selectedDiscount.getOffers().forEach(offer->{
                productsFromStore.put(offer.itemId,productsFromStore.getOrDefault(offer.itemId, 0.0 ) + offer.quantity);
                price += offer.quantity* offer.forAdditional;
            });
        }
        else{
            Double quantity = Double.parseDouble(radio.getText().split(" ")[0]);
            productsFromStore.put(Integer.parseInt(radio.getId()),productsFromStore.getOrDefault(Integer.parseInt(radio.getId()), 0.0 ) + quantity);
            price += quantity* Double.parseDouble(radio.getText().substring(radio.getText().lastIndexOf(" ")+1));
        }
    }
}
