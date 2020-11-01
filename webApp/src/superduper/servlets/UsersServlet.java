package superduper.servlets;

import superduper.utils.ServletUtils;
import com.google.gson.Gson;
import course.java.sdm.engine.Customer;
import course.java.sdm.engine.StoreOwner;
import course.java.sdm.engine.UserManager;
import superduper.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UsersServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String userAction = request.getParameter("action");
        Integer userIdFromSession = SessionUtils.getUserId(request);
        PrintWriter out = response.getWriter();
        switch (userAction){
            case("getUsersList"):{
                getUsersList(out);
                break;
            }
        }

    }

    private void getUsersList(PrintWriter out ) {
        //returning JSON objects, not HTML
            Gson gson = new Gson();
            UserManager userManager = ServletUtils.getEngine(getServletContext()).getUserManager();
            Map<String,String> usersList = new HashMap<>();
            usersList.putAll(userManager.getAllCustomers().values().stream().collect( Collectors.toMap(Customer::getName,(customer)->"customer")));
            usersList.putAll(userManager.getAllStoreOwners().values().stream().collect( Collectors.toMap(StoreOwner::getName,(owner)->"store owner")));
            String json = gson.toJson(usersList);
            out.println(json);
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
