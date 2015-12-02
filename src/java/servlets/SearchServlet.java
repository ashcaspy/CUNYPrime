/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import search.cunyfirst.TimeRange;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Integer;
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
import scheduling.*;

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
        processRequest(request, response);

    }


    protected void closeTimeMatchAction (ResultSet res, PreparedStatement update, Day day, int closeTimeIndex){
        if(!day.isCloseTimesEmpty()){
            while (closeTimeIndex < day.getCloseTimeSize()){
                try{
                    if(day.getClosedTimeElement(closeTimeIndex) == res.getInt("starttime")){
                        update.execute();
                    } else if(day.getClosedTimeElement(closeTimeIndex) == res.getInt("endtime")){
                        update.execute();
                    } else if(day.getClosedTimeElement(closeTimeIndex) > res.getInt("starttime") && day.getClosedTimeElement(closeTimeIndex) < res.getInt("endtime")){
                        update.execute();
                    }
                    closeTimeIndex++;
                } catch (SQLException e){
                    e.printStackTrace();

                }

            }
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
        //processRequest(request, response);

        // CALL KAT'S FUNCTION HERE
        Connection conn = null;
        System.out.println("OKAY HERE1");
        try {
            conn = ClassSearcher.classSearch();
        } catch (URISyntaxException ex) {
            Logger.getLogger(SearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        String college = request.getParameter("college_value").toLowerCase();
        String term = request.getParameter("term_value");
        String dept = request.getParameter("dept_value");
        String course_num = request.getParameter("course_num_value");
        String keyword = request.getParameter("keyword_value");
        String prof = request.getParameter("prof_value");

        System.out.println(college);
        // use the original term since ID.semester() reconstructs the same thing
        /*
        int year = Integer.parseInt(term.substring(0, 4));
        term = term.split(" ")[1];
        */
        System.out.println(term);

        // by default, select all courses that meet the other criteria
        MatchValuePair mvpair;
        if ("".equals(course_num)) {
            mvpair = new MatchValuePair(ID.greaterThan, "0");
        }
        else {
            mvpair = new MatchValuePair(ID.contains, course_num);
        }

        if ("".equals(keyword))
            keyword = null;
        if ("".equals(prof))
            prof = null;


        System.out.println("OKAY HERE2");
        Search searcher = new Search(conn, 1);
        searcher.selectTerm(college.toUpperCase(), term);
        searcher.find(

                mvpair,
                //new TimeRange(10, 12),
                //new TimeRange(11, 14),
                null, null,
                keyword,
                prof,
                new int[] {},
                Arrays.asList(new String[]{dept})
        );
        searcher.find(
                new MatchValuePair(ID.greaterThan, "0"),
                10,
                3,
                null,
                null,
                new int[] {3},
                Arrays.asList(new String[]{"CSCI", "ANTHC"})
        );

        //Test values for available and unavailable times.
        String testArr [] = {"timeslot-div- 1 - 09", "timeslot-div-1-11", "timeslot-div- 1 - 10","timeslot-div-1-12", "timeslot-div-1-14","timeslot-div-1-15", "timeslot-div-2-12", "timeslot-div-2-13"};
        String testArr2 []= {"timeslot-div-1-13", "timeslot-div-2-10", "timeslot-div-1-14"};
        Schedule schedule = new Schedule();
        schedule.setOpenTimes(testArr);
        schedule.setCloseTimes(testArr2);
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        int closeTimeIndex = 0;


        String queryA = "select * from "+ searcher.tableName();


        try {
            String days = "";
            preparedStatement = conn.prepareStatement(queryA);
            preparedStatement.execute();
            resultSet = preparedStatement.executeQuery();
            
            String[] WEEK = new String[] {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
            PreparedStatement update = conn.prepareStatement("UPDATE "+searcher.tableName()+" "
                    + "SET points=? WHERE cdept=? AND cnbr=? AND sec=?");

            while (resultSet.next()){
                update.setInt(1, resultSet.getInt("points")+2);
                update.setString(2, resultSet.getString("cdept"));
                update.setString(3, resultSet.getString("cnbr"));
                update.setString(4, resultSet.getString("sec"));
                
                days = resultSet.getString("days");

                for(int i=0; i<WEEK.length; ++i) {
                    if(days.contains(WEEK[i])) {
                        Day temp = schedule.getElementFromSchedule(i);
                        closeTimeMatchAction(resultSet, update, temp, closeTimeIndex);
                    }
                }
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }



        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        college = college.toLowerCase();

        String query1 =
                "select * into combined_section_table1 "
                        + "from " + searcher.tableName() + " left join college_courses" + college +
                        " on " + searcher.tableName() +".cdept = college_courses" + college + ".dept and "
                        + searcher.tableName() + ".cnbr = college_courses" +
                        college + ".nbr;";
        System.out.println(query1);
        String query2 = "alter table combined_section_table1 drop column dept";
        String query3 = "alter table combined_section_table1 drop column nbr";
        String query4 = "select * from combined_section_table1";
        String query5 = "drop table combined_section_table1;";
        String query6 = "drop table " + searcher.tableName();
        //String query1 = "select * from sections1_1";
        //PreparedStatement preparedStatement;
        //ResultSet resultSet;
        System.out.println("OKAY HERE3");

        try {

            preparedStatement = conn.prepareStatement(query1);
            preparedStatement.execute();
            preparedStatement = conn.prepareStatement(query2);
            preparedStatement.execute();
            preparedStatement = conn.prepareStatement(query3);
            preparedStatement.execute();


            preparedStatement = conn.prepareStatement(query4);
            resultSet = preparedStatement.executeQuery();

            System.out.println("OKAY HERE4");
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
                response.getWriter().write("Cr~"+resultSet.getString("credits") + "FIELD_END" + "ENTRY_END");
            }
            System.out.println("OKAY HERE5");
            preparedStatement = conn.prepareStatement(query5);
            preparedStatement.execute();
            preparedStatement = conn.prepareStatement(query6);
            preparedStatement.execute();

        }
        catch (SQLException e) {

            e.printStackTrace();
        }

        System.out.println(response);



        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
