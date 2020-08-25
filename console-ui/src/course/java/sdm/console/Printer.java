package course.java.sdm.console;

import course.java.sdm.engine.Engine;
import course.java.sdm.engine.Order;
import course.java.sdm.engine.Product;
import course.java.sdm.engine.Store;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Printer {
    private Engine engine;
    public Printer(Engine engine){
        this.engine = engine;
    }
    public static void printMenu()
    {
        System.out.println("\nWelcome!\n" +
                "please choose your desired action:\n" +
                "1. Load File\n" +
                "2. Exhibit Stores Details\n" +
                "3. Exhibit Products Details\n" +
                "4. Make A New Order\n" +
                "5. Exhibit Orders History\n" +
                "6. Exit\n");
    }

    public void printStoresList()
    {
        for (Store store : this.engine.getStores().values()) {
            System.out.printf("serial number: %d\n name: %s\n PPK: %d\n ------------------------\n%n",
                    store.getSerialNumber(),store.getName(),store.getPPK());
        }
    }
    public void printProductDetails() {
        if(engine.getisXMLLoaded()) {
            for (Product product : engine.getProducts().values()) {
                System.out.println("\n---------------------");
                System.out.println(product.toString() +
                        "\nSold in: " + product.getStoreCount()+" stores"+
                        "\nAverage price: " + product.getAvgPrice());
                System.out.printf("Sold amount: %.2f\n",product.getSoldAmount());
            }
        }
        else{
            System.out.println("There is no XML loaded!\n");
        }
    }

    public void printStoreDetails() {
        if(engine.getisXMLLoaded()) {
            for (Store store : engine.getStores().values()) {
                System.out.println(store.printStore(engine.getProducts()) + '\n');
            }
        }
        else{
            System.out.println("There is no XML loaded!\n");
        }
    }

    public void goodbye() {
        System.out.println("Goodbye!");
    }

    public void printProducts(Store chosenStore) {
        Integer price = 0;

        System.out.println("Please choose products and an amount to order. enter 'q' to finish.\n");

        for (Product product : engine.getProducts().values()) {
            price = chosenStore.getProductPrices().get(product.getSerialNumber());
            System.out.printf("Serial number:%d\n Name:%s\n Selling method:%s\n Price:%d%n",
                    product.getSerialNumber(), product.getName(), product.getMethod(), price);
        }
    }

    public void printNewOrder(Order newOrder) {
        System.out.println("Order Details:\n");
        for(Map.Entry<Integer, Map<Integer, Float>> storeProduct : newOrder.getStoreProducts().entrySet()){
            Store store = engine.getStores().get(storeProduct.getKey());
            System.out.println(store.getName() + ":\n");
            for(Integer productSerial : storeProduct.getValue().keySet()){
                float amount = storeProduct.getValue().get(productSerial);
                int price = store.getProductPrices().get(productSerial);
                System.out.println(engine.getProducts().get(productSerial));
                System.out.printf("price: %d\namount: %.2f\ntotal product price: %.2f\n", price, amount,(price*amount));
                System.out.println("--------------------------\n");
            }
            System.out.printf("Distance from store: %.2f\nStore PPK: %d\nStore delivery price: %.2f\n", newOrder.getDistance(), store.getPPK(),( newOrder.getDistance()*store.getPPK()));
            System.out.println("--------------------------\n");
        }
    }

    public void printOrderDetails(Order order) {

            int productsTypes = 0;
            int productsAmount = 0;
            SimpleDateFormat dateformat = new SimpleDateFormat();
            dateformat.applyPattern("dd/MM-HH:mm");
            System.out.println("Order serial number: " + order.getSerial() +
                    "\nDate: " + dateformat.format(order.getDate()));
            for (Map.Entry<Integer, Map<Integer, Float>> storeProduct : order.getStoreProducts().entrySet()) {
                Store store = engine.getStores().get(storeProduct.getKey());
                System.out.println("Store serial number: " + store.getSerialNumber() +
                        "\nstore name: " + store.getName());

                for (Map.Entry<Integer, Float> product : storeProduct.getValue().entrySet()) {
                    productsAmount += engine.getProducts().get(product.getKey()).getMethod().equals(Product.SellingMethod.QUANTITY) ? product.getValue() : 1;
                    productsTypes += 1;
                }
            }
            System.out.println("Number of products types: " + productsTypes +
                    "\nNumber of products sold: " + productsAmount);
            System.out.printf("\nTotal products' price: %.2f\nDelivery price: %.2f\nTotal order price: %.2f\n\n", order.getPrice(), order.getDeliveryPrice(), order.getTotalPrice());
    }
}


