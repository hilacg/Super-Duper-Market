package course.java.sdm.engine;
import generatedClasses.SDMSell;
import generatedClasses.SDMStore;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;
import java.util.*;
import java.util.List;

@XmlRootElement(name = "SDM-store")
public class Store {
    private int serialNumber;
    private String name;
    private Map<Product,Integer> productPrices = new HashMap<>();
    private Map<Product,Integer> productsSold = new HashMap<>();
    private List<Order> orders;
    private int PPK;
    private float deliveryEarnings;
    private Point location;

    public Store(){}

    public Store(SDMStore store){
        serialNumber = store.getId();
        name = store.getName();
//        for(SDMSell sell : store.getSDMPrices().getSDMSell()){
//            productPrices.put(sell.getItemId(),sell.getPrice());
//        }
        PPK = store.getDeliveryPpk();
        location = new Point(store.getLocation().getX(),store.getLocation().getY());

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

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
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

    public Map<Product, Integer> getProductPrices() {
        return productPrices;
    }

    public Map<Product, Integer> getProductsSold() {
        return productsSold;
    }

    @Override
    public String toString() {
        int i = 1;
        String res = "--------------------" +
                "\nSerialNumber: " + serialNumber +
                "\nName: " + name +
                "\nList of products:";

        for(Map.Entry<Product, Integer> product : productPrices.entrySet()){
            res += "\n  " + i++ + ". " + (product.getKey().getName().toString()) +  "\n     price: "+(product.getValue().toString()) +
            "\n     amount sold: " + (productsSold.get(product.getKey()));
        }
        res = res + "\n     orders=" + orders +
                "\nPPK:" + PPK +
                "\ndeliveryEarnings:" + deliveryEarnings;

        return res;
    }
}
