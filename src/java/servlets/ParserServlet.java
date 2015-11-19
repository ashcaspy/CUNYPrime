/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import parser.ProfileParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

/**
 *
 * @author Ashley
 */
@WebServlet(name = "ParserServlet", urlPatterns = {"/parseprofilepdf"})
@MultipartConfig
public class ParserServlet extends HttpServlet {

    private ServletContext context; 
    @Override
    public void init(ServletConfig config) throws ServletException {
        //super.init(config); //To change body of generated methods, choose Tools | Templates.
        this.context = config.getServletContext();
    }

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
            out.println("<title>Servlet ParserServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ParserServlet at " + request.getContextPath() + "</h1>");
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
        String fname = request.getParameter("fname");
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        ProfileParser pp = new ProfileParser();
        //String p = pp.parseProfile("pdfs/" + fname + ".pdf");
        response.getWriter().write("p");
        
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
        String p = "";
        ProfileParser pp = new ProfileParser();
        
         try {

            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);            

             for (FileItem item : items) {
                 if (item.isFormField()) {
                     //String ajaxUpdateResult = "Field " + item.getFieldName() +
                            // " with value: " + item.getString() + " is successfully read\n\r";
                 } else {
                     //String fileName = item.getName();
                     InputStream content = item.getInputStream();
                     //response.setContentType("text/plain");
                     //response.setCharacterEncoding("UTF-8");
                     p = pp.parseProfile(content);
                     //System.out.println(Streams.asString(content));
                 }
             }
        } catch (FileUploadException e) {
            throw new ServletException("Parsing file upload failed.", e);
        }
    
        
        
        //InputStream fileInput = request.getInputStream();
        //String p = pp.parseProfile(fileInput);
        response.getWriter().write(p);
        
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
