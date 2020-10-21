package course.java.sdm.engine;

import generatedClasses.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SuperXML {

    public static final String JAXB_XML = "generatedClasses";
    private Engine engine;
    private SuperDuperMarketDescriptor superMarket = null;
    private Map<Integer, Store> tempAllStores = new HashMap<>();
    private Map<Integer, Product> tempAllProducts = new HashMap<>();
    private Map<Integer, Customer> tempAllCustomers = new HashMap<>();
    private Map<Point,Integer> tempAllLocations = new HashMap<>();

    public SuperXML(Engine engine){
        this.engine = engine;
    }

    public SuperDuperMarketDescriptor getSuperMarket() {
        return superMarket;
    }

    public Map<Integer, Product> getTempAllProducts() {
        return tempAllProducts;
    }

    public Map<Integer, Store> getTempAllStores() {
        return tempAllStores;
    }

    public void load(InputStream fileContent) throws Exception {
        try {
            superMarket = this.XMLToObject(fileContent);
            if( superMarket != null){
                validateXML();
            }
        }catch (Exception e){
            throw e;
        }
    }

    private SuperDuperMarketDescriptor XMLToObject(InputStream fileContent) throws Exception {
        SuperDuperMarketDescriptor superMarket = null;
        try {
   //         File file = new File(fileContent);
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_XML);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            superMarket =  (SuperDuperMarketDescriptor) jaxbUnmarshaller.unmarshal(fileContent);
        }catch (JAXBException e) {
            throw new Exception("couldn't load file");
        }
        return  superMarket;
    }

    public void validateXML() throws Exception {
        checkZone();
        checkStores();
        checkProducts();
        storeProductCheck();
        productSoldTwice(); //in a single store
    //    checkCustomers();
        checkSales();

    }

    private void checkZone() throws Exception {
        for (StoreOwner owner : engine.getUserManager().getAllStoreOwners().values()) {
            if (owner.getZones().containsKey(superMarket.getSDMZone().getName())) {
                throw new Exception("The Zone already exists in the system\n");
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
