package course.java.sdm.engine;

import components.main.SuperController;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;


public class Engine {
    private SuperController controller;
    private static int orderNum = 0;
    private Map<Integer, Product> allProducts;
    private Map<Integer, Store> allStores;
    private Map<Integer, Customer> allCustomers;
    private boolean isXMLLoaded = false;
    private SuperXML superXML;
    private final List<Order> orders = new ArrayList<>();

    public Engine(SuperController superController) {
        this.controller = superController;
    }

    public void loadXML(String filePath ,Runnable onFinish){
        this.superXML  = new SuperXML();
        Task<Boolean> currentRunningTask = new LoadTask(filePath, superXML,this);
        controller.bindTaskToUIComponents(currentRunningTask,onFinish);
        new Thread(currentRunningTask).start();

    }

    public Map<Integer, Customer> getAllCustomers() {
        return allCustomers;
    }

    public boolean getisXMLLoaded() {
        return isXMLLoaded;
    }

    public void initMembers(){
        isXMLLoaded = true;
        allProducts = superXML.getTempAllProducts();
        allStores = superXML.getTempAllStores();
        allCustomers = superXML.getTempAllCustomers();
        setProductAvgAndStoreCount();
    }

    private void setProductAvgAndStoreCount() {
        int price = 0;
        for(Product product : allProducts.values()){
            price = 0;
            for(Store store : allStores.values()){
                if(store.getProductPrices().containsKey(product.getSerialNumber())) {
                    price += store.getProductPrices().get(product.getSerialNumber());
                    product.setStoreCount();
                }
            }
            product.setAvgPrice(price);
        }
    }

    public void executeCommand() {
    }

    public Map<Integer, Product> getProducts() {
        return allProducts;
    }
    public Map<Integer, Store> getStores() {
        return allStores;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Order setNewOrder(String chosenStore, Date deliveryDate, Map<Integer, Float> productsToOrder, Point customerLocation) {
        Order newOrder = new Order(++orderNum,deliveryDate, productsToOrder, customerLocation);
        newOrder.updateStoreProducts(Integer.parseInt(chosenStore));
        newOrder.calculateDistance(allStores);
        return newOrder;
    }

    public void addOrder(Order newOrder) {
        newOrder.calculatePrice(allStores);
        newOrder.calculateTotalPrice();
        updateProductSoldAmount(newOrder);

        orders.add(newOrder);
    }

    private void updateProductSoldAmount(Order newOrder) {
        for (Map.Entry<Integer, Map<Integer, Float>> storeSoldProduct : newOrder.getStoreProducts().entrySet()) {
            for (Map.Entry<Integer, Float> productSold : storeSoldProduct.getValue().entrySet()) {
                allProducts.get(productSold.getKey()).setSoldAmount(productSold.getValue());
                allStores.get(storeSoldProduct.getKey()).setProductsSold(productSold);
            }
        }
    }

    public Product getChosenProduct(Store chosenStore, int chosenSerial) throws Exception {
        if (chosenStore.getProductPrices().containsKey(chosenSerial))
            return allProducts.get(chosenSerial);
        else
            throw new Exception("the chosen store doesn't sell this product\n");

    }


    public boolean productSoldInOtherStore(Product chosenProduct, int chosenStoreSerial) {
        boolean res = false;
        for(Store store : allStores.values()){
            if(store.getSerialNumber() != chosenStoreSerial && store.getProductPrices().containsKey(chosenProduct.getSerialNumber())){
                res = true;
                break;
            }
        }
        return res;
    }
}
