package superduper.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
import java.util.List;
import java.util.Stack;

public class AccountServlet  extends HttpServlet {

    private UserManager userManager;
    private Engine engine;


    @Override
    public void init() throws ServletException {
        super.init();
        engine = ServletUtils.getEngine(getServletContext());
        userManager = engine.getUserManager();
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userAction = request.getParameter("action");
        ServletOutputStream out = response.getOutputStream();
        Integer userIdFromSession = SessionUtils.getUserId(request);
        switch (userAction) {
            case "deposit": {
                deposit(response, request, out, userIdFromSession);
                break;
            }
            case "getAccountAction": {
                showAccountActions(response, out, userIdFromSession);
                break;
            }
            case "getAccountBalance": {
                response.setContentType("text/plain;charset=UTF-8");
                response.setStatus(200);
                out.println(userManager.getUserBalance(userIdFromSession));
                out.flush();
                break;
            }
            case "charge": {
                chargeOrder(response,request,out, userIdFromSession);
                break;
            }
            case "getNotifications":{
                getNotifications(response,out,userIdFromSession);
            }

        }
    }

    private void getNotifications(HttpServletResponse response, ServletOutputStream out, Integer userIdFromSession) throws IOException {
        Gson gson = new Gson();
        StoreOwner owner = userManager.getAllStoreOwners().get(userIdFromSession);
        Stack<Notification> notificationList = owner.getNotification();
        out.println(notificationList.size() > 0 ? gson.toJson( notificationList.pop()) : null);
        out.flush();
        response.setStatus(200);
    }

    private void chargeOrder(HttpServletResponse response, HttpServletRequest request, ServletOutputStream out, Integer userIdFromSession) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        Order order = userManager.getAllCustomers().get(userIdFromSession).getOrders().get(userManager.getAllCustomers().get(userIdFromSession).getOrders().size()-1);
        Zone zone = userManager.getZone(Integer.parseInt(request.getParameter("owner")),request.getParameter("zoneName"));
        userManager.updateAccount(userIdFromSession,"withdraw", order.getTotalPrice(), order.getDate());
        order.getStoreProducts().forEach((storeId,prouductAndAmount)->{
            Double amountToPay = 0.0;
            amountToPay+= order.calculateStorePrice(zone.getAllStores().get(storeId));
            amountToPay+= order.getDeliveryPrices().get(storeId);
            amountToPay+=order.calculateStoreDiscount(storeId);
            userManager.updateAccount(zone.getAllStores().get(storeId).getOwnerId(),"deposit", amountToPay, order.getDate());
        });
        response.setStatus(200);
        out.println(order.getTotalPrice());
        out.flush();
    }

    private void deposit(HttpServletResponse response, HttpServletRequest request, ServletOutputStream out, Integer userIdFromSession) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        try{
            double amount = Double.parseDouble(request.getParameter("amount"));
            String date =  request.getParameter("date");
            userManager.updateAccount(userIdFromSession,"deposit", amount, date);
            response.setStatus(200);
            out.flush();
        }
        catch (NumberFormatException e){
            response.setStatus(401);
            response.getOutputStream().println("Amount must be a number");
            out.flush();
        }
        catch (NullPointerException e){
            response.setStatus(402);
            response.getOutputStream().println("All fields must be filled");
            out.flush();
        }

    }

    private void showAccountActions(HttpServletResponse response, ServletOutputStream out, Integer userIdFromSession) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        JsonArray actionJason = new JsonArray();
        JsonObject mainObj = new JsonObject();
        userManager.getUserActions(userIdFromSession).forEach(action -> {
                JsonObject actionObj = new JsonObject();
            actionObj.addProperty("Type", action.getType());
            actionObj.addProperty("Date",action.getDate());
            actionObj.addProperty("Amount", action.getAmount());
            actionObj.addProperty("Previous Balance", action.getPrevBalance());
            actionObj.addProperty("Current Balance", action.getCurrBalance());
            actionJason.add(actionObj);
        });
        mainObj.add("actions",actionJason);
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
