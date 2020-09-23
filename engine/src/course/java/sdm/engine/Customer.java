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
    private int id;
    private String name;
    private Point location;
    private int totalOrders = 0; //number of orders for customer
    private double avgOrdersPrice = 0; //without shipping
    private double avgShippingPrice = 0;
    private List<Order> orders = new ArrayList<>();

    public Customer(){}

    public Customer(SDMCustomer customer){
        id = customer.getId();
        name = customer.getName();
        location = new Point(customer.getLocation().getX(),customer.getLocation().getY());

    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public Point getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return
                "id:" + id +
                "\nlocation: (" + location.x + ", " + location.y + ")" +
                "\ntotal orders:" + totalOrders +
                "\naverage orders price:" + avgOrdersPrice +
                "\naverage shipping price:" + avgShippingPrice ;
    }
}
