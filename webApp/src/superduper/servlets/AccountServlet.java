package superduper.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import course.java.sdm.engine.Engine;
import course.java.sdm.engine.UserManager;
import superduper.utils.ServletUtils;
import superduper.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        response.setContentType("application/json");
        Integer userIdFromSession = SessionUtils.getUserId(request);
        switch(userAction){
            case "getAccountAction":
                showAccountActions(response,out,userIdFromSession);
        }
        }

    private void showAccountActions(HttpServletResponse response, ServletOutputStream out, Integer userIdFromSession) throws IOException {
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
        ServletOutputStream out = response.getOutputStream();
        response.setContentType("application/json");
        Integer userIdFromSession = SessionUtils.getUserId(request);
        userManager.updateAccount(userIdFromSession,"deposit", Double.parseDouble(request.getParameter("amount")), request.getParameter("date"));

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
