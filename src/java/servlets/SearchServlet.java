/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import search.cunyfirst.TimeRange;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import search.ClassSearcher;
import search.Search;
import search.cunyfirst.ID;
import search.cunyfirst.MatchValuePair;


/**
 *
 * @author Ashley
 */
@WebServlet(name = "SearchServlet", urlPatterns = {"/performclasssearch"})
public class SearchServlet extends HttpServlet {

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
            out.println("<title>Servlet SearchServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SearchServlet at " + request.getContextPath() + "</h1>");
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
        //processRequest(request, response);
        
        // CALL KAT'S FUNCTION HERE
        Connection conn = null;
        try {
            conn = ClassSearcher.classSearch();
        } catch (URISyntaxException ex) {
            Logger.getLogger(SearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        Search searcher = new Search(conn);
        searcher.selectTerm("Hunter College", ID.semester("Fall", 2015));
        searcher.find(
                new MatchValuePair(ID.greaterThan, "0"), new TimeRange(10, 12), new TimeRange(11, 14), null, null,
                new int[] {3},
                Arrays.asList(new String[]{"CSCI", "ANTHC"}));
       /* searcher.find(
                null, new TimeRange(12, 13), new TimeRange(13,15), null, null, new int[]{1},
                Arrays.asList(new String[]{"CSCI", "ENGL", "CHIN"})
        );
        */
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        response.getWriter().write("ok<br />");
        
        
        String query1 = "select * from courses1";
        String query2 = "select * from sections1";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
         preparedStatement = conn.prepareStatement(query1);
         resultSet = preparedStatement.executeQuery();

         response.getWriter().write("<table cellspacing='0' cellpadding='5' border='1'>");
         response.getWriter().write("<tr>");
         response.getWriter().write("<td><b>Department</b></td>");
         response.getWriter().write("<td><b>Course Number</b></td>");
         response.getWriter().write("<td><b>Name</b></td>");
         response.getWriter().write("<td><b>Components</b></td>");
         response.getWriter().write("<td><b>Requirements</b></td>");
         response.getWriter().write("<td><b>Description</b></td>");
         response.getWriter().write("<td><b>Credits</b></td>");
         
         response.getWriter().write("</tr>");

         while(resultSet.next()) {
          response.getWriter().write("<tr>");
          response.getWriter().write("<td>"+resultSet.getString(1) + "</td>");
          response.getWriter().write("<td>"+resultSet.getString(2) + "</td>");
          response.getWriter().write("<td>"+resultSet.getString(3) + "</td>");
          response.getWriter().write("<td>"+resultSet.getString(4) + "</td>");
          response.getWriter().write("<td>"+resultSet.getString(5) + "</td>");
          response.getWriter().write("<td>"+resultSet.getString(6) + "</td>");
          response.getWriter().write("<td>"+resultSet.getString(7) + "</td>");
          response.getWriter().write("</tr>");

         }

         response.getWriter().write("</table><br />");
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
