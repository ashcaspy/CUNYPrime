/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import connections.MainConfig;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Ashley
 */
@WebServlet(name = "GetTermsAndDeptsServlet", urlPatterns = {"/gettermanddeptlist"})
public class GetTermsAndDeptsServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet GetTermsAndDeptsServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetTermsAndDeptsServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
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
        //processRequest(request, response);
        if (request.getParameter("college_value") != "none")
        {
            Connection conn = null;
            try {
                conn = MainConfig.getConnection();
            } catch (SQLException ex) {
                Logger.getLogger(GetCollegesServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (URISyntaxException ex) {
                Logger.getLogger(GetCollegesServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GetCollegesServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");

            String query1 = "select dept from depts_" + request.getParameter("college_value");
            String query2 = "select term from terms_" + request.getParameter("college_value");
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            try {
                preparedStatement = conn.prepareStatement(query1);
                resultSet = preparedStatement.executeQuery();
                while(resultSet.next()) {
                    response.getWriter().write("<option value = \""+ resultSet.getString("dept") +"\">"+resultSet.getString("dept") + "</option>");
                }
                response.getWriter().write("SPLIT");
                preparedStatement = conn.prepareStatement(query2);
                resultSet = preparedStatement.executeQuery();
                while(resultSet.next()) {
                    response.getWriter().write("<option value = \""+ resultSet.getString("term") +"\">"+resultSet.getString("term") + "</option>");
                }
            }
            catch (SQLException e) {
             e.printStackTrace();
            }
            
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
