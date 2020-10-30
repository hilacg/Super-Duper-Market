package superduper.servlets;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import course.java.sdm.engine.*;
import jdk.nashorn.internal.parser.JSONParser;
import superduper.utils.ServletUtils;
import superduper.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderServlet extends HttpServlet {

    private UserManager userManager;
    private Engine engine;
    private Zone zone;
    private Map<Integer, Double> productsToOrder = new HashMap<>();
    private Map<Integer,  Map<Integer, Double>> storeProductsToOrder;
    private Order newOrder;
    private List<Discount> discounts = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        super.init();
        engine = ServletUtils.getEngine(getServletContext());
        userManager = engine.getUserManager();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String userAction = request.getParameter("action");
        ServletOutputStream out = response.getOutputStream();
        switch (userAction) {
            case "initOrder":{
                response.setContentType("text/plain;charset=UTF-8");
                productsToOrder = new HashMap<>();
                storeProductsToOrder = new HashMap<>();
                discounts = new ArrayList<>();
                response.setStatus(200);
                out.flush();
            }
            case "addToCart": {
                response.setContentType("text/plain;charset=UTF-8");
                try {
                    zone = userManager.getZone(Integer.parseInt(request.getParameter("owner")),request.getParameter("zoneName"));
                    Product productToAdd = zone.getAllProducts().get(Integer.parseInt(request.getParameter("product")));
                    productToAdd.getMethod().validateAmount(request.getParameter("amount"));
                    productsToOrder.put(productToAdd.getSerialNumber(), productsToOrder.getOrDefault(productToAdd.getSerialNumber(), 0.0) + Double.parseDouble(request.getParameter("amount")));
                    response.setStatus(200);
                    out.println("added to cart");
                    out.flush();
                } catch (Exception e) {
                    response.setStatus(401);
                    response.getOutputStream().println(e.getMessage());
                    out.flush();
                }
                break;
            }
            case "finishOrder":{
                finishOrder(request,response,out);
                break;
            }
            case "saveDiscounts":{
                saveDiscounts(request,response,out);
                break;
            }
            case "getOrderSum":{
                getOrderSum(request,response,out);
                break;
            }
        }

    }

    private void getOrderSum(HttpServletRequest request, HttpServletResponse response, ServletOutputStream out) throws IOException {
        Gson gson = new Gson();
        JsonObject mainObj = new JsonObject();
        mainObj.add("orderSum", buildTotalOrderSum());
        response.setStatus(200);
        out.println( gson.toJson(mainObj));
        out.flush();
    }

    private void saveDiscounts(HttpServletRequest request, HttpServletResponse response, ServletOutputStream out) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        JsonArray j = new JsonParser().parse(request.getParameter("discounts")).getAsJsonArray();
        j.forEach(discount ->{
            JsonObject discountObj = (JsonObject)discount;
            Discount selectedDiscount = zone
                    .getAllStores().get(discountObj.get("storeId").getAsInt()).getDiscounts().stream().filter(storeDiscount -> storeDiscount.getName().equals(discountObj.get("discountName").getAsString())).collect(Collectors.toList()).get(0);
            newOrder.saveDiscounts(selectedDiscount, discountObj.get("chosenRadio").getAsString(), discountObj.get("productId").getAsInt());
        });
        response.setStatus(200);
    }

    private void finishOrder(HttpServletRequest request, HttpServletResponse response, ServletOutputStream out) throws IOException {
        storeProductsToOrder = new HashMap<>();
        Integer userIdFromSession = SessionUtils.getUserId(request);
        Customer customer = userManager.getAllCustomers().get(userIdFromSession);
        if(request.getParameter("type").equals("dynamic")){
            storeProductsToOrder = zone.findOptimalOrder(productsToOrder);
            newOrder = zone.setNewOrder(customer,storeProductsToOrder,request.getParameter("date"),Integer.parseInt(request.getParameter("x")),Integer.parseInt(request.getParameter("y")));
        }
        else{
            storeProductsToOrder.put(Integer.parseInt(request.getParameter("store")), productsToOrder);
            newOrder = zone.setNewOrder(customer,storeProductsToOrder,request.getParameter("date"),Integer.parseInt(request.getParameter("x")),Integer.parseInt(request.getParameter("y")));
        }

        for(Map.Entry<Integer,  Map<Integer, Double>> storeAndProduct : storeProductsToOrder.entrySet()){
            Store store = zone.getAllStores().get(storeAndProduct.getKey());
            store.getDiscounts().forEach(discount -> {
                if(storeAndProduct.getValue().containsKey(discount.getItemId())){
                    double amountBought = storeAndProduct.getValue().get(discount.getItemId());
                    if (amountBought >= discount.getQuantity()) {
                        while (amountBought >= discount.getQuantity()) {
                            discounts.add(discount);
                            amountBought -= discount.getQuantity();
                        }
                    }
                }
            });
        }
        buildOrderResponse(response,out);

    }

    private void buildOrderResponse(HttpServletResponse response, ServletOutputStream out) throws IOException {
        Gson gson = new Gson();
        JsonObject mainObj = new JsonObject();
        response.setStatus(200);
        mainObj.add("discounts",buildDiscounts());
        mainObj.add("orderSum", buildOrder());
        out.println( gson.toJson(mainObj));
        out.flush();
    }

    private JsonElement buildDiscounts() {
        JsonArray jsonArray = new JsonArray();
        discounts.forEach(discount->{
            JsonObject obj = new JsonObject();
            JsonArray offers = new JsonArray();
            discount.getOffers().forEach((offer)->{
                JsonObject offerObj = new JsonObject();
                offerObj.addProperty("quantity", offer.getQuantity());
                offerObj.addProperty("productId",offer.getItemId());
                offerObj.addProperty("product", zone.getAllProducts().get(offer.getItemId()).getName());
                offerObj.addProperty("forAdditional", offer.getForAdditional());

                offers.add(offerObj);
            });
            obj.addProperty("storeId",discount.getStoreSerial());
            obj.addProperty("name",discount.getName());
            obj.addProperty("quantity",discount.getQuantity());
            obj.addProperty("product",zone.getAllProducts().get(discount.getItemId()).getName());
            obj.addProperty("operator",discount.getOperator().toString());
            obj.add("offers", offers);
            jsonArray.add(obj);
        });
        return jsonArray;
    }


    private JsonElement buildOrder() {
        JsonArray jsonArray = new JsonArray();
        storeProductsToOrder.forEach((storeId,productsAndAmount)->{
            JsonObject obj = new JsonObject();
            JsonArray products = new JsonArray();
            productsAndAmount.forEach((productId,amount)->{
                JsonObject productsObj = new JsonObject();
                productsObj.addProperty(zone.getAllProducts().get(productId).getName(), amount);
                products.add(productsObj);
            });
            obj.add(zone.getAllStores().get(storeId).getName(), products);
            jsonArray.add(obj);
        });
        return jsonArray;
    }
    private JsonElement buildTotalOrderSum() {

        JsonArray jsonArray = new JsonArray();
        newOrder.getStoreProducts().forEach((storeId,productAndAmount)->{
            JsonObject storeObj = new JsonObject();
            JsonArray storeP = new JsonArray();
            JsonArray storeD = new JsonArray();
            storeObj.addProperty("storeName",zone.getAllStores().get(storeId).getName());

            productAndAmount.forEach((productId,amount)->{
                storeP.add(buildStoreSum(productId,amount,storeId, false));
            });
            if( newOrder.getDiscountsProducts().get(storeId) !=null ) {
                newOrder.getDiscountsProducts().get(storeId).forEach((productId, amount) -> {
                    storeD.add(buildStoreSum(productId, amount, storeId, true));

                });
            }
            storeObj.add("product",storeP);
            storeObj.add("discount",storeD);
            jsonArray.add(storeObj);
        });

        return jsonArray;
    }

    private JsonElement buildStoreSum(Integer productId, Double amount,Integer storeId, boolean fromDiscount) {
        Product product = zone.getAllProducts().get(productId);
        JsonObject productObj = new JsonObject();
        productObj.addProperty("Product",product.getName());
        productObj.addProperty("Id",productId);
        productObj.addProperty("Buying Method",product.getMethod().toString());
        productObj.addProperty("Amount",amount);
        productObj.addProperty("Price",zone.getAllStores().get(storeId).getProductPrices().get(productId));
        productObj.addProperty("Total Price",zone.getAllStores().get(storeId).getProductPrices().get(productId) * amount);
        return productObj;
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
        processRequest(request, response);
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
