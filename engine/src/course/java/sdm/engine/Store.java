package course.java.sdm.engine;
import generatedClasses.SDMDiscount;
import generatedClasses.SDMSell;
import generatedClasses.SDMStore;

import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;
import java.util.*;
import java.util.List;

@XmlRootElement(name = "SDM-store")
public class Store {
    private int serialNumber;
    private int ownerId;
    private String name;
    private Map<Integer,Integer> productPrices = new HashMap<>();//serial price
    private Map<Integer,Double> productsSold = new HashMap<>(); //serial total earnings
    private List<Order> orders = new ArrayList<>();
    private List<Discount> discounts =  new ArrayList<>();
    private int PPK;
    private float deliveryEarnings;
    private Point location;
    private List<Feedback> storeFeedback = new ArrayList<>();;

    public Store(){}

    public Store(SDMStore store,int ownerId) {
        serialNumber = store.getId();
        this.ownerId = ownerId;
        name = store.getName();
        for (SDMSell sell : store.getSDMPrices().getSDMSell()) {
            productPrices.put(sell.getItemId(), sell.getPrice());
            productsSold.put(sell.getItemId(), 0.0);
        }
        if (store.getSDMDiscounts() != null)
            for (SDMDiscount discount : store.getSDMDiscounts().getSDMDiscount()) {
                discounts.add(new Discount(discount, serialNumber));
            }
        PPK = store.getDeliveryPpk();
        location = new Point(store.getLocation().getX(), store.getLocation().getY());

    }

    public Store(Point location, int storeId, String name,int ownerId,int ppk){
        this.location = location;
        this.serialNumber = storeId;
        this.name = name;
        this.ownerId = ownerId;
        this.PPK = ppk;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Integer getTotalOrders(){
        return orders.size();
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public int getPPK() {
        return PPK;
    }

    public String getName() {
        return name;
    }

    public Point getLocation() {
        return location;
    }

    public String getStringLocation() {
        return "("+location.x+", "+location.y+")";
    }
    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public List<Feedback> getStoreFeedback() {
        return storeFeedback;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeliveryEarnings(float deliveryEarnings) {
        this.deliveryEarnings = deliveryEarnings;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void setPPK(int PPK) {
        this.PPK = PPK;
    }

    public void setProductsSold(Map.Entry<Integer, Double> productsSold) {
        this.productsSold.put(productsSold.getKey(), this.productsSold.get(productsSold.getKey()) + productsSold.getValue());
    }

    public void addFeedback(int stars, String message, Customer customer) {
        String Date = customer.getOrders().get(customer.getOrders().size()-1).getDate();
        Feedback newFeedback = new Feedback(this.serialNumber,this.name,customer.getName(), stars,message,Date);
        this.storeFeedback.add(newFeedback);
    }
    public Map<Integer, Integer> getProductPrices() {
        return productPrices;
    }

    public Map<Integer, Double> getProductsSold() {
        return productsSold;
    }

    public double getTotalProductPrice() {
        return productsSold.values().stream().mapToDouble(Double::doubleValue).sum();
    }
    public double getTotalDeliveryEarnings(){
        return orders.stream().mapToDouble(order -> order.getDeliveryPrices().values().stream().mapToDouble(Double::doubleValue).sum()).sum();
    }


    public void deleteProduct(int chosenProductSerial) {
        productPrices.remove(chosenProductSerial);
        productsSold.remove(chosenProductSerial);
    }

    public double calculateDistance(Point location) {

          return Math.sqrt((Math.pow(location.x - this.location.x,2) + Math.pow(location.y -this.location.y,2)));

        }

    public String printStore(Map<Integer, Product> allProducts) {
        int i = 1;
        String res = "--------------------" +
                "\nSerialNumber: " + serialNumber +
                "\nName: " + name +
                "\nList of products:";

        for(Map.Entry<Integer, Integer> product : productPrices.entrySet()){
            res += "\n  " + i++ + ". " + (allProducts.get(product.getKey()).getName()) +  "\n     price: "+(product.getValue().toString()) +
            "\n     amount sold: " + (productsSold.get(product.getKey()));
        }
        res = res + "\norders:" + (orders.size() > 0 ? orders : " none") +
                "\nPPK: " + PPK +
                "\ndeliveryEarnings: " + deliveryEarnings;

        return res;
    }

    public String storeNotify(String owner, String zoneName, int zoneProducts){
        return  owner + " has opened a new store in " + zoneName +
                "\nstore name: " + name +
                        "\nlocation: (" + location.x + ", " + location.y + ")" +
                        "\nsells: " + productsSold.size() + "/" + zoneProducts + " products";
    }

    @Override
    public String toString() {
        return
                "serialNumber: " + serialNumber +
                "\nPPK: " + PPK +
                "\ndeliveryEarnings: " + deliveryEarnings +
                "\nlocation: (" + location.x + ", " + location.y + ")" ;
    }

}
