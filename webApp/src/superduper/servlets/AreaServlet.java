package superduper.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import course.java.sdm.engine.*;
import superduper.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@WebServlet
@MultipartConfig
public class AreaServlet  extends HttpServlet {
    private Engine engine;
    private UserManager userManager;

    @Override
    public void init() throws ServletException {
        super.init();
        engine = ServletUtils.getEngine(getServletContext());
        userManager = engine.getUserManager();
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userAction = request.getParameter("action");
        ServletOutputStream out = response.getOutputStream();
        switch (userAction){
            case "getZones":{
                getZones(response,out);
                break;
            }
            case "getProducts":{
                getProducts(request,response,out);
                break;
            }
            case "getStores":{
                getStores(request,response,out);
                break;
            }
        }


    }

    private void getStores(HttpServletRequest request, HttpServletResponse response, ServletOutputStream out) throws IOException {
        Gson gson = new Gson();
        JsonArray storeJson = new JsonArray();
        JsonObject mainObj = new JsonObject();
        StoreOwner storeOwner = userManager.getAllStoreOwners().values().stream().filter(owner -> owner.getZones().containsKey(request.getParameter("zoneName"))).findFirst().get();
        Zone zone = storeOwner.getZones().get(request.getParameter("zoneName"));
        zone.getAllStores().values().forEach(store ->{
            JsonObject obj = new JsonObject();
            obj.addProperty("Serial Number", store.getSerialNumber());
            obj.addProperty("Name", store.getName());
            obj.addProperty("Owner", storeOwner.getName());
            obj.addProperty("Location", store.getLocation().toString());
            obj.addProperty("products", getStoreProducts(store,zone));
            obj.addProperty("Orders", store.getTotalOrders());
            obj.addProperty("Total Product Price", store.getTotalProductPrice());
            obj.addProperty("PPK", store.getPPK());
            obj.addProperty("Total Delivery Earnings", store.getTotalDeliveryEarnings());
            storeJson.add(obj);
        });
        mainObj.add("stores",storeJson);
        response.setStatus(200);
        out.println(gson.toJson(mainObj));
        out.flush();
    }

    private String getStoreProducts(Store store, Zone zone) {
        Gson gson = new Gson();
        JsonArray productJson = new JsonArray();
        store.getProductPrices().forEach((key, value) -> {
            Product product = zone.getAllProducts().get(key);
            JsonObject obj = new JsonObject();
            obj.addProperty("Serial Number", product.getSerialNumber());
            obj.addProperty("Name", product.getName());
            obj.addProperty("Selling Method", product.getMethod().toString());
            obj.addProperty("Price", value);
            obj.addProperty("sold", store.getProductsSold().get(key));
            productJson.add(obj);
        });
        return gson.toJson(productJson);
    }

    private void getProducts(HttpServletRequest request, HttpServletResponse response, ServletOutputStream out) throws IOException {

        Gson gson = new Gson();
        JsonArray productJson = new JsonArray();
        JsonObject mainObj = new JsonObject();
       StoreOwner storeOwner = userManager.getAllStoreOwners().values().stream().filter(owner -> owner.getZones().containsKey(request.getParameter("zoneName"))).findFirst().get();
       Zone zone = storeOwner.getZones().get(request.getParameter("zoneName"));
       zone.getAllProducts().values().forEach(product ->{
           JsonObject obj = new JsonObject();
           obj.addProperty("Serial Number", product.getSerialNumber());
           obj.addProperty("Name", product.getName());
           obj.addProperty("Selling Method", product.getMethod().toString());
           obj.addProperty("Sold In", product.getStoreCount());
           obj.addProperty("Average Price", product.getAvgPrice());
           obj.addProperty("Amount Sold", product.getSoldAmount());
           productJson.add(obj);
       });
        mainObj.add("products",productJson);
        response.setStatus(200);
        out.println(gson.toJson(mainObj));
        out.flush();
    }

    private void getZones(HttpServletResponse response,ServletOutputStream out) throws IOException {
        Gson gson = new Gson();
        JsonArray zoneJson = new JsonArray();
        JsonObject mainObj = new JsonObject();
        userManager.getAllStoreOwners().values().forEach(owner -> {
            owner.getZones().values().forEach(zone->{
                JsonObject zoneObj = new JsonObject();
                zoneObj.addProperty("zoneName", zone.getName());
                zoneObj.addProperty("ownerName", owner.getName());
                zoneObj.addProperty("productsTypes", zone.getProductTypes());
                zoneObj.addProperty("amountOfStores", zone.getStoresNum());
                zoneObj.addProperty("amountOfOrders", zone.getOrderNum());
                zoneObj.addProperty("orderAvg", zone.getOrdersAvg());
                zoneJson.add(zoneObj);
            });
        });
        mainObj.add("zones",zoneJson);
        response.setStatus(200);
        out.println(gson.toJson(mainObj));
        out.flush();
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }



    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletOutputStream out = response.getOutputStream();
        response.setContentType("text/plain;charset=UTF-8");
        try {
            engine.loadXML(request.getPart("file").getInputStream(),(int)request.getSession(false).getAttribute("userId"));
            response.setStatus(200);
            out.println("File loaded successfully!");
            out.flush();
        }catch(Exception e){
            response.setStatus(401);
            out.println(e.getMessage());
            out.flush();
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
