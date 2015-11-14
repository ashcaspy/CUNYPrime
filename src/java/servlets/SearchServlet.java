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
                new MatchValuePair(ID.greaterThan, "0"), 
                //new TimeRange(10, 12), 
                //new TimeRange(11, 14), 
                null,
                null,
                null, 
                null,
                new int[] {1},
                Arrays.asList(new String[]{"CSCI"})
        );
              
        
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        
        
        String query1 = "select * into combined_section_table1 from sections1 left join courses1 \n" +
"on sections1.cdept = courses1.dept and sections1.cnbr = courses1.nbr;";
        String query2 = "alter table combined_section_table1 drop column dept";
        String query3 = "alter table combined_section_table1 drop column nbr;";
        String query4 = "select * from combined_section_table1;\n" +
"";
        String query5 = "drop table combined_section_table1;";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
         preparedStatement = conn.prepareStatement(query1);
         preparedStatement.execute();
         preparedStatement = conn.prepareStatement(query2);
         preparedStatement.execute();
         preparedStatement = conn.prepareStatement(query3);
         preparedStatement.execute();
         preparedStatement = conn.prepareStatement(query4);
         resultSet = preparedStatement.executeQuery();

         while(resultSet.next()) {
          response.getWriter().write("Dept~"+resultSet.getString("cdept") + "FIELD_END");
          response.getWriter().write("CNum~"+resultSet.getString("cnbr") + "FIELD_END");
          response.getWriter().write("Name~"+resultSet.getString("name") + "FIELD_END");
          response.getWriter().write("Comp~"+resultSet.getString("components") + "FIELD_END");
          response.getWriter().write("Req~"+resultSet.getString("requirements") + "FIELD_END");
          response.getWriter().write("Desc~"+resultSet.getString("description") + "FIELD_END");
          response.getWriter().write("SNum~"+resultSet.getString("sec") + "FIELD_END");
          response.getWriter().write("STime~"+resultSet.getString("starttime") + "FIELD_END");
          response.getWriter().write("ETime~"+resultSet.getString("endtime") + "FIELD_END");
          response.getWriter().write("Days~"+resultSet.getString("days") + "FIELD_END");
          response.getWriter().write("Room~"+resultSet.getString("room") + "FIELD_END");
          response.getWriter().write("Inst~"+resultSet.getString("instructor") + "FIELD_END");
          response.getWriter().write("Flag~"+resultSet.getString("open") + "FIELD_END");
          response.getWriter().write("Cr:~"+resultSet.getString("credits") + "ENTRY_END");
         }
         
         preparedStatement = conn.prepareStatement(query5);
         preparedStatement.execute();
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
