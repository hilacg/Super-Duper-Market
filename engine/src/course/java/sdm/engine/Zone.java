package course.java.sdm.engine;

import generatedClasses.SDMStore;

import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Zone {
    private static int orderNum = 0;
    private int ownerId;
    private String name;
    private Map<Integer, Product> allProducts = new HashMap<>();
    private Map<Integer, Store> allStores = new HashMap<>();
    private final List<Order> orders = new ArrayList<>();
    private Map<Point,Integer> AllLocations = new HashMap<>();

    public Zone(SuperXML superXML,int userId) {
        this.name = superXML.getSuperMarket().getSDMZone().getName();
        this.ownerId = userId;
        allProducts = superXML.getTempAllProducts();
        allStores = superXML.getTempAllStores();
        AllLocations = superXML.getTempAllLocations();
        setProductAvgAndStoreCount();
    }

    public int getOwnerId() {
        return ownerId;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Map<Integer, Product> getAllProducts() {
        return allProducts;
    }

    public Map<Integer, Store> getAllStores() {
        return allStores;
    }

    public String getName() {
        return name;
    }

    public int getProductTypes(){
        return allProducts.size();
    }

    public int getStoresNum(){
        return allStores.size();
    }
    public int getOrderNum(){
        return orders.size();
    }
    public double getOrdersAvg(){
        return  orders.size() >0? (orders.stream().mapToDouble(Order::getTotalPrice).sum())/orders.size():0;
    }
    public void addNewStore(Store newStore) throws Exception {
        checkStores(newStore);
        this.allStores.put(newStore.getSerialNumber(), newStore);
        setProductAvgAndStoreCount();
    }

    private void setProductAvgAndStoreCount() {
        int price;
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
    public void checkStores(Store store) throws Exception {
            Store s = allStores.get(store.getSerialNumber());
            if(checkLocationRange(store.getLocation().x, store.getLocation().y)){
                throw new Exception("location exception\n");
            }
            else{
                Integer p = AllLocations.putIfAbsent(new Point(store.getLocation().x, store.getLocation().y),store.getSerialNumber());
                if(p!=null){
                    throw new Exception("duplicated location error\n");
                }
            }
            if(s != null)
            {
                throw new Exception("store duplicated id error\n");
            }
        }

    public static boolean checkLocationRange(int x, int y) {
        return (x > 50 || x< 1 ||  y > 50 || y < 1);

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

    public Order setNewOrder(Customer customer, Map<Integer, Map<Integer, Double>> storeProductsToOrder, String date, int x, int y) {
        Order newOrder = new Order(++orderNum,date, storeProductsToOrder, new Point(x,y),customer.getId(),this.name);
        newOrder.calculateDistance(allStores);
        return newOrder;
    }

    public void addOrder(Order newOrder,Customer customer) {
        updateProductSoldAmount(newOrder);
        Customer c = customer;
        c.addOrder(newOrder);
        orders.add(newOrder);
    }
}
