

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		String loginUser = "qiaoh3";
        String loginPasswd = "zj@38971";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";

        response.setContentType("application/json");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            
            
            // Declare our statement
            Statement statement = dbcon.createStatement();
            
            String query = "select password from customers where email = \"" + username + "\"";
            System.out.println(query);
            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            boolean exist = rs.next();
            System.out.println("exist: " + exist);
            JsonObject responseJsonObject = new JsonObject();
            
            if (exist) {
            		//email exist, check password
            		if (password.equals(rs.getString("password"))) {
            			//login success
            			request.getSession().setAttribute("user", new User(username));
            			//JsonObject responseJsonObject = new JsonObject();
	    	    			responseJsonObject.addProperty("status", "success");
	    	    			responseJsonObject.addProperty("message", "success");
	    	    			//out.write(responseJsonObject.toString());
            		}
            		else {
	    	    			responseJsonObject.addProperty("status", "fail");
	    	    			responseJsonObject.addProperty("message", "Incorrect password, try again!");
	    	    			//out.write(responseJsonObject.toString());
            		}
            }
            else {
            		System.out.println("email does not exist ");
            		//email does not exist
            		//JsonObject responseJsonObject = new JsonObject();
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
