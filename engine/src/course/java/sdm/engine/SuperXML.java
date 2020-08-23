package course.java.sdm.engine;

import generatedClasses.SDMItem;
import generatedClasses.SDMSell;
import generatedClasses.SDMStore;
import generatedClasses.SuperDuperMarketDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SuperXML {

    public static final String JAXB_XML = "generatedClasses";
    private SuperDuperMarketDescriptor superMarket = null;
    private Map<Integer, Store> tempAllStores = new HashMap<>();
    private Map<Integer, Product> tempAllProducts = new HashMap<>();


    public SuperDuperMarketDescriptor getSuperMarket() {
        return superMarket;
    }

    public Map<Integer, Product> getTempAllProducts() {
        return tempAllProducts;
    }

    public Map<Integer, Store> getTempAllStores() {
        return tempAllStores;
    }

    public void load(String filePath) throws Exception {
        try {
            if (filePath.length() < 4 || filePath.substring(filePath.length() - 4).toLowerCase().compareTo(".xml") != 0) {
                throw new Exception("file is not an XML\n");
            }
            superMarket = this.XMLToObject(filePath);
            if (superMarket != null) {
                validateXML();
            }
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

    private void validateXML() throws Exception {
        checkStores();
        checkProducts();
        storeProductCheck();
        productSoldTwice(); //in a single store

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
           if(checkLocation(store.getLocation().getX(), store.getLocation().getY()))
            {
                throw new Exception("locaion exception\n");
            }
            if(s != null)
            {
                throw new Exception("duplicated id error\n");
            }
        }
    }

    public static boolean checkLocation(int x, int y) {
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
                throw new Exception("duplicated id error\n");
            }
        }
    }

    private void storeProductCheck() throws Exception {
        for(Store store : tempAllStores.values()) {
            for (Product producr : store.getProductPrices().keySet()) {
                Product p = tempAllProducts.get(producr.getSerialNumber());
                if(p== null) {
                    throw new Exception("store sells an item that doesn't exist\n");
                }
            }
        }
    }
}
