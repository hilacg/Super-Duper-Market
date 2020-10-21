package course.java.sdm.engine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Zone {
    private static int orderNum = 0;
    private String name;
    private Map<Integer, Product> allProducts = new HashMap<>();
    private Map<Integer, Store> allStores = new HashMap<>();
    private final List<Order> orders = new ArrayList<>();

    public Zone(SuperXML superXML) {
        this.name = superXML.getSuperMarket().getSDMZone().getName();
        allProducts = superXML.getTempAllProducts();
        allStores = superXML.getTempAllStores();
        setProductAvgAndStoreCount();
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
        return  orders.size() >0? (orders.stream().mapToDouble(Order::getPrice).sum())/orders.size():0;
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

    public Order setNewOrder(Customer selectedCustomer, Map<Integer, Map<Integer, Double>> storeProductsToOrder, LocalDate date) {
        Order newOrder = new Order(++orderNum,date, storeProductsToOrder, selectedCustomer.getLocation(),selectedCustomer.getId());
        newOrder.calculateDistance(allStores);
        return newOrder;
    }

   /* public void addOrder(Order newOrder) {
        newOrder.calculatePrice(allStores);
        newOrder.calculateTotalPrice();
        updateProductSoldAmount(newOrder);
        Customer c = userManager.getAllCustomers().get(newOrder.getCustomerId());
        c.addOrder(newOrder);
        orders.add(newOrder);
    }*/

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
}
