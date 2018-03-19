

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		boolean isEmployee = false;
		
		if (username == null && password == null) {
			isEmployee = true;
			username = request.getParameter("e_username");
			password = request.getParameter("e_password");
		}
		
//		String logout = request.getParameter("logout");
//		if (logout != null && request.getSession().getAttribute("user") != null) {
//			System.out.println("logout != null && request.getSession().getAttribute(\"user\") != null");
//			request.getSession().removeAttribute("user");
//			return;
//		}

//		String loginUser = "mytestuser";
//        String loginPasswd = "mypassword";
//        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        /*String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
		boolean valid = VerifyUtils.verify(gRecaptchaResponse);
		
		
		 
		if (!valid) {
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("status", "fail");
			responseJsonObject.addProperty("message", "Recaptcha WRONG!!!!!");
			out.write(responseJsonObject.toString());	
			return;
		}*/


        try {
//            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
//            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            
        		//*******************************************************
        		// the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();
            if (initCtx == null)
                out.println("initCtx is NULL");

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");
            //*******************************************************
            
            // Declare our statement
            Statement statement = dbcon.createStatement();
            
            String query = "select password from customers where email = \"" + username + "\"";
            if (isEmployee) {
            		query = "select password from employees where email = \"" + username + "\"";
            }
            
            System.out.println(query);
            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            boolean exist = rs.next();
//            System.out.println("exist: " + exist);
            JsonObject responseJsonObject = new JsonObject();
            
            if (exist) {
            		//email exist, check password
            		if (password.equals(rs.getString("password"))) {
            			//login success
            			if (!isEmployee) {
            				request.getSession().setAttribute("user", new User(username));
            			}
            			else {
            				request.getSession().setAttribute("user", new User(username));
            			}
            			
	    	    			responseJsonObject.addProperty("status", "success");
	    	    			responseJsonObject.addProperty("message", "success");
            		}
            		else {
	    	    			responseJsonObject.addProperty("status", "fail");
	    	    			responseJsonObject.addProperty("message", "Incorrect password, try again!");
            		}
            }
            else {
            		System.out.println("email does not exist ");
            		//email does not exist

	    			responseJsonObject.addProperty("status", "fail");
	    			responseJsonObject.addProperty("message", "User " + username + " not exist!");	
            }
            out.write(responseJsonObject.toString());	
            rs.close();
            statement.close();
            dbcon.close();
            
        } catch (SQLException ex) {
            while (ex != null) {
                System.out.println("SQL Exception:  " + ex.getMessage());
                ex = ex.getNextException();
            } // end while
        } // end catch SQLException

        catch (java.lang.Exception ex) {
            out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>"
                    + "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
            return;
        }
        out.close();
    
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
