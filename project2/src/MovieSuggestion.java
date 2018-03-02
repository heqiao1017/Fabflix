

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MovieSuggestion
 */
@WebServlet("/MovieSuggestion")
public class MovieSuggestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieSuggestion() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
				// setup the response json arrray
				JsonArray jsonArray = new JsonArray();
				
				// get the query string from parameter
				String full_text_query = request.getParameter("query");
				
				// return the empty json array if query is null or empty
				if (full_text_query == null || full_text_query.trim().isEmpty()) {
					response.getWriter().write(jsonArray.toString());
					return;
				}	
				
				// search on marvel heros and DC heros and add the results to JSON Array
				// this example only does a substring match
				// TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
				String loginUser = "mytestuser";
		        String loginPasswd = "mypassword";
		        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";
	
		        response.setContentType("application/json");
		        
		        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
	            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
	            Statement statement = dbcon.createStatement();
	            String[] splitStrs = full_text_query.trim().split("\\s+");
	            String query = "SELECT id, title FROM movies WHERE MATCH (title) AGAINST ('"; //('+grad* +E*' IN BOOLEAN MODE);";
	            for (String splitStr : splitStrs) {
	    				query += "+"+splitStr+"* ";
	    			}
	    			query.trim();
	    			query += "' IN BOOLEAN MODE)";
	    			
	    			System.out.println(query);
	    			
	    			int limit = 10;
	    			
	    			ResultSet rs = statement.executeQuery(query);
	    			while (rs.next() && limit > 0) {
	    				jsonArray.add(generateJsonObject(rs.getString("id"), rs.getString("title"), "Movie"));
	    				limit--;
	    			}
	    			
	    			if (limit > 0 ) {
	    				query = "SELECT id, name FROM stars WHERE MATCH (name) AGAINST ('"; //('+grad* +E*' IN BOOLEAN MODE);";
	    	            for (String splitStr : splitStrs) {
	    	    				query += "+"+splitStr+"* ";
	    	    			}
	    	    			query.trim();
	    	    			query += "' IN BOOLEAN MODE)";
	    	    			
	    	    			System.out.println(query);
	    	    			
	    	    			rs = statement.executeQuery(query);
	    	    			while (rs.next() && limit > 0) {
	    	    				jsonArray.add(generateJsonObject(rs.getString("id"), rs.getString("name"), "Star"));
	    	    				limit--;
	    	    			}
	    			}
				response.getWriter().write(jsonArray.toString());
				rs.close();
	            statement.close();
	            dbcon.close();
				return;
		} catch (Exception e) {
			System.out.println(e);
			response.sendError(500, e.getMessage());
		}
	}
	
	/*
	 * Generate the JSON Object from hero and category to be like this format:
	 * {
	 *   "value": "Iron Man",
	 *   "data": { "category": "marvel", "heroID": 11 }
	 * }
	 * 
	 */
	private static JsonObject generateJsonObject(String id, String value, String categoryName) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", value);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("category", categoryName);
		additionalDataJsonObject.addProperty("id", id);
		
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
