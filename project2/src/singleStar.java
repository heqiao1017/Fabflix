

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class singleStar
 */
@WebServlet("/singleStar")
public class singleStar extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public singleStar() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";
        String starName = request.getParameter("star");
        starName = starName.replaceAll(Pattern.quote("+"), " ");
        System.out.println("starName in single movie: "+ starName);
        response.setContentType("application/json");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            
            
            // Declare our statement
            Statement statement = dbcon.createStatement();
            String query = "select name, birthYear, GROUP_CONCAT(distinct title SEPARATOR '----') as mtitle from stars s, stars_in_movies t, movies m where m.id = t.movieId and t.starId = s.id and name =\"";
            query += starName;
            query += "\" GROUP BY s.id;";
            System.out.println(query);
            
            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            
//            JsonObject responseJsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            
            // Iterate through each row of rs
            while (rs.next()) {  
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_name", rs.getString("name"));
                jsonObject.addProperty("star_birth", rs.getString("birthYear"));
                jsonObject.addProperty("movie_title", rs.getString("mtitle"));
                
                jsonArray.add(jsonObject);
            }
            
            out.write(jsonArray.toString());
            
            rs.close();
            statement.close();
            dbcon.close();
        }catch (SQLException ex) {
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
