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

    public void printMenu()
    {
        System.out.println("\nWelcome!\n" +
                "please choose your desired action:\n" +
                "1. Load File\n" +
                "2. Exhibit Stores Details\n" +
                "3. Exhibit Products Details\n" +
                "4. Make A New Order\n" +
                "5. Exhibit Orders History\n" +
                "6. Update Store Products\n" +
                "7. Exit\n");
    }

    public void printProductUpdateChoices() {
        System.out.println("\nplease choose your desired action:\n" +
                "1. Delete Product\n" +
                "2. Add Product\n" +
                "3. Update Product Price\n");
    }

    public void printStoresList()
    {
        for (Store store : this.engine.getStores().values()) {
            System.out.printf("------------------------\nserial number: %d\nname: %s\nPPK: %d\n",
                    store.getSerialNumber(),store.getName(),store.getPPK());
        }
        System.out.println("");
    }
    public void printProductDetails() {
        if(engine.getisXMLLoaded()) {
            System.out.println("The Products are:");
            for (Product product : engine.getProducts().values()) {
                System.out.println(product.toString() +
                        "\nSold in: " + product.getStoreCount()+" stores");
                System.out.printf("Average price: %.2f \nSold amount: %.2f\n\n",product.getAvgPrice(), product.getSoldAmount());
            }
        }
        else{
            System.out.println("There is no XML loaded!\n");
        }
    }

    public void printStoreDetails() {
        if(engine.getisXMLLoaded()) {
            System.out.println("The stores are:");
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

        for (Product product : engine.getProducts().values()) {
            price = chosenStore.getProductPrices().get(product.getSerialNumber());
            System.out.printf("------------------\nSerial number: %d\nName: %s\nSelling method: %s\nPrice: %d\n\n",
                    product.getSerialNumber(), product.getName(), product.getMethod(), price);
        }
    }

    public void printNewOrder(Order newOrder) {
        System.out.println("Order Details:\n");
        for(Map.Entry<Integer, Map<Integer, Float>> storeProduct : newOrder.getStoreProducts().entrySet()){
            Store store = engine.getStores().get(storeProduct.getKey());
            System.out.println(store.getName() + ":\n");
            for(Integer productSerial : storeProduct.getValue().keySet()) {
                float amount = storeProduct.getValue().get(productSerial);
                int price = store.getProductPrices().get(productSerial);
                System.out.println(engine.getProducts().get(productSerial));
                System.out.printf("price: %d\namount: %.2f\ntotal product price: %.2f\n", price, amount, (price * amount));
            }
            System.out.println("\n===============================");
            System.out.printf("Distance from store: %.2f\nStore PPK: %d\nStore delivery price: %.2f\n", newOrder.getDistance().get(storeProduct.getKey()), store.getPPK(), (newOrder.getDistance().get(storeProduct.getKey()) * store.getPPK()));
            System.out.println("===============================\n");
        }
    }

    public void printOrderDetails(Order order) {

            int productsTypes = 0;
            int productsAmount = 0;
            SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM-HH:mm");
            System.out.println("-----------------------");
            System.out.println("Order serial number: " + order.getSerial() +
                    "\nDate: " + dateformat.format(order.getDate()));
            for (Map.Entry<Integer, Map<Integer, Float>> storeProduct : order.getStoreProducts().entrySet()) {
                Store store = engine.getStores().get(storeProduct.getKey());
                System.out.println("Store serial number: " + store.getSerialNumber() +
                        "\nStore name: " + store.getName());

                for (Map.Entry<Integer, Float> product : storeProduct.getValue().entrySet()) {
                    productsAmount += engine.getProducts().get(product.getKey()).getMethod().equals(Product.SellingMethod.QUANTITY) ? product.getValue() : 1;
                    productsTypes += 1;
                }
            }
            System.out.println("Number of products types: " + productsTypes +
                    "\nNumber of products sold: " + productsAmount);
            System.out.printf("Total products' price: %.2f\nDelivery price: %.2f\nTotal order price: %.2f\n\n", order.getPrice(), order.getDeliveryPrice(), order.getTotalPrice());
    }

}


