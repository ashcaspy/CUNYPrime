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
import search.CunyFirstSearch;
import search.cunyfirst.ID;
import search.cunyfirst.MatchValuePair;
import scheduling.*;

import org.json.*;
import search.BackupSearch;

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


    protected void closeTimeMatchAction (ResultSet res, PreparedStatement update, Day day, Integer value, boolean isTimeBased){
        int closeTimeIndex = 0;

        if(!day.isCloseTimesEmpty()){
            while (closeTimeIndex < day.getCloseTimeSize()){
                try{
                    if(day.getClosedTimeElement(closeTimeIndex) * 100 == res.getInt("starttime") ||
                            day.getClosedTimeElement(closeTimeIndex) * 100 == res.getInt("endtime") ||
                            (day.getClosedTimeElement(closeTimeIndex) * 100  > res.getInt("starttime") &&
                                    day.getClosedTimeElement(closeTimeIndex) * 100 < res.getInt("endtime")) ){
                        if(isTimeBased){
                            value += 2;
                        } else {
                            value += 1;
                        }
                        update.setInt(1, value);

                        update.execute();
                    }
                    closeTimeIndex++;
                } catch (SQLException e){
                    e.printStackTrace();

                }

            }
        }

    }
    
    protected void createPtBasedTables(Connection conn, Search searcher, int id_num){
        try {
            
            PreparedStatement myStatement = conn.prepareStatement("delete from " + searcher.tableName() + " where points >= 3");
            myStatement.execute();
            myStatement = conn.prepareStatement("select * into best_fit_" + id_num + " from " + searcher.tableName() + " where points = 0");
            myStatement.execute();
            myStatement = conn.prepareStatement("select * into some_conflicts_" + id_num + " from " + searcher.tableName() + " where points = 1");
            myStatement.execute();
            myStatement = conn.prepareStatement("select * into others_" + id_num + " from " + searcher.tableName() + " where points = 2");
            myStatement.execute();
            
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    
    protected void searchAction(Connection conn, HttpServletRequest request, Schedule schedule, Search searcher, boolean isTimeBased){
        
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            Integer value = 0; 
            String queryA = "select * from "+ searcher.tableName();

           
            try {

                String days = "";
                preparedStatement = conn.prepareStatement(queryA);
                preparedStatement.execute();
                resultSet = preparedStatement.executeQuery();
                PreparedStatement update = conn.prepareStatement("UPDATE "+searcher.tableName()+" "
                        + "SET points=? WHERE cdept=? AND cnbr=? AND sec=?");

                while (resultSet.next()){
                    value = 0;
                    update.setString(2, resultSet.getString("cdept"));
                    update.setString(3, resultSet.getString("cnbr"));
                    update.setString(4, resultSet.getString("sec"));

                    days = resultSet.getString("days");

                    
                    for(int i=0; i<schedule.getWeek().length; ++i) {
                        if(days.contains(schedule.getWeek()[i])) {
                            Day temp = schedule.getElementFromSchedule(i);
                            closeTimeMatchAction(resultSet, update, temp, value, isTimeBased);
                        }
                    }
                    
                    if(isTimeBased){
                        boolean hasMatch = false;
                        int counter = 0;
                        for (int i = 0; i < parseJSON(request.getParameter("reqs")).length; i++){
                           counter = 0;
                            try {
                                if( parseJSON(request.getParameter("reqs"))[i].getString("dept").equals(resultSet.getString("cdept").replaceAll(" ", ""))){
                                    if(parseJSON(request.getParameter("reqs"))[i].getBoolean("hasAt")) {
                                        for (int k = 0; k < parseJSON(request.getParameter("reqs"))[i].length(); k++){
                                            if(resultSet.getString("cnum").charAt(k) != parseJSON(request.getParameter("reqs"))[i].getString("cnum").charAt(k)){
                                                counter++;
                                            }
                                            
                                        }
                                        if(counter == 0){
                                            hasMatch = hasMatch || true;
                                        } else {
                                            hasMatch = hasMatch || false;
                                        }
                                    } else if(!resultSet.getString("cnbr").replaceAll(" ","").equals(parseJSON(request.getParameter("reqs"))[i].getString("cnum"))){
                                        hasMatch = hasMatch || false;
                                    }


                                }
                            } catch(JSONException e){
                               e.printStackTrace();
                            }

                            if(!hasMatch && parseJSON(request.getParameter("reqs")).length > 0){
                                update.setInt(1, value + 1);
                                update.execute();
                            }

                        }
                    }
                    
                }
            }catch (SQLException e) {

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

    
    protected void putTimeSlotsInSched(Schedule schedule, HttpServletRequest request){
        try {
                JSONArray arr=  new JSONArray(request.getParameter("sched_open"));
                String openTimesArr [] = new String[arr.length()];
                for (int i =0; i < arr.length(); i++){
                    openTimesArr[i] = arr.getString(i);
                }
                schedule.setOpenTimes(openTimesArr);
                
                arr = new JSONArray(request.getParameter("sched_closed"));
                String closedTimesArr[] = new String[arr.length()];
                  for (int i =0; i < arr.length(); i++){
                    openTimesArr[i] = arr.getString(i);
                }
                schedule.setCloseTimes(closedTimesArr);
            
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
        
        // create searcher
        
        MatchValuePair mvpair = null;
        if (!"".equals(course_num)) {
            mvpair = new MatchValuePair(ID.contains, course_num);
        }

        if ("".equals(keyword))
            keyword = null;
        if ("".equals(prof))
            prof = null;
        System.out.println(request.getParameter("search_type"));

        if (request.getParameter("search_type").equals("DEFAULT_SEARCH")){

            searcher = Search.createSearch(conn, id_num, college.toUpperCase(), term);
            
            searcher.find(
                    mvpair,
                    null, null,
                    keyword,
                    prof,
                    new int[] {},
                    Arrays.asList(new String[]{dept})
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

            /**********************************************************************/
            // Sorting goes here
            /**********************************************************************/
            
            
            searchAction(conn, request, schedule, searcher, true);
            createPtBasedTables(conn, searcher, id_num);
            
            
            /**********************************************************************/
            // Response parsing
            //  uncomment this when search/sort is functional
            /**********************************************************************/
            
            
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            college = college.toLowerCase();
            
            String queryBF;
            if (college.equals("htr01") || college.equals("cty01")){
                queryBF =
                    "select * into combined_section_table_" + id_num
                            + " from " + "best_fit" + id_num + " left join college_courses" + college +
                            " on " + "best_fit" + id_num +".cdept = college_courses" + college + ".dept and "
                            + "best_fit" + id_num + ".cnbr = college_courses" +
                            college + ".nbr;";
            }
            else{
                queryBF = "select * into combined_section_table_" + id_num
                            + " from " + "best_fit" + " left join college_courses" + "_empty" +
                            " on " + "best_fit" +".cdept = college_courses" + "_empty" + ".dept and "
                            + "best_fit" + ".cnbr = college_courses" +
                            "_empty" + ".nbr;";
            }
            String querySC;
            if (college.equals("htr01") || college.equals("cty01")){
                querySC =
                    "select * into combined_section_table_" + id_num
                            + " from " + "some_conflicts" + id_num + " left join college_courses" + college +
                            " on " + "some_conflicts" + id_num +".cdept = college_courses" + college + ".dept and "
                            + "some_conflicts" + id_num + ".cnbr = college_courses" +
                            college + ".nbr;";
            }
            else{
                querySC = "select * into combined_section_table_" + id_num
                            + " from " + "some_conflicts" + " left join college_courses" + "_empty" +
                            " on " + "some_conflicts" +".cdept = college_courses" + "_empty" + ".dept and "
                            + "some_conflicts" + ".cnbr = college_courses" +
                            "_empty" + ".nbr;";
            }
            String queryO;
            if (college.equals("htr01") || college.equals("cty01")){
                queryO =
                    "select * into combined_section_table_" + id_num
                            + " from " + "others" + id_num + " left join college_courses" + college +
                            " on " + "others" + id_num +".cdept = college_courses" + college + ".dept and "
                            + "others" + id_num + ".cnbr = college_courses" +
                            college + ".nbr;";
            }
            else{
                queryO = "select * into combined_section_table_" + id_num
                            + " from " + "others" + " left join college_courses" + "_empty" +
                            " on " + "others" +".cdept = college_courses" + "_empty" + ".dept and "
                            + "others" + ".cnbr = college_courses" +
                            "_empty" + ".nbr;";
            }
            
            String query1 = "alter table combined_section_table_" + id_num + " drop column dept";
            String query2 = "alter table combined_section_table_" + id_num + " drop column nbr";
            String query3 = "select * from combined_section_table_" + id_num;
            String query4 = "drop table combined_section_table_" + id_num;
            
            String queryBFDrop = "drop table " + "best_fit" + id_num;
            String querySCDrop = "drop table " + "some_conflicts" + id_num;
            String queryODrop = "drop table " + "others" + id_num;

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
           
            HashMap<String, ArrayList<JSONObject>> reqs_by_dept = new HashMap<>();
            for(JSONObject obj : parseJSON(request.getParameter("reqs"))) {
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

            /**********************************************************************/
            // Finds go here
            /**********************************************************************/
            
            // check for entries with only one course number
            // search for each by itself
            // it takes the same amount of time search-wise
            // and results are more specific
            for(Entry<String, ArrayList<JSONObject>> entry : reqs_by_dept.entrySet()) {
                if(entry.getValue().size() == 1) {
                    MatchValuePair courseNumber;
                    try {
                        JSONObject req = entry.getValue().get(0);
                        String cnum = Integer.toString(req.getInt("cnum"));
                        if(req.getBoolean("hasAt")) {
                            // this is contains and not beginsWith 
                            // but it's as close as it gets
                            courseNumber = new MatchValuePair(
                                    ID.contains, cnum);
                        } else {
                            courseNumber = new MatchValuePair(
                                    ID.exact, cnum);
                        }
                        searcher.find(courseNumber, null, null, null, null, null, 
                            Arrays.asList(new String[] {entry.getKey()}));
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    
                }
            }
            
            // get departments with more than one course
            List<String> others = reqs_by_dept.entrySet().stream().filter(
                    en -> en.getValue().size() > 1).map(Entry::getKey)
                    .collect(Collectors.toList());

            searcher.find(null, null, null, null, null, null, others);
            

            /**********************************************************************/
            // Sorting goes here
            /**********************************************************************/

            // delete any rows that aren't actually requirements
            
            PreparedStatement delete;
            for(String department: others) {
                // courses to keep
                ArrayList<JSONObject> courses = reqs_by_dept.get(department);
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
            searchAction(conn, request, schedule, searcher, false);
            createPtBasedTables(conn, searcher, id_num);
            
            
            /**********************************************************************/
            // Response parsing
            //  uncomment this when search/sort is functional
            /**********************************************************************/
            
            
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            college = college.toLowerCase();
            
            String queryBF;
            if (college.equals("htr01") || college.equals("cty01")){
                queryBF =
                    "select * into combined_section_table_" + id_num
                            + " from " + "best_fit" + id_num + " left join college_courses" + college +
                            " on " + "best_fit" + id_num +".cdept = college_courses" + college + ".dept and "
                            + "best_fit" + id_num + ".cnbr = college_courses" +
                            college + ".nbr;";
            }
            else{
                queryBF = "select * into combined_section_table_" + id_num
                            + " from " + "best_fit" + " left join college_courses" + "_empty" +
                            " on " + "best_fit" +".cdept = college_courses" + "_empty" + ".dept and "
                            + "best_fit" + ".cnbr = college_courses" +
                            "_empty" + ".nbr;";
            }
            String querySC;
            if (college.equals("htr01") || college.equals("cty01")){
                querySC =
                    "select * into combined_section_table_" + id_num
                            + " from " + "some_conflicts" + id_num + " left join college_courses" + college +
                            " on " + "some_conflicts" + id_num +".cdept = college_courses" + college + ".dept and "
                            + "some_conflicts" + id_num + ".cnbr = college_courses" +
                            college + ".nbr;";
            }
            else{
                querySC = "select * into combined_section_table_" + id_num
                            + " from " + "some_conflicts" + " left join college_courses" + "_empty" +
                            " on " + "some_conflicts" +".cdept = college_courses" + "_empty" + ".dept and "
                            + "some_conflicts" + ".cnbr = college_courses" +
                            "_empty" + ".nbr;";
            }
            String queryO;
            if (college.equals("htr01") || college.equals("cty01")){
                queryO =
                    "select * into combined_section_table_" + id_num
                            + " from " + "others" + id_num + " left join college_courses" + college +
                            " on " + "others" + id_num +".cdept = college_courses" + college + ".dept and "
                            + "others" + id_num + ".cnbr = college_courses" +
                            college + ".nbr;";
            }
            else{
                queryO = "select * into combined_section_table_" + id_num
                            + " from " + "others" + " left join college_courses" + "_empty" +
                            " on " + "others" +".cdept = college_courses" + "_empty" + ".dept and "
                            + "others" + ".cnbr = college_courses" +
                            "_empty" + ".nbr;";
            }
            
            String query1 = "alter table combined_section_table_" + id_num + " drop column dept";
            String query2 = "alter table combined_section_table_" + id_num + " drop column nbr";
            String query3 = "select * from combined_section_table_" + id_num;
            String query4 = "drop table combined_section_table_" + id_num;
            
            String queryBFDrop = "drop table " + "best_fit" + id_num;
            String querySCDrop = "drop table " + "some_conflicts" + id_num;
            String queryODrop = "drop table " + "others" + id_num;

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
