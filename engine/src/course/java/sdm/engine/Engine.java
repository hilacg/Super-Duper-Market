package course.java.sdm.engine;

import java.io.FileNotFoundException;
import java.util.*;
import generatedClasses.*;

public class Engine {
    private Map<Integer, Product> allProducts;
    private Map<Integer, Store> allStores;
    private SuperXML superXML = new SuperXML();

    public void loadXML(String filePath) throws Exception {
        try {
            superXML.load(filePath);
            initMembers();
        }
        catch (Exception e){
            throw e;
        }
    }

    public void initMembers(){
        allProducts = superXML.getTempAllProducts();
        initStores();
    }

    private void initStores() {
        allStores = superXML.getTempAllStores();
        for(SDMStore store : superXML.getSuperMarket().getSDMStores().getSDMStore()){
            Store s = allStores.get(store.getId());
            for(SDMSell item : store.getSDMPrices().getSDMSell()){
                s.getProductPrices().put(allProducts.get(item.getItemId()), item.getPrice());
                s.getProductsSold().put(allProducts.get(item.getItemId()),0);
            }
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
}
