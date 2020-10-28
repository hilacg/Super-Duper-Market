package superduper.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import course.java.sdm.engine.*;
import superduper.utils.ServletUtils;
import superduper.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        }

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
        Gson gson = new Gson();
        JsonObject mainObj = new JsonObject();
        response.setStatus(200);
        mainObj.add("discounts", gson.toJsonTree(discounts));
        mainObj.add("orderSum", gson.toJsonTree(storeProductsToOrder));
        out.println( gson.toJson(mainObj));
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
