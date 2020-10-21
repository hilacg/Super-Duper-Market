package superduper.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import course.java.sdm.engine.Engine;
import course.java.sdm.engine.UserManager;
import course.java.sdm.engine.Zone;
import superduper.utils.ServletUtils;

import javax.servlet.ServletException;
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
        String userAction = request.getParameter("action");
        switch (userAction){
            case "loadXML":{
                response.setContentType("text/plain;charset=UTF-8");
                try {
  //                  InputStream xmlConnection = request.getPart("xmlFile").getInputStream();
                    engine.loadXML(request.getPart("file").getInputStream(),(int)request.getSession(false).getAttribute("userId"));
                    response.setStatus(200);
                    response.getOutputStream().println("File loaded successfully!");
                    break;
                }catch(Exception e){
                    response.setStatus(401);
                    response.getOutputStream().println(e.getMessage());
                }
            }
            case "getZones":{
                getZones(response);
            }
        }


    }

    private void getZones(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
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
        response.getOutputStream().println(gson.toJson(mainObj));
        response.setStatus(200);
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
