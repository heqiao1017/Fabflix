

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
 * Servlet implementation class singleMovie
 */
@WebServlet("/singleMovie")
public class singleMovie extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public singleMovie() {
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
        String title = request.getParameter("title");
        title = title.replaceAll(Pattern.quote("+"), " ");
        System.out.println("title in single movie: "+ title);
        
        //new added
//        if (title != null) {
//          title = title.replaceAll(Pattern.quote("+"), " ");
//          System.out.println("title in single movie: "+ title);
//        }
//        String genre = request.getParameter("genre");//new added
//        if (genre != null) {
//        		genre = genre.replaceAll(Pattern.quote("+"), " ");
//            System.out.println("genre in single movie java: "+ genre);
//         }
        
        response.setContentType("application/json");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            
            
            // Declare our statement
            Statement statement = dbcon.createStatement();
            String query = "SELECT m.id as ID, title, year, director, "
            		+ "GROUP_CONCAT(DISTINCT g.name) AS genres, GROUP_CONCAT(DISTINCT s.name) AS stars "
            		+ "FROM stars s, stars_in_movies t, genres g, genres_in_movies e, movies m "
            		+ "WHERE m.id = t.movieId AND t.starId = s.id AND m.id = e.movieId AND e.genreId = g.id AND "
            		+"title=\"" + title+"\" ";
            
            	//new added
//            String query = "SELECT m.id as ID, title, year, director, "
//            		+ "GROUP_CONCAT(DISTINCT g.name) AS genres, GROUP_CONCAT(DISTINCT s.name) AS stars "
//            		+ "FROM stars s, stars_in_movies t, genres g, genres_in_movies e, movies m "
//            		+ "WHERE m.id = t.movieId AND t.starId = s.id AND m.id = e.movieId AND e.genreId = g.id AND ";
//
//            if (title != null) {
//	        		query += "title='"+ title + "' ";
//            }
//            if (genre != null) {
//            		query += "g.name='"+ genre + "' ";
//            }
            
            query += "GROUP BY m.id";
            System.out.println(query);
            
            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            
//            JsonObject responseJsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            
            // Iterate through each row of rs
            while (rs.next()) {  
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", rs.getString("ID"));
                jsonObject.addProperty("movie_title", rs.getString("title"));
                jsonObject.addProperty("movie_year", rs.getString("year"));
                jsonObject.addProperty("movie_director", rs.getString("director"));
                jsonObject.addProperty("movie_genres", rs.getString("genres"));
                jsonObject.addProperty("movie_stars", rs.getString("stars"));
                
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
