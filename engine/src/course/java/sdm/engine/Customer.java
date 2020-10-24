package course.java.sdm.engine;

import generatedClasses.SDMCustomer;
import generatedClasses.SDMSell;
import generatedClasses.SDMStore;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customer {
    private Integer id;
    private String name;
    private Point location;
    private Integer totalOrders = 0; //number of orders for customer
    private double avgOrdersPrice = 0; //without shipping
    private double avgShippingPrice = 0;
    private List<Order> orders = new ArrayList<>();
    private Account account = new Account();

    public Customer(){
    }
    public Customer(Integer id, String name){
        this.id = id;
        this.name = name;
    }
    public Customer(SDMCustomer customer){
        id = customer.getId();
        name = customer.getName();
        location = new Point(customer.getLocation().getX(),customer.getLocation().getY());

    }

    public Account getAccount() {
        return account;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Point getLocation() {
        return location;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }
    public void addOrder(Order newOrder){
        this.orders.add(newOrder);
        totalOrders++;
        calculatePriceAvg();

    }

    private void calculatePriceAvg() {
        double priceSum = orders.stream().mapToDouble(Order::getPrice).sum();
        double deliverySum = orders.stream().mapToDouble(Order::getDeliveryPrice).sum();
        this.avgOrdersPrice = priceSum/orders.size();
        this.avgShippingPrice = deliverySum/orders.size();
    }

    @Override
    public String toString() {
        return
                "id:" + id +
                "\nlocation: (" + location.x + ", " + location.y + ")" +
                "\ntotal orders:" + totalOrders +
                String.format("\naverage orders price: %.2f", avgOrdersPrice) +
                        String.format("\naverage shipping price: %.2f" , avgShippingPrice) ;
    }
}
