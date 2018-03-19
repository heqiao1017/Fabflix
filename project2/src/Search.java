import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class Search
 */
@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long startTime_0 = System.nanoTime();
		
		
		String title = request.getParameter("title");
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
			
		String genre = request.getParameter("genre");//from search by genre, since this jump to movielist.html, which connected with search.js - > search.java
		String titleFirstChar = request.getParameter("titleFirstChar");//from search by movie title, same up
		
		String full_text_query = request.getParameter("query");
		
		String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";

        response.setContentType("application/json");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
        		long startTime_Ts = System.nanoTime();
            /*
        		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            */
            //*******************************************************
            
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
            
            PreparedStatement updateSearchQuery = null;
            
            String query = "SELECT m.id as ID, title, year, director, "
            		+ "GROUP_CONCAT(DISTINCT g.name) AS genres, GROUP_CONCAT(DISTINCT s.name) AS stars "
            		+ "FROM stars s, stars_in_movies t, genres g, genres_in_movies e, movies m "
            		+ "WHERE m.id = t.movieId AND t.starId = s.id AND m.id = e.movieId AND e.genreId = g.id ";
            
            String searchQuery = "SELECT m.id as ID, title, year, director, "
            		+ "GROUP_CONCAT(DISTINCT g.name) AS genres, GROUP_CONCAT(DISTINCT s.name) AS stars "
            		+ "FROM stars s, stars_in_movies t, genres g, genres_in_movies e, movies m "
            		+"INNER JOIN (select id from movies where match (title) against (? IN BOOLEAN MODE) OR edth(lower(title), ? , ?)"
            		+ ") as k on k.id = m.id WHERE m.id = t.movieId AND t.starId = s.id AND m.id = e.movieId AND e.genreId = g.id GROUP BY m.id";
            
            //More user-friendly full text search query
            if (full_text_query != null) { 		
            		query = "SELECT m.id as ID, title, year, director, "
                    		+ "GROUP_CONCAT(DISTINCT g.name) AS genres, GROUP_CONCAT(DISTINCT s.name) AS stars "
                    		+ "FROM stars s, stars_in_movies t, genres g, genres_in_movies e, movies m "
                    		+"INNER JOIN (select id from movies where match (title) against ('";
            		
            		String[] splitStrs = full_text_query.trim().split("\\s+");
            		for (String splitStr : splitStrs) {
            			query += "+"+splitStr+"* ";
            		}
            		
            		
            		query.trim();
            		query += "' IN BOOLEAN MODE)";
            		
            		String new_query = full_text_query.trim().toLowerCase();
	    			int len = new_query.length();
	    			if (len > 5) {
    					if (len <= 9)
    						query += " OR edth(lower(title), '" + new_query + "', 1)";
    					else if (len <= 15)
    						query += " OR edth(lower(title), '" + new_query + "', 2)";
    					else
    						query += " OR edth(lower(title), '" + new_query + "', 3)";
    				}
	    			
	    			query += ") as k on k.id = m.id WHERE m.id = t.movieId AND t.starId = s.id AND m.id = e.movieId AND e.genreId = g.id ";
            }
            else if (genre!=null) {
            		query = "SELECT m.id as ID, title, year, director, "
                		+ "GROUP_CONCAT(DISTINCT g.name) AS genres, GROUP_CONCAT(DISTINCT s.name) AS stars "
                		+ "FROM stars s, stars_in_movies t, genres g, genres_in_movies e, movies m "
                		+"INNER JOIN (select r.movieId as mId from genres_in_movies r INNER JOIN movies x on x.id = r.movieId INNER JOIN genres f on f.id = r.genreId and f.name = '"+genre+"') as k ON k.mId = m.id"
                		+ " WHERE m.id = t.movieId AND t.starId = s.id AND m.id = e.movieId AND e.genreId = g.id ";
            }
            else if (titleFirstChar != null) {
	        		query += "and title like '"+titleFirstChar+"%' ";
            }
            else {
                if (!title.equals("")) {
                    title = title.replaceAll(Pattern.quote("+"), " ");
                    query += "and title LIKE \"%"+ title + "%\" ";
                }
                if (!year.equals("")) {
                        query += "and year =\""+ year + "\" ";
                }
                if (!director.equals("")) {
                        director = director.replaceAll(Pattern.quote("+"), " ");
                        query += "and director LIKE \"%"+ director+ "%\" ";
                }
                if (!firstName.equals("") && !lastName.equals("")) {
                        query += "and s.name LIKE \"%"+ firstName + " " + lastName + "%\" ";
                }
                else if (!firstName.equals("")) {
                        query += "and s.name LIKE \"%"+ firstName +"%\" ";
                }
                else if (!lastName.equals("")){
                        query += "and s.name LIKE \"%"+ lastName +"%\" ";
                }
            }
            
	        query += "GROUP BY m.id";

	        //System.out.println(query);
	        //System.out.println("SearchQuery: "+query);
	        
	        ResultSet rs = null;
	        
	        
	        if (full_text_query != null) 
	        {
	        		String fullText = null;
		        	
	        		String[] splitStrs = full_text_query.trim().split("\\s+");
	        		for (String splitStr : splitStrs) {
	        			fullText = "+"+splitStr+"* ";
	        		}
	        		fullText = fullText.trim();
	        		
	        		String new_query = full_text_query.trim().toLowerCase();
	    			int len = new_query.length();
	    			int diff = 0;
	    			if (len > 5) {
    					if (len <= 9) {
    						//fuzzy = " OR edth(lower(title), '" + new_query + "', 1)";
    						diff = 1;
    					}
    					else if (len <= 15) {
    						//fuzzy = " OR edth(lower(title), '" + new_query + "', 2)";
    						diff = 2;
    					}
    					else {
    						//fuzzy = " OR edth(lower(title), '" + new_query + "', 3)";
    						diff = 3;
    					}
    				}
	        		
	        		dbcon.setAutoCommit(false);
	        		updateSearchQuery = dbcon.prepareStatement(searchQuery);
	        		updateSearchQuery.setString(1, fullText);
	        		//updateSearchQuery.setNull(2, java.sql.Types.VARCHAR);
	        		updateSearchQuery.setString(2, new_query);
	        		updateSearchQuery.setInt(3, diff);
	        		rs = updateSearchQuery.executeQuery();
	        		dbcon.commit();
	        }else {
	        		rs = statement.executeQuery(query);
	        }
	        
	          
            //uncomment the next line when not doing prepare statement
            //rs = statement.executeQuery(query);
	        
	        long endTime_T = System.nanoTime();
	        long elapsedTime_TJ = endTime_T - startTime_Ts;
            

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
            
            
            //##################################################
            long endTime = System.nanoTime();
            long elapsedTime_TS = endTime - startTime_0;
            //long elapsedTime_Tq = endTime - startTime_Ts;
            
            //String data = "TS "+elapsedTime_TS+" TJ "+elapsedTime_TJ+" Tq "+elapsedTime_Tq+"\n";
            String data = "TS "+elapsedTime_TS+" TJ "+elapsedTime_TJ+"\n";
            String savestr = "log.txt"; 
	    		File f = new File(savestr);
	
	    		PrintWriter outfile = null;
	    		if ( f.exists() && !f.isDirectory() ) {
	    			outfile = new PrintWriter(new FileOutputStream(new File(savestr), true));
	    		}
	    		else {
	    			outfile = new PrintWriter(savestr);
	    		}
	    		outfile.append(data);
	    		outfile.close();
            
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
