/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
import org.json.*;
import search.parser.Section;

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


    protected float closeTimeMatchAction (ResultSet res, PreparedStatement update, Day day, float value, boolean isTimeBased){
        int closeTimeIndex = 0;
        
        if(!day.isCloseTimesEmpty()){
            try {
            while (closeTimeIndex < day.getCloseTimeSize()){
                
                    if((day.getClosedTimeElement(closeTimeIndex).Y() * 100 >= res.getInt("endtime") && 
                            day.getClosedTimeElement(closeTimeIndex).X() * 100  <= res.getInt("endtime")) ||
                            (day.getClosedTimeElement(closeTimeIndex).Y() * 100  >= res.getInt("starttime") &&
                                    day.getClosedTimeElement(closeTimeIndex).X() * 100 <= res.getInt("starttime"))){
                        if(isTimeBased){
                            value += 2.0;
                        } else {
                            value += 1.0;
                        }
                    }
                    closeTimeIndex++;
            }

                    
        
            } catch(SQLException e){
                e.printStackTrace();
            }
        }
            return value;

    }
    
    protected void createPtBasedTables(Connection conn, Search searcher, int id_num){
        try {
            Section.createTable(conn, "best_fit_" + id_num);
            Section.createTable(conn, "some_conflicts_" + id_num);
            Section.createTable(conn, "others_" + id_num);
            PreparedStatement myStatement = myStatement = conn.prepareStatement("insert into best_fit_" + id_num + " select * from " + searcher.tableName() + " where points < 1");
            myStatement.execute();
            myStatement = conn.prepareStatement("insert into some_conflicts_" + id_num + " select * from " + searcher.tableName() + " where points >= 1 and points < 2");
            myStatement.execute();
            myStatement = conn.prepareStatement("insert into others_" + id_num + " select * from " + searcher.tableName() + " where points >=2 and points < 3");
            myStatement.execute();
            
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    
    /**
     * A function that adds 0.5 points to all results from a search that are not in an open time slot range and not in a closed time range. 
     * @param conn, A Connection instance.
     * @param res, The query results.
     * @param schedule, A Schedule instance containing available and unavailable time slots for each day of the week.
     * @param searcher, An instance of Search.
     * @param value, A float containing the current points the result has, so 0.5 can be added to it if applicable.  
     * @return 
     */
    protected float addHalfAPt(Connection conn, ResultSet res, Schedule schedule, Search searcher, float value){
        try {
            
            int openTimesIndex = 0;
            int closeTimesIndex = 0;
            boolean hasMatch = false;
            String days = res.getString("days");
            if(days != null){
                for (int i = 0; i < schedule.getWeek().length;i++){
                    if(days.contains(schedule.getWeek()[i])){
                        hasMatch = false;


                        Day day = schedule.getElementFromSchedule(i);
                        while(openTimesIndex < day.getOpenTimeSize() && 
                            closeTimesIndex < day.getCloseTimeSize()){
                            if(((day.getOpenTimeElement(openTimesIndex).Y() * 100 >= res.getInt("endtime") && 
                                    day.getOpenTimeElement(openTimesIndex).X() * 100  <= res.getInt("endtime")) ||
                                    (day.getOpenTimeElement(openTimesIndex).Y() * 100  >= res.getInt("starttime") &&
                                            day.getOpenTimeElement(openTimesIndex).X() * 100 <= res.getInt("starttime"))) || ((day.getClosedTimeElement(closeTimesIndex).Y() * 100 >= res.getInt("endtime") && 
                                    day.getClosedTimeElement(closeTimesIndex).X() * 100  <= res.getInt("endtime")) ||
                                    (day.getClosedTimeElement(closeTimesIndex).Y() * 100  >= res.getInt("starttime") &&
                                            day.getClosedTimeElement(closeTimesIndex).X() * 100 <= res.getInt("starttime")))){

                                hasMatch = true;
                                break;
                            }
                            openTimesIndex++;
                            closeTimesIndex++;
                        }

                        while (openTimesIndex <day.getOpenTimeSize()){
                            if((day.getOpenTimeElement(openTimesIndex).Y() * 100 >= res.getInt("endtime") && 
                                        day.getOpenTimeElement(openTimesIndex).X() * 100  <= res.getInt("endtime")) ||
                                        (day.getOpenTimeElement(openTimesIndex).Y() * 100  >= res.getInt("starttime") &&
                                                day.getOpenTimeElement(openTimesIndex).X() * 100 <= res.getInt("starttime"))){

                                    hasMatch = true;
                                    break;
                            }
                            openTimesIndex++;
                        }

                        while (closeTimesIndex < day.getCloseTimeSize()){
                                if ((day.getClosedTimeElement(closeTimesIndex).Y() * 100 >= res.getInt("endtime") && 
                                        day.getClosedTimeElement(closeTimesIndex).X() * 100  <= res.getInt("endtime")) ||
                                        (day.getClosedTimeElement(closeTimesIndex).Y() * 100  >= res.getInt("starttime") &&
                                                day.getClosedTimeElement(closeTimesIndex).X() * 100 <= res.getInt("starttime"))){

                                    hasMatch = true;
                                    break;
                                }
                            closeTimesIndex++;
                        }

                        if(!hasMatch){

                            value += 0.5;
                        }


                    }
                }
            }
           
        } catch(SQLException e){
            e.printStackTrace();
        }
        return value;
        
    }
    
    /**
     * A function that assigns points to the result of a search based on its start time and end time, and if the result fulfills a requirement.
     * @param conn, An instance of Connection.
     * @param reqs, A HashMap containing requirements needed to be fulfilled for a degree. 
     * @param schedule, A Schedule instance containing available and unavailable time slots for each day of the week.
     * @param searcher, An instance of Search.
     * @param isTimeBased, A boolean indicating what type of search (time focused or requirement focused search) so the proper point system can be used. 
     */
    protected void assignPts(Connection conn, HashMap<String, List<JSONObject>> reqs, Schedule schedule, Search searcher, boolean isTimeBased){
        
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            float value = 0; 
            String queryA = "select * from "+ searcher.tableName();

            try {

                String days;
                preparedStatement = conn.prepareStatement(queryA);
                preparedStatement.execute();
                resultSet = preparedStatement.executeQuery();
                PreparedStatement update = conn.prepareStatement("UPDATE "+searcher.tableName()+" "
                        + "SET points=? WHERE cdept=? AND cnbr=? AND sec=? AND starttime=? AND endtime=?");

                while (resultSet.next()){
                    value = 0;
                    update.setString(2, resultSet.getString("cdept"));
                    update.setString(3, resultSet.getString("cnbr"));
                    update.setString(4, resultSet.getString("sec"));
                    update.setInt(5, resultSet.getInt("starttime"));
                    update.setInt(6, resultSet.getInt("endtime"));

                    days = resultSet.getString("days");
                    
                    
                    for(int i=0; i<schedule.getWeek().length; ++i) {
                        if (days != null){
                            if(days.contains(schedule.getWeek()[i])) {
                                Day temp = schedule.getElementFromSchedule(i);
                               
                                value = closeTimeMatchAction(resultSet, update, temp, value, isTimeBased);
                                
                                
                            }
                        }
                    }
                  
                    
                    String cnbr = resultSet.getString("cnbr");
                    String cdept = resultSet.getString("cdept");
                    String start = resultSet.getString("starttime");
                  
                    value = addHalfAPt(conn, resultSet, schedule, searcher, value);
                   
                    if(isTimeBased){
                        boolean hasMatch = false;
                        List<JSONObject> courses = reqs.get(cdept);
                        if(null != courses) {
                            for (JSONObject obj : courses) {
                                try {
                                    // check if cnum is null
                                    String cnum;
                                    if(JSONObject.NULL == obj.get("cnum")){
                                        cnum = null;
                                    } else {
                                        cnum = Integer.toString(obj.getInt("cnum"));
                                    }
                                    String dept = obj.getString("dept");
                                    if(null == cnum || cnbr.startsWith(cnum)) {
                                            hasMatch = true;
                                            break;

                                    } 
                                } catch(JSONException e){
                                   e.printStackTrace();
                                }

                               
                            } // end for courses
                          
                        } // and if courses!=null
                        if(!hasMatch && !reqs.isEmpty()){
                                value += 1;  
                                update.setFloat(1, value);
                                update.execute();
                        } else{
                           
                            update.setFloat(1, value);
                            update.execute();
                        }
                    } else {
                         update.setFloat(1, value);
                         update.execute();
                    }// end if(isTimeBased)
                } // end while(rs.next())
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }
    

    /**
     * 
     * 
     */
    protected JSONObject[] parseJSON(String input) {
        JSONObject[] result = null;
        try {
            JSONArray array = new JSONArray(input);
            result = new JSONObject[array.length()];
            for(int i=0; i<array.length(); ++i) {
                result[i] = new JSONObject(array.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace(); 
        }
        return result;
    }
    
    protected HashMap<String, List<JSONObject>> sort_reqs(JSONObject[] reqs) {
        HashMap<String, List<JSONObject>> reqs_by_dept = new HashMap<>(); 
        for(JSONObject obj : reqs) {
            try {
                String req_dept = obj.getString("dept");
                if(!reqs_by_dept.containsKey(req_dept)) {
                    reqs_by_dept.put(req_dept, new ArrayList<>());
                }
                reqs_by_dept.get(req_dept).add(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return reqs_by_dept;
    }

    
    protected void putTimeSlotsInSched(Schedule schedule, HttpServletRequest request){
        try {
                JSONArray arr=  new JSONArray(request.getParameter("sched_open"));


                String openTimesArr [] = new String[arr.length()];
                for (int i =0; i < arr.length(); i++){
                    openTimesArr[i] = arr.getString(i);
                }
                
                
                
                schedule.setTimes(openTimesArr, true);
                
                arr = new JSONArray(request.getParameter("sched_closed"));
                String closedTimesArr[] = new String[arr.length()];
                  for (int i =0; i < arr.length(); i++){
                    closedTimesArr[i] = arr.getString(i);
                }
                schedule.setTimes(closedTimesArr, false);
            
            } catch (JSONException e){
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

        /**********************************************************************/
        // Initializing connection and search regardless of search type (may move)
        /**********************************************************************/
        Connection conn = null;
        try {
            conn = ClassSearcher.classSearch();
        } catch (URISyntaxException ex) {
            Logger.getLogger(SearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        Search searcher;

        /**********************************************************************/
        // getting parameters here regardless of search type
        /**********************************************************************/

        String college = request.getParameter("college_value").toLowerCase();
        String term = request.getParameter("term_value");
        String dept = request.getParameter("dept_value");
        String course_num = request.getParameter("course_num_value");
        String keyword = request.getParameter("keyword_value");
        String prof = request.getParameter("prof_value");
        int id_num = Integer.parseInt(request.getParameter("id_num"));
        
        MatchValuePair mvpair = null;
        if (!"".equals(course_num)) {
            mvpair = new MatchValuePair(ID.contains, course_num);
        }

        if ("".equals(keyword))
            keyword = null;
        if ("".equals(prof))
            prof = null;
        
        boolean[] findErrors = new boolean[] {false, false, false};
        
        System.out.println(request.getParameter("search_type"));

        if (request.getParameter("search_type").equals("DEFAULT_SEARCH")){

            searcher = Search.createSearch(conn, id_num, college.toUpperCase(), term);
            
            searcher.find(
                    mvpair,
                    null, null,
                    keyword,
                    prof,
                    new int[] {},
                    ("none".equalsIgnoreCase(dept)) ? null : Arrays.asList(new String[]{dept}),
                    findErrors
            );
            /******************************************************************/
            // Response parsing -- leave this to Ashley unless you're testing
            //  If you're testing, remember to change the table names where relevant
            //      ex: bestfit, someconflicts, etc
            /******************************************************************/

            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            college = college.toLowerCase();
            
            String query1 =
                    "select * into combined_section_table_" + id_num
                            + " from " + searcher.tableName() + " left join college_courses" + college +
                            " on " + searcher.tableName() +".cdept = college_courses" + college + ".dept and "
                            + searcher.tableName() + ".cnbr = college_courses" +
                            college + ".nbr;";
            
            String query2 = "alter table combined_section_table_" + id_num + " drop column dept";
            String query3 = "alter table combined_section_table_" + id_num + " drop column nbr";
            String query4 = "select * from combined_section_table_" + id_num;
            String query5 = "drop table combined_section_table_" + id_num;
            String query6 = "drop table " + searcher.tableName();

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
                response.getWriter().write("ERRORS_BEGIN" + findErrors[0] + "," + findErrors[1]+ "," + findErrors[2] + "ERRORS_END");
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
                preparedStatement = conn.prepareStatement(query5);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query6);
                preparedStatement.execute();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        /**********************************************************************/
        // Time focused search
        /**********************************************************************/
        else if (request.getParameter("search_type").equals("TIME_FOCUSED_SEARCH")){

            /**********************************************************************/
            // parsing of parameters specific to this search goes here
            /**********************************************************************/
            
            Schedule schedule = new Schedule();
            putTimeSlotsInSched(schedule, request);
             
            
            /**********************************************************************/
            // Finds go here
            /**********************************************************************/
            searcher = Search.createSearch(conn, id_num, college.toUpperCase(), term);
            
            /* since time-focused searches cannot be run 
             *  without selecting some open block,
             *  find() is guaranteed to be called at least once
             *  and create the table
             */
            Day day;
            Pair time;
            for(int i=0; i < schedule.getSize(); ++i) {
                day = schedule.getElementFromSchedule(i);
                for(int t = 0; t < day.getOpenTimeSize(); ++t) {
                    time = day.getOpenTimeElement(t);
                    searcher.find(null, time.X(), time.Y(), null, null, 
                            new int[] {day.getDay()}, null, findErrors);
                }
            }
            
            /**********************************************************************/
            // Sorting goes here
            /**********************************************************************/

            
            assignPts(conn, sort_reqs(parseJSON(request.getParameter("reqs"))), 
                    schedule, searcher, true);
            createPtBasedTables(conn, searcher, id_num);
            
            
            /**********************************************************************/
            // Response parsing
            //  uncomment this when search/sort is functional
            /**********************************************************************/
            
            
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            college = college.toLowerCase();
            
            String queryBF;
            queryBF =
                    "select * into combined_section_table_" + id_num
                            + " from " + "best_fit_" + id_num + " left join college_courses" + college +
                            " on " + "best_fit_" + id_num +".cdept = college_courses" + college + ".dept and "
                            + "best_fit_" + id_num + ".cnbr = college_courses" +
                            college + ".nbr;";
            
            String querySC;
            querySC =
                    "select * into combined_section_table_" + id_num
                            + " from " + "some_conflicts_" + id_num + " left join college_courses" + college +
                            " on " + "some_conflicts_" + id_num +".cdept = college_courses" + college + ".dept and "
                            + "some_conflicts_" + id_num + ".cnbr = college_courses" +
                            college + ".nbr;";
            
            String queryO;
            queryO =
                    "select * into combined_section_table_" + id_num
                            + " from " + "others_" + id_num + " left join college_courses" + college +
                            " on " + "others_" + id_num +".cdept = college_courses" + college + ".dept and "
                            + "others_" + id_num + ".cnbr = college_courses" +
                            college + ".nbr;";
            
            String query1 = "alter table combined_section_table_" + id_num + " drop column dept";
            String query2 = "alter table combined_section_table_" + id_num + " drop column nbr";
            String query3 = "select * from combined_section_table_" + id_num;
            String query4 = "drop table combined_section_table_" + id_num;
            
            String queryBFDrop = "drop table " + "best_fit_" + id_num;
            String querySCDrop = "drop table " + "some_conflicts_" + id_num;
            String queryODrop = "drop table " + "others_" + id_num;
            String querySectionsDrop = "drop table " + searcher.tableName();

            PreparedStatement preparedStatement;
            ResultSet resultSet;

            try {
                // BEST FIT RESPONSE
                preparedStatement = conn.prepareStatement(queryBF);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query1);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query2);
                preparedStatement.execute();


                preparedStatement = conn.prepareStatement(query3);
                resultSet = preparedStatement.executeQuery();
                response.getWriter().write("ERRORS_BEGIN" + findErrors[0] + "," + findErrors[1]+ "," + findErrors[2] + "ERRORS_END");
                response.getWriter().write("BEST_FIT_START");
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
                response.getWriter().write("BEST_FIT_END");
                preparedStatement = conn.prepareStatement(query4);
                preparedStatement.execute();
                
                // SOME CONFLICTS RESPONSE
                preparedStatement = conn.prepareStatement(querySC);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query1);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query2);
                preparedStatement.execute();


                preparedStatement = conn.prepareStatement(query3);
                resultSet = preparedStatement.executeQuery();
                
                response.getWriter().write("SOME_CONFLICTS_START");
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
                response.getWriter().write("SOME_CONFLICTS_END");
                preparedStatement = conn.prepareStatement(query4);
                preparedStatement.execute();
                
                // OTHERS RESPONSE
                preparedStatement = conn.prepareStatement(queryO);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query1);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query2);
                preparedStatement.execute();


                preparedStatement = conn.prepareStatement(query3);
                resultSet = preparedStatement.executeQuery();
                
                response.getWriter().write("OTHERS_START");
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
                response.getWriter().write("OTHERS_END");
                preparedStatement = conn.prepareStatement(query4);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(queryODrop);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(queryBFDrop);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(querySCDrop);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(querySectionsDrop);
                preparedStatement.execute();
            }
            catch (SQLException e) {

                e.printStackTrace();
            }
            
        }

        /**********************************************************************/
        // Requirement focused search
        /**********************************************************************/
        else if (request.getParameter("search_type").equals("REQ_FOCUSED_SEARCH")){

            
            Schedule schedule = new Schedule();
            putTimeSlotsInSched(schedule, request);           
            
            /**********************************************************************/
            // parsing of parameters specific to this search goes here
            /**********************************************************************/
            
            // find all unique departments and get all courses from them 
            // to cut down on search time
            searcher = Search.createSearch(conn, id_num, college.toUpperCase(), term);
           
            HashMap<String, List<JSONObject>> reqs_by_dept = sort_reqs(
                    parseJSON(request.getParameter("reqs")));
            

            /**********************************************************************/
            // Finds go here
            /**********************************************************************/
            
            // check for entries with only one course number
            // search for each by itself
            // it takes the same amount of time search-wise
            // and results are more specific
            for(Entry<String, List<JSONObject>> entry : reqs_by_dept.entrySet()) {
                if(entry.getValue().size() == 1) {
                    MatchValuePair courseNumber;
                    try {
                        JSONObject req = entry.getValue().get(0);
                        String cnum;
                        // check if cnum is null (ie. ANY course from dept)
                        try {
                            cnum = Integer.toString(req.getInt("cnum"));
                        } catch (JSONException e) {
                            cnum = null;
                        }
                        if(req.getBoolean("hasAt")) {
                            // any cnum
                            if(null == cnum) {
                                courseNumber = null;
                            }
                            else {
                                // this is contains and not beginsWith 
                                // but it's as close as it gets
                                // we'll drop the rest later, anyway
                                courseNumber = new MatchValuePair(
                                    ID.contains, cnum);
                            }
                        } else {
                            courseNumber = new MatchValuePair(
                                    ID.exact, cnum);
                        }
                        searcher.find(courseNumber, null, null, null, null, null, 
                            Arrays.asList(new String[] {entry.getKey()}),
                            findErrors);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    
                }
            }
            
            // get departments with more than one course
            List<String> others = reqs_by_dept.entrySet().stream().filter(
                    en -> en.getValue().size() > 1).map(Entry::getKey)
                    .collect(Collectors.toList());

            searcher.find(null, null, null, null, null, null, others,
                            findErrors);
            

            /**********************************************************************/
            // Sorting goes here
            /**********************************************************************/

            // delete any rows that aren't actually requirements
            
            // get all 1-course depts where hasAt is true and cnum is not null
            // since contains is not foolproof
            for(Entry<String, List<JSONObject>> entry : reqs_by_dept.entrySet()) {
                if(entry.getValue().size() == 1) {
                    try {
                        JSONObject obj = entry.getValue().get(0);
                        if(obj.getBoolean("hasAt")
                                && obj.get("cnum") != JSONObject.NULL) {
                            others.add(entry.getKey());
                        }
                    } catch (JSONException e) {
                        continue;
                    }
                }
            }
            
            PreparedStatement delete;
            for(String department: others) {
                // courses to keep
                List<JSONObject> courses = reqs_by_dept.get(department);
                // the conditions to be and'ed together
                String[] conditions = new String[courses.size()];
                
                for(int i=0; i<courses.size(); ++i) {
                    try {
                        String cnum = Integer.toString(courses.get(i).getInt("cnum"));
                        if(courses.get(i).getBoolean("hasAt")) {
                            conditions[i] = "cnbr NOT LIKE '" + cnum + "%'";
                        } else {
                            conditions[i] = "cnbr!='" + cnum + "'";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        conditions[i] = "";
                    }
                }
                
                String ands = String.join(" AND ", conditions);
                try {
                    delete = conn.prepareStatement("DELETE FROM " + 
                            searcher.tableName() + " WHERE " +
                            "cdept=? AND " + ands + ";");
                    delete.setString(1, department);
                    delete.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            // check if any requirements fall within closed times
            assignPts(conn, sort_reqs(parseJSON(request.getParameter("reqs"))), 
                    schedule, searcher, false);
            createPtBasedTables(conn, searcher, id_num);
            
            
            /**********************************************************************/
            // Response parsing
            //  uncomment this when search/sort is functional
            /**********************************************************************/
            
            
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            college = college.toLowerCase();
            
            String queryBF;
            queryBF =
                    "select * into combined_section_table_" + id_num
                            + " from " + "best_fit_" + id_num + " left join college_courses" + college +
                            " on " + "best_fit_" + id_num +".cdept = college_courses" + college + ".dept and "
                            + "best_fit_" + id_num + ".cnbr = college_courses" +
                            college + ".nbr;";
            
            String querySC;
            querySC =
                    "select * into combined_section_table_" + id_num
                            + " from " + "some_conflicts_" + id_num + " left join college_courses" + college +
                            " on " + "some_conflicts_" + id_num +".cdept = college_courses" + college + ".dept and "
                            + "some_conflicts_" + id_num + ".cnbr = college_courses" +
                            college + ".nbr;";
            
            String queryO;
            queryO =
                    "select * into combined_section_table_" + id_num
                            + " from " + "others_" + id_num + " left join college_courses" + college +
                            " on " + "others_" + id_num +".cdept = college_courses" + college + ".dept and "
                            + "others_" + id_num + ".cnbr = college_courses" +
                            college + ".nbr;";
            
            String query1 = "alter table combined_section_table_" + id_num + " drop column dept";
            String query2 = "alter table combined_section_table_" + id_num + " drop column nbr";
            String query3 = "select * from combined_section_table_" + id_num;
            String query4 = "drop table combined_section_table_" + id_num;
            
            String queryBFDrop = "drop table " + "best_fit_" + id_num;
            String querySCDrop = "drop table " + "some_conflicts_" + id_num;
            String queryODrop = "drop table " + "others_" + id_num;
            String querySectionsDrop = "drop table " + searcher.tableName();

            PreparedStatement preparedStatement;
            ResultSet resultSet;

            try {
                // BEST FIT RESPONSE
                preparedStatement = conn.prepareStatement(queryBF);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query1);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query2);
                preparedStatement.execute();


                preparedStatement = conn.prepareStatement(query3);
                resultSet = preparedStatement.executeQuery();
                response.getWriter().write("ERRORS_BEGIN" + findErrors[0] + "," + findErrors[1]+ "," + findErrors[2] + "ERRORS_END");                
                
          
                
                response.getWriter().write("BEST_FIT_START");
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
                response.getWriter().write("BEST_FIT_END");
                preparedStatement = conn.prepareStatement(query4);
                preparedStatement.execute();
                
                // SOME CONFLICTS RESPONSE
                preparedStatement = conn.prepareStatement(querySC);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query1);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query2);
                preparedStatement.execute();


                preparedStatement = conn.prepareStatement(query3);
                resultSet = preparedStatement.executeQuery();
                
                response.getWriter().write("SOME_CONFLICTS_START");
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
                response.getWriter().write("SOME_CONFLICTS_END");
                preparedStatement = conn.prepareStatement(query4);
                preparedStatement.execute();
                
                // OTHERS RESPONSE
                preparedStatement = conn.prepareStatement(queryO);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query1);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(query2);
                preparedStatement.execute();


                preparedStatement = conn.prepareStatement(query3);
                resultSet = preparedStatement.executeQuery();
                
                response.getWriter().write("OTHERS_START");
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
                response.getWriter().write("OTHERS_END");
                preparedStatement = conn.prepareStatement(query4);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(queryODrop);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(queryBFDrop);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(querySCDrop);
                preparedStatement.execute();
                preparedStatement = conn.prepareStatement(querySectionsDrop);
                preparedStatement.execute();
  
            }
            catch (SQLException e) {

                e.printStackTrace();
            }
            
        }

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
