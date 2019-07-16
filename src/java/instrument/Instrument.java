/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instrument;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author HP
 */
public class Instrument extends HttpServlet {

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
          
           String [] paramresult = request.getParameterValues("result");
           String result="";
           
           if (paramresult.length > 0){
               result=paramresult[0];
           }
           
           /*
           try (OutputStream output = new FileOutputStream("configuration.properties")) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("db.db", "localhost");
            prop.setProperty("db.2  ", "mkyong");
            prop.setProperty("db.3", "password");

            // save properties to project root folder
            prop.store(output, null);

            System.out.println(prop);

        } catch (IOException io) {
            io.printStackTrace();
        }
        */
            //loading all config options
           
            Properties prop = new Properties();
        
            String dbname = "";
            String resultable = "";
            String user = "";
            String password = "";
            String host = "";
            String port = "";
            
            try {

                InputStream in = getClass().getResourceAsStream("configuration.properties");
                
                prop = new Properties();

                // load a properties file
                prop.load(in);
                
                dbname=prop.getProperty("db.name");
                resultable=prop.getProperty("db.resultable");
                user=prop.getProperty("db.user");
                password=prop.getProperty("db.password");
                host=prop.getProperty("db.host");
                port=prop.getProperty("db.port");
                
            }catch(Exception e){

            }
                                   
            //Properties prop = getSystemStringProperties();
            
            
           if (!result.equals("")){
               //insertar en la base de datos
               try{
                   PreparedStatement pst = null;
                   String text = "";
                   Boolean b = false;
                   String consulta = "";
                   
                    try (Connection connection = DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+dbname, user, password)) {

                         System.out.println("Java JDBC PostgreSQL Example");
                         // When this class first attempts to establish a connection, it automatically loads any JDBC 4.0 drivers found within 
                         // the class path. Note that your application must manually load any JDBC drivers prior to version 4.0.
             //          Class.forName("org.postgresql.Driver"); 

                         System.out.println("Connected to PostgreSQL database!");
                         
                         try{
                             
                         pst = connection.prepareStatement("INSERT INTO "+resultable+"(\"ENTRY\") VALUES (?)");
                         
                         //remove breaklines from instrument
                         text = result.replace("\n", "").replace("\r", "");
                         pst.setString(1, text);
                         b = pst.execute();
                         
                         }catch(Exception e){
                             Statement st = connection.createStatement();
                             consulta = "INSERT INTO "+resultable+" (\"ENTRY\") VALUES ('"+text.replace("\n", "").replace("\r", "")+"')";
                             st.executeUpdate(consulta);
                             st.close();
                         }
                         
                         if (b){
                             out.println("Guardado exitoso en base de datos postgres");
                         }
                         
                         out.close();
                         
                     } /*catch (ClassNotFoundException e) {
                         System.out.println("PostgreSQL JDBC driver not found.");
                         e.printStackTrace();
                     }*/ catch (SQLException e) {
                         pst.close();
                         System.out.println("Connection failure.");
                         e.printStackTrace();
                     }finally{
                         pst.close();
                    }
           
                }catch(Exception e){
                   System.out.println(e.getLocalizedMessage());
                }
            }
               
           }
        }
    

/*
    public static Properties getSystemStringProperties() {
        Properties prop = new Properties();
        String cargar = "";
        String strClassPath = System.getProperty( "java.class.path" );
                String ud = System.getProperty("user.dir");
                
         try (InputStream input = new FileInputStream("src/java/instrument/configuration.properties")) {

             //prop.load(Classname.class.getClassLoader().getResourceAsStream("foo.properties"));
             
                               prop = new Properties();

                               // load a properties file
                               prop.load(input);
                               
         }catch(Exception e){
             
         }
                            
         /*
    // result list
    List<String> result = new LinkedList<>();

    // defining variable for assignment in loop condition part
    String value;

    // next value loading defined in condition part
    for(int i = 0; (value = prop.getProperty("db." + i)) != null; i++) {
        result.add(value);
    }

    // return
         
    return prop;
}
*/

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
