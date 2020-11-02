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
import java.awt.*;
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
                productsToOrder = new HashMap<>();
                storeProductsToOrder = new HashMap<>();
                discounts = new ArrayList<>();
                response.setStatus(200);
                out.flush();
                break;
            }
            case "switchZone":{
                getServletContext().setAttribute("zone", zone);
                zone = userManager.getZone(Integer.parseInt(request.getParameter("ownerId")),request.getParameter("zoneName"));
                response.setStatus(200);
                out.flush();
            }
            case "addToCart": {
                response.setContentType("text/plain;charset=UTF-8");
                try {
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
                Gson gson = new Gson();
                JsonObject mainObj= getOrderSum(response,out,newOrder);
                response.setStatus(200);
                out.println( gson.toJson(mainObj));
                out.flush();
                break;
            }
            case "confirmOrder":{
                response.setContentType("text/plain;charset=UTF-8");
                zone.addOrder(newOrder,userManager.getAllCustomers().get(newOrder.getCustomerId()));
                notifyOrder();
                setOwnerOrders();
                response.setStatus(200);
                out.flush();
            }
            case "getCustomerOrders":{
                getCustomerOrders(response, SessionUtils.getUserId(request),out);
                break;
            }
            case "getStoreOrders":{
                getStoreOrders(request,response,out);
                break;
            }
            case "feedback":{
                addFeedbackToStore(request,response,SessionUtils.getUserId(request),out);
                break;
            }
        }

    }

    private void notifyOrder() {
        newOrder.getStoreProducts().keySet().forEach(storeId->{
            int ownerId = zone.getAllStores().get(storeId).getOwnerId();
            Notification note = userManager.getAllStoreOwners().get(ownerId).getNotification();
            note.setMessage("sold!");
            note.setSent(false);
        });
    }

    private void notifyfeedback(int ownerId) {
            Notification note = userManager.getAllStoreOwners().get(ownerId).getNotification();
            note.setMessage("got feedback!");
            note.setSent(false);
    }


    private void getStoreOrders(HttpServletRequest request,HttpServletResponse response,ServletOutputStream out) throws IOException {
        Gson gson = new Gson();
        JsonArray ordersArray = new JsonArray();
        Store store = zone.getAllStores().get(Integer.parseInt(request.getParameter("storeId")));
        store.getOrders().forEach(order -> {
              ordersArray.add(storeOrderProducts(response, out,order ));
                }
        );

        response.setStatus(200);
        out.println(gson.toJson(ordersArray));
        out.flush();
    }

    private JsonElement storeOrderProducts(HttpServletResponse response, ServletOutputStream out, Order order) {
        Gson gson = new Gson();
        JsonObject storeObj = new JsonObject();
        JsonArray storeP = new JsonArray();
        JsonArray storeD = new JsonArray();
        storeOrderSum(order);
        order.getStoreProducts().forEach((storeId,productAndAmount)->{
            productAndAmount.forEach((productId,amount)->{
                storeP.add(buildStoreSum(productId,amount,storeId, zone.getAllStores().get(storeId).getProductPrices().get(productId)));
            });
            if( order.getDiscountsProducts().get(storeId) !=null ) {
                order.getDiscountsProducts().get(storeId).forEach((productId, discountProducts) -> {
                    storeD.add(buildStoreSum(productId, discountProducts.getAmount(), storeId, discountProducts.getPrice()));

                });
    }
            storeObj.add("ordersum",storeOrderSum(order));
            storeObj.add("product",storeP);
            storeObj.add("discount",storeD);
    });
        return storeObj;
    }

    private JsonElement storeOrderSum(Order order) {
        JsonObject orderSum = new JsonObject();
        orderSum.addProperty("order number", order.getSerial());
        orderSum.addProperty("date", order.getDate());
        orderSum.addProperty("customer name", userManager.getAllCustomers().get(order.getCustomerId()).getName());
        orderSum.addProperty("customer location", "("+order.getCustomerLocation().x+", "+order.getCustomerLocation().y+")");
        orderSum.addProperty("number of item bought", order.getStoreProducts().values().size());
        orderSum.addProperty("total products price", order.getPrice());
        orderSum.addProperty("delivery price", order.getDeliveryPrice());
        orderSum.addProperty("total order price", order.getTotalPrice());
        return orderSum;
    }

    private void addFeedbackToStore(HttpServletRequest request, HttpServletResponse response, Integer userId, ServletOutputStream out) {
        response.setContentType("text/plain;charset=UTF-8");
        JsonArray feedbacks = new JsonParser().parse(request.getParameter("feedbacks")).getAsJsonArray();
        feedbacks.forEach(feedback -> {
            JsonObject feedbackObj = (JsonObject) feedback;
            Store chosenStore = zone.getAllStores().get((feedbackObj.get("storeId").getAsInt()));
            chosenStore.addFeedback(feedbackObj.get("stars").getAsInt(),feedbackObj.get("message").getAsString(),userManager.getAllCustomers().get(userId));
            notifyfeedback(chosenStore.getOwnerId());
        });
        response.setStatus(200);
    }

    private void setOwnerOrders() throws IOException {
        newOrder.addOrdersToStores(zone);
    }

    private void getCustomerOrders(HttpServletResponse response, Integer userId, ServletOutputStream out) throws IOException {
        Gson gson = new Gson();
        JsonArray orderArray = new JsonArray();
        Customer customer = userManager.getAllCustomers().get(userId);
        customer.getOrders().stream().filter(order->order.getZoneName().equals(zone.getName())).forEach(order-> {
            try {
                orderArray.add(getOrderSum(response,out,order));
            } catch (IOException e){}
        });
        response.setStatus(200);
        out.println(gson.toJson(orderArray));
        out.flush();
    }

    private JsonObject getOrderSum(HttpServletResponse response, ServletOutputStream out,Order newOrder) throws IOException {
        Gson gson = new Gson();
        JsonObject mainObj = new JsonObject();
        JsonObject orderObj = new JsonObject();
        newOrder.calculateTotalPrice();
        orderObj.addProperty("total products price", newOrder.getPrice()+newOrder.getDiscountsPrice());
        orderObj.addProperty("total delivery price", newOrder.getDeliveryPrice());
        orderObj.addProperty("total order price", newOrder.getTotalPrice());
        mainObj.add("orderSum", buildTotalOrderSum(newOrder));
        mainObj.add("orderFinalSum",orderObj);
        return mainObj;
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
        if(productsToOrder.size() == 0 ){
            String errorMessage = "The cart is empty. Please add products first.";

            // stands for unauthorized as there is already such user with this name
            response.setStatus(401);
            response.getOutputStream().println(errorMessage);
        }
        if(request.getParameter("type").equals("dynamic")){
            storeProductsToOrder = zone.findOptimalOrder(productsToOrder);
            newOrder = zone.setNewOrder(customer,storeProductsToOrder,request.getParameter("date"),Integer.parseInt(request.getParameter("x")),Integer.parseInt(request.getParameter("y")));
        }
        else{
            storeProductsToOrder.put(Integer.parseInt(request.getParameter("store")), productsToOrder);
            newOrder = zone.setNewOrder(customer,storeProductsToOrder,request.getParameter("date"),Integer.parseInt(request.getParameter("x")),Integer.parseInt(request.getParameter("y")));
        }
        newOrder.calculatePrice(zone.getAllStores());
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
        mainObj.add("orderSum", buildOptimalOrder());
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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private JsonElement buildOptimalOrder() {
        JsonArray jsonArray = new JsonArray();
        storeProductsToOrder.forEach((storeId,productsAndAmount)->{

            JsonObject obj = new JsonObject();
            JsonArray products = new JsonArray();
            JsonObject storeDetailObj = new JsonObject();
            storeDetailObj.addProperty("store Serial",storeId);
            storeDetailObj.addProperty("location",zone.getAllStores().get(storeId).getStringLocation());
            storeDetailObj.addProperty("PPK",zone.getAllStores().get(storeId).getPPK());
            storeDetailObj.addProperty("distance",newOrder.getDistance(storeId));
            storeDetailObj.addProperty("delivery From Store",newOrder.getDeliveryPrices().get(storeId));
            storeDetailObj.addProperty("product types",newOrder.getStoreProducts().get(storeId).size());
            storeDetailObj.addProperty("total products price",newOrder.calculateStorePrice(zone.getAllStores().get(storeId)));
            productsAndAmount.forEach((productId,amount)->{
                JsonObject productsObj = new JsonObject();
                productsObj.addProperty(zone.getAllProducts().get(productId).getName(), amount);
                products.add(productsObj);
            });
            obj.addProperty("storeName", zone.getAllStores().get(storeId).getName());
            obj.add("storeDetails",storeDetailObj);
            jsonArray.add(obj);
        });
        return jsonArray;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private JsonElement buildTotalOrderSum(Order newOrder) {
        JsonArray jsonArray = new JsonArray();
        newOrder.getStoreProducts().forEach((storeId,productAndAmount)->{
            JsonObject storeObj = new JsonObject();
            JsonObject storeDetailObj = new JsonObject();
            JsonArray storeP = new JsonArray();
            JsonArray storeD = new JsonArray();
            storeObj.addProperty("store Name",zone.getAllStores().get(storeId).getName());
            storeDetailObj.addProperty("store Serial",storeId);
            storeDetailObj.addProperty("PPK",zone.getAllStores().get(storeId).getPPK());
            storeDetailObj.addProperty("distance",newOrder.getDistance(storeId));
            storeDetailObj.addProperty("delivery From Store",newOrder.getDeliveryPrices().get(storeId));

            productAndAmount.forEach((productId,amount)->{
                storeP.add(buildStoreSum(productId,amount,storeId, zone.getAllStores().get(storeId).getProductPrices().get(productId)));
            });
            if( newOrder.getDiscountsProducts().get(storeId) !=null ) {
                newOrder.getDiscountsProducts().get(storeId).forEach((productId, discountProducts) -> {
                    storeD.add(buildStoreSum(productId, discountProducts.getAmount(), storeId, discountProducts.getPrice()));

                });
            }
            storeObj.add("product",storeP);
            storeObj.add("discount",storeD);
            storeObj.add("details",storeDetailObj);
            jsonArray.add(storeObj);
        });

        return jsonArray;
    }

    private JsonElement buildStoreSum(Integer productId, Double amount,Integer storeId, int price) {
        Product product = zone.getAllProducts().get(productId);
        JsonObject productObj = new JsonObject();
        productObj.addProperty("Product",product.getName());
        productObj.addProperty("Id",productId);
        productObj.addProperty("Buying Method",product.getMethod().toString());
        productObj.addProperty("Amount",amount);
        productObj.addProperty("Price",price);
        productObj.addProperty("Total Price",price * amount);
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
