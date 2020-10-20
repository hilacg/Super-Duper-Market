package superduper.servlets;

import course.java.sdm.engine.Engine;
import course.java.sdm.engine.UserManager;
import superduper.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class AreaServlet  extends HttpServlet {
    private Engine engine;
    private UserManager userManager;
    private final static String XML_PATH = "C:/Users/Hila/Desktop/Java/Intelij stub project/webApp/web/common/resources/EX 3/ex3-small.xml";

    @Override
    public void init() throws ServletException {
        super.init();
        engine = ServletUtils.getEngine(getServletContext());
        userManager = engine.getUserManager();
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        String userAction = request.getParameter("action");
        switch (userAction){
            case "loadXML":{
                try {
  //                  InputStream xmlConnection = request.getPart("xmlFile").getInputStream();
                    engine.loadXML(XML_PATH,(int)request.getSession(false).getAttribute("userId"));
                    response.setStatus(200);
                    response.getOutputStream().println("file loaded successfully!");
                    break;
                }catch(Exception e){
                    response.setStatus(401);
                    response.getOutputStream().println(e.getMessage());
                }
            }
        }


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
