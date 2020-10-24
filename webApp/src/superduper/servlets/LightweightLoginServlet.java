package superduper.servlets;

import superduper.utils.ServletUtils;
import superduper.utils.SessionUtils;
import com.google.gson.JsonObject;
import course.java.sdm.engine.Engine;
import course.java.sdm.engine.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LightweightLoginServlet extends HttpServlet {

    private Engine engine;
    private UserManager userManager;
    private static int userId = 0;

    @Override
    public void init() throws ServletException {
        super.init();
        engine = ServletUtils.getEngine(getServletContext());
        userManager = engine.getUserManager();
    }

    // urls that starts with forward slash '/' are considered absolute
    // urls that doesn't start with forward slash '/' are considered relative to the place where this servlet request comes from
    // you can use absolute paths, but then you need to build them from scratch, starting from the context path
    // ( can be fetched from request.getContextPath() ) and then the 'absolute' path from it.
    // Each method with it's pros and cons...
    private final String ZONES_CENTER_URL = "pages/zonesCenter/zonesCenter.html";
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userAction = request.getParameter("action");


        Integer userIdFromSession = SessionUtils.getUserId(request);

        switch (userAction){
            case "login": {
                response.setContentType("text/plain;charset=UTF-8");
                login(request,response,userIdFromSession,userManager);
                break;
            }
            case "getUser":{
                response.setContentType("application/json");
                JsonObject json = new JsonObject();
                json.addProperty("name",userManager.getUserName(userIdFromSession));
                json.addProperty("isCustomer",userManager.isCustomer(userIdFromSession));
                json.addProperty("id",userIdFromSession);
                response.setStatus(200);
                response.getOutputStream().print(json.toString());
                break;
            }
            case "getUserType": {
                response.setStatus(200);
                response.getOutputStream().println(userManager.isCustomer(userIdFromSession));
                break;
            }
            case "getUserName": {
                response.setStatus(200);
                response.getOutputStream().println(userManager.getUserName(userIdFromSession));
                break;
            }
        }

    }

    private void getUser(HttpServletRequest request, HttpServletResponse response, String usernameFromSession, UserManager userManager) {
    }

    private void login(HttpServletRequest request, HttpServletResponse response, Integer userIdFromSession, UserManager userManager) throws IOException {
        if (userIdFromSession == null) { //user is not logged in yet
            String usernameFromParameter = request.getParameter("userName");
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                //no username in session and no username in parameter - not standard situation. it's a conflict

                // stands for conflict in server state
                response.setStatus(409);

                // returns answer to the browser to go back to the sign up URL page
                response.getOutputStream().println("Please enter user name");
            } else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();

                /*
                One can ask why not enclose all the synchronizations inside the userManager object ?
                Well, the atomic action we need to perform here includes both the question (isUserExists) and (potentially) the insertion
                of a new user (addUser). These two actions needs to be considered atomic, and synchronizing only each one of them, solely, is not enough.
                (of course there are other more sophisticated and performable means for that (atomic objects etc) but these are not in our scope)

                The synchronized is on this instance (the servlet).
                As the servlet is singleton - it is promised that all threads will be synchronized on the very same instance (crucial here)

                A better code would be to perform only as little and as necessary things we need here inside the synchronized block and avoid
                do here other not related actions (such as request dispatcher\redirection etc. this is shown here in that manner just to stress this issue
                 */
                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";

                        // stands for unauthorized as there is already such user with this name
                        response.setStatus(401);
                        response.getOutputStream().println(errorMessage);
                    }
                    else {
                        //add the new user to the users list
                        userManager.addUser(++userId,usernameFromParameter,Boolean.parseBoolean(request.getParameter("isCustomer")));
  //                      engine.addUser(usernameFromParameter,Boolean.parseBoolean(request.getParameter("isCustomer")));
                        //set the username in a session so it will be available on each request
                        //the true parameter means that if a session object does not exists yet
                        //create a new one
                        request.getSession(true).setAttribute("userId", userId);
                        //redirect the request to the chat room - in order to actually change the URL
                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        response.setStatus(200);
                        response.getOutputStream().println(ZONES_CENTER_URL);
                    }
                }
            }
        } else {
            //user is already logged in
            response.setStatus(200);
            response.getOutputStream().println(ZONES_CENTER_URL);
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
