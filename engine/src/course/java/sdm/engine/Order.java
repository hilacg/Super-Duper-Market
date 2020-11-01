package course.java.sdm.engine;
import javafx.scene.control.RadioButton;

import java.awt.*;
import java.time.LocalDate;
import java.util.*;

public class Order {
    private int serial;
    private String zoneName;
    private String date;
    private Map<Integer,  Map<Integer, Double>> storeProducts = new HashMap<>(); //store serial, and list of products serials to buy and amounts for each
    private double price = 0;
    private double discountsPrice;
    private Map<Integer, Double> deliveryPrices = new HashMap<>();
    private Map<Integer, Double> distance = new HashMap<>();
    private double totalPrice;
    private int customerId;
    private Point customerLocation;
    private Map<Integer,Map<Integer,DiscountProduct>> discountsProducts = new HashMap<>();//storeId,productId,price(forAdditionl),amount

    public Order(int serial, String date, Map<Integer, Map<Integer, Double>> storeProductsToOrder, Point location,int customerId,String zoneName){
        this.serial = serial;
        this.date = date;
        this.storeProducts = storeProductsToOrder;
        this.customerLocation = location;
        this.customerId = customerId;
        this.zoneName = zoneName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public double getDiscountsPrice() {
        return discountsPrice;
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

    public  Map<Integer,Map<Integer,DiscountProduct>> getDiscountsProducts() {
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
    public Double calculateStorePrice(Store store){
       double res = 0;
       for(Map.Entry<Integer, Double> productAndAmount :storeProducts.get(store.getSerialNumber()).entrySet()){
           int productPrice = store.getProductPrices().get(productAndAmount.getKey());
           res += productPrice*productAndAmount.getValue();
       }
       return res;
    }

    public Double calculateStoreDiscount(int storeSerial){
        if(discountsProducts.get(storeSerial)!= null)
        return discountsProducts.get(storeSerial).values().stream().mapToDouble(DiscountProduct::getAmountPrice).sum();
        else
           return 0.0;
    }
    public void calculateTotalPrice(){
        totalPrice = price;
        for(Double deliveryPrice : deliveryPrices.values()){
            totalPrice += deliveryPrice;
        }
        for(Map<Integer,DiscountProduct> discountPrice : discountsProducts.values()) {
            for (DiscountProduct discount : discountPrice.values()) {
                totalPrice += discount.amount*discount.price;
            }
        }
    }

    public void saveDiscounts(Discount selectedDiscount, String radio, Integer productId) {
        Map<Integer, DiscountProduct> productsFromStore = discountsProducts.get(selectedDiscount.getStoreSerial());
        if (productsFromStore == null) {
            productsFromStore = new HashMap<>();
            discountsProducts.put(selectedDiscount.getStoreSerial(), productsFromStore);
        }
        if (radio.equals("")) {
            for (Offer offer : selectedDiscount.getOffers()) {
                DiscountProduct defaultP = productsFromStore.getOrDefault(offer.itemId, new DiscountProduct(offer.itemId, offer.forAdditional));
                defaultP.amount += offer.quantity;
                productsFromStore.put(offer.itemId, defaultP);
                discountsPrice += offer.quantity * offer.forAdditional;
            }
        }
         else {
            String[] parts = radio.split(" ");
            Double quantity = Double.parseDouble(parts[0]);
            DiscountProduct defaultP = productsFromStore.getOrDefault(productId, new DiscountProduct(productId, Integer.parseInt(parts[parts.length - 2])));
            defaultP.amount +=  quantity;
            productsFromStore.put(productId, defaultP);
            discountsPrice += quantity * Double.parseDouble(parts[parts.length - 2]);
        }
    }

    public void addDiscounts() {
        for( Map.Entry<Integer,Map<Integer,DiscountProduct>> discountsProducts : discountsProducts.entrySet()) {
            Map<Integer, Double> productsFromStore = storeProducts.get(discountsProducts.getKey());
            for (Map.Entry<Integer, DiscountProduct> product : discountsProducts.getValue().entrySet()) {
                productsFromStore.put(product.getKey(), product.getValue().amount);
            }
        }
        price += discountsPrice;
    }

    public class DiscountProduct{
        Integer productId;
        Integer price;
        Double amount= 0.0;

        public DiscountProduct(Integer itemId, Integer forAdditional) {
            productId = itemId;
            amount = 0.0;
            price= forAdditional;
        }

        public Double getAmount() {
            return amount;
        }

        public Double getAmountPrice(){return price*amount;}


        public Integer getPrice() {
            return price;
        }

        public Integer getProductId() {
            return productId;
        }
    }
}