package course.java.sdm.engine;

import components.main.SuperController;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class Engine {
    private static int orderNum = 0;
    private Map<Integer, Product> allProducts = new HashMap<>();
    private Map<Integer, Store> allStores = new HashMap<>();
    private UserManager userManager = new UserManager();
    private boolean isXMLLoaded = false;
    private SuperXML superXML;
    private final List<Order> orders = new ArrayList<>();

    public Engine() {

    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void loadXML(String filePath , SuperController controller, Runnable onFinish){
        this.superXML  = new SuperXML();
        Task<Boolean> currentRunningTask = new LoadTask(filePath, superXML,this);
        controller.bindTaskToUIComponents(currentRunningTask,onFinish);
        new Thread(currentRunningTask).start();

    }

    public Map<Integer, Customer> getAllCustomers() {
        return userManager.getAllCustomers();
    }

    public boolean getisXMLLoaded() {
        return isXMLLoaded;
    }

    public void initMembers(){
        isXMLLoaded = true;
        allProducts = superXML.getTempAllProducts();
        allStores = superXML.getTempAllStores();
    //    allCustomers = superXML.getTempAllCustomers();
        setProductAvgAndStoreCount();
    }



    private void setProductAvgAndStoreCount() {
        int price = 0;
        for(Product product : allProducts.values()){
            price = 0;
            product.setStoreCount(0);
            for(Store store : allStores.values()){
                if(store.getProductPrices().containsKey(product.getSerialNumber())) {
                    price += store.getProductPrices().get(product.getSerialNumber());
                    product.setStoreCount(product.getStoreCount()+1);
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

    public Order setNewOrder(Customer selectedCustomer, Map<Integer, Map<Integer, Double>> storeProductsToOrder, LocalDate date) {
        Order newOrder = new Order(++orderNum,date, storeProductsToOrder, selectedCustomer.getLocation(),selectedCustomer.getId());
        newOrder.calculateDistance(allStores);
        return newOrder;
    }

    public void addOrder(Order newOrder) {
        newOrder.calculatePrice(allStores);
        newOrder.calculateTotalPrice();
        updateProductSoldAmount(newOrder);
        Customer c = userManager.getAllCustomers().get(newOrder.getCustomerId());
        c.addOrder(newOrder);
        orders.add(newOrder);
    }

    private void updateProductSoldAmount(Order newOrder) {
        for (Map.Entry<Integer, Map<Integer, Double>> storeSoldProduct : newOrder.getStoreProducts().entrySet()) {
            for (Map.Entry<Integer, Double> productSold : storeSoldProduct.getValue().entrySet()) {
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

    public void deleteProduct(Store chosenStore, Product chosenProduct) throws Exception {
        if (chosenProduct.getStoreCount() > 1){
            if(chosenStore.getProductPrices().size()>1) {
                allProducts.put(chosenProduct.getSerialNumber(),chosenProduct);
                chosenStore.getProductPrices().remove(chosenProduct.getSerialNumber());
                allStores.put(chosenStore.getSerialNumber(),chosenStore);
                setProductAvgAndStoreCount();
                checkDiscounts(chosenStore,chosenProduct);
            }
            else
                throw new Exception("Can't delete product, The chosen store sells only in chosen product.\n");
        }
        else
            throw new Exception("Can't delete product, The chosen product is sold only in the chosen store.\n");
    }

    private void checkDiscounts(Store chosenStore, Product chosenProduct) {
        for(Discount discount: chosenStore.getDiscounts()){
            discount.setOffers(discount.getOffers().stream().filter(offer->offer.getItemId()!= chosenProduct.getSerialNumber()).collect(Collectors.toList()));
            if(discount.getOffers().size() == 0)
                chosenStore.getDiscounts().remove(discount);
            }
    }

    public void updateProductPrice(Store chosenStore, Product chosenProduct, int price) {
        chosenStore.getProductPrices().put(chosenProduct.getSerialNumber(),price);
        allStores.put(chosenStore.getSerialNumber(),chosenStore);
        setProductAvgAndStoreCount();

    }

    public void addProductToStore(int chosenSerial, Store chosenStore,int price) throws Exception {
        if(allProducts.containsKey(chosenSerial)){
            if(chosenStore.getProductPrices().containsKey(chosenSerial))
                throw new Exception("The store already sells this product\n");
            else{
                chosenStore.getProductPrices().put(chosenSerial,price);
                allStores.put(chosenStore.getSerialNumber(),chosenStore);
                setProductAvgAndStoreCount();
            }
        }
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

    public  Map<Integer,  Map<Integer, Double>> findOptimalOrder(Map<Integer, Double> productsToOrder) {
        Map<Integer,  Map<Integer, Double>> storeProducts = new HashMap<>();
        for(Map.Entry<Integer, Double> productToBuy : productsToOrder.entrySet()){
            Map<Integer, Double> newProductAndPrice = new HashMap<>();
            Optional<Store> cheapestStore = allStores.values().stream()
                    .filter(store -> store.getProductPrices().get(productToBuy.getKey())!=null).min(Comparator.comparing(store -> store.getProductPrices().get(productToBuy.getKey())));

            newProductAndPrice.put(productToBuy.getKey(),productToBuy.getValue());
            Map<Integer, Double> productAndPrice = storeProducts.get(cheapestStore.get().getSerialNumber());
            if(productAndPrice!=null){
                productAndPrice.putAll(newProductAndPrice);
            }
            else
                storeProducts.put(cheapestStore.get().getSerialNumber(),newProductAndPrice);
        }
        return storeProducts;
    }

    public Point findMapLimits() {
        int maxCustomerX = userManager.getAllCustomers().values().stream().max(Comparator.comparing(customer -> customer.getLocation().y)).get().getLocation().x;
        int maxStoreX = allStores.values().stream().max(Comparator.comparing(store -> store.getLocation().x)).get().getLocation().x;
        int maxCustomerY = userManager.getAllCustomers().values().stream().max(Comparator.comparing(customer -> customer.getLocation().y)).get().getLocation().y;
        int maxStoreY = allStores.values().stream().max(Comparator.comparing(store -> store.getLocation().y)).get().getLocation().y;
        int maxX = Math.max(maxCustomerX,maxStoreX);
        int maxY = Math.max(maxCustomerY,maxStoreY);
        return new Point(maxX,maxY);
    }
}
