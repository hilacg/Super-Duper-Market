package course.java.sdm.engine;

import generatedClasses.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SuperXML {

    public static final String JAXB_XML = "generatedClasses";
    private SuperDuperMarketDescriptor superMarket = null;
    private Map<Integer, Store> tempAllStores = new HashMap<>();
    private Map<Integer, Product> tempAllProducts = new HashMap<>();
    private Map<Integer, Customer> tempAllCustomers = new HashMap<>();
    private Map<Point,Integer> tempAllLocations = new HashMap<>();


    public SuperDuperMarketDescriptor getSuperMarket() {
        return superMarket;
    }

    public Map<Integer, Product> getTempAllProducts() {
        return tempAllProducts;
    }

    public Map<Integer, Store> getTempAllStores() {
        return tempAllStores;
    }

    public boolean load(String filePath) throws Exception {
        try {
            if (filePath.length() < 4 || filePath.substring(filePath.length() - 4).toLowerCase().compareTo(".xml") != 0) {
                throw new Exception("file is not an XML\n");
            }
            superMarket = this.XMLToObject(filePath);
            return superMarket != null;

        }catch (Exception e){
            throw e;
        }
    }

    private SuperDuperMarketDescriptor XMLToObject(String filePath) throws Exception {
        SuperDuperMarketDescriptor superMarket = null;
        try {
            File file = new File(filePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_XML);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            superMarket =  (SuperDuperMarketDescriptor) jaxbUnmarshaller.unmarshal(file);
        }catch (JAXBException e) {
            throw new Exception("couldn't load file");
        }
        return  superMarket;
    }

    public void validateXML() throws Exception {
        checkStores();
        checkProducts();
        storeProductCheck();
        productSoldTwice(); //in a single store
        checkCustomers();
        checkSales();

    }



    private void checkCustomers() throws Exception {
        tempAllCustomers = new HashMap<>();
        for(SDMCustomer customer : superMarket.getSDMCustomers().getSDMCustomer()){
            Customer c = tempAllCustomers.putIfAbsent(customer.getId(),new Customer(customer));
            if(checkLocationRange(customer.getLocation().getX(), customer.getLocation().getY()))
            {
                throw new Exception("location exception\n");
            }
            else{
                Integer p = tempAllLocations.putIfAbsent(new Point(customer.getLocation().getX(),customer.getLocation().getY()),customer.getId());
                if(p!=null){
                    throw new Exception("duplicated location error\n");
                }
            }
            if(c != null)
            {
                throw new Exception("customer duplicated id error\n");
            }
        }
    }

    private void productSoldTwice() throws Exception {
        for(SDMStore store : superMarket.getSDMStores().getSDMStore()){
            Map<Integer, Integer> p = new HashMap<>();
            for(SDMSell sell : store.getSDMPrices().getSDMSell()){
                if(p.putIfAbsent(sell.getItemId(), sell.getPrice()) != null)
                {
                    throw new Exception("single store sells same product twice");
                }
            }
        }
    }

    private void checkStores() throws Exception {
       tempAllStores = new HashMap<>();
        for(SDMStore store : superMarket.getSDMStores().getSDMStore()){
            Store s = tempAllStores.putIfAbsent(store.getId(),new Store(store));
           if(checkLocationRange(store.getLocation().getX(), store.getLocation().getY()))
            {
                throw new Exception("location exception\n");
            }
           else{
               Integer p = tempAllLocations.putIfAbsent(new Point(store.getLocation().getX(),store.getLocation().getY()),store.getId());
               if(p!=null){
                   throw new Exception("duplicated location error\n");
               }
           }
            if(s != null)
            {
                throw new Exception("store duplicated id error\n");
            }
        }
    }


    private void checkSales() throws Exception {
        for (Store store : tempAllStores.values()) {
            for (Discount discount : store.getDiscounts()) {
                for (Offer offer : discount.getOffers()) {
                    if (this.tempAllProducts.get(discount.getItemId()) == null || (this.tempAllProducts.get(offer.getItemId()) == null))
                        throw new Exception("discount product does not exist!\n");
                    if (store.getProductPrices().get(discount.getItemId()) == null || store.getProductPrices().get(offer.getItemId()) == null)
                        throw new Exception("store doesn't sell discount product!\n");
                }
            }
        }
    }

    public static boolean checkLocationRange(int x, int y) {
        return (x > 50 || x< 1 ||  y > 50 || y < 1);

    }

    private void checkProducts() throws Exception {
        boolean itemIsSold = false;
        tempAllProducts = new HashMap<>();
        for(SDMItem item : superMarket.getSDMItems().getSDMItem())
        {
            itemIsSold = false;
            Product p = tempAllProducts.putIfAbsent(item.getId(),new Product(item));
            for(SDMStore store : superMarket.getSDMStores().getSDMStore()) {
                for(SDMSell sell : store.getSDMPrices().getSDMSell()){
                    if(sell.getItemId() == item.getId())
                        itemIsSold = true;
                }
            }

            if(!itemIsSold){
                throw new Exception("unsold product exception\n");
            }
            if(p != null)
            {
                throw new Exception("product duplicated id error\n");
            }
        }
    }

    private void storeProductCheck() throws Exception {
        for(Store store : tempAllStores.values()) {
            for (Integer serialNumber : store.getProductPrices().keySet()) {
                Product p = tempAllProducts.get(serialNumber);
                if(p== null) {
                    throw new Exception("store sells an item that doesn't exist\n");
                }
            }
        }
    }

    public Map<Integer, Customer> getTempAllCustomers() {
        return tempAllCustomers;
    }
}
