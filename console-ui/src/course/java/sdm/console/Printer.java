package course.java.sdm.console;

import course.java.sdm.engine.Engine;
import course.java.sdm.engine.Product;
import course.java.sdm.engine.Store;

public class Printer {
    public static void printMenu()
    {
        System.out.println("Welcome!\n" +
                "please choose your desired action:\n" +
                "1. Load File\n" +
                "2. Exhibit Stores Details\n" +
                "3. Exhibit Products Details\n" +
                "4. Make A New Order\n" +
                "5. Exhibit Orders History\n" +
                "6. Exit\n");
    }

    public static void printStoresList(Engine engine)
    {
        for (Store store : engine.getStores().values()) {
            System.out.printf("serial number: %d\n name: %s\n PPK: %d\n ------------------------\n%n",
                    store.getSerialNumber(),store.getName(),store.getPPK());
        }
    }
    public static void printProductDetails(Engine engine) {
        if(engine.getisXMLLoaded()) {
            for (Product product : engine.getProducts().values()) {
                System.out.println(product.toString() + '\n');
            }
        }
        else{
            System.out.println("There is no XML loaded!\n");
        }
    }

    public static void printStoreDetails(Engine engine) {
        if(engine.getisXMLLoaded()) {
            for (Store store : engine.getStores().values()) {
                System.out.println(store.printStore(engine.getProducts()) + '\n');
            }
        }
        else{
            System.out.println("There is no XML loaded!\n");
        }
    }

    public static void goodbye() {
        System.out.println("Goodbye!");
    }

}

