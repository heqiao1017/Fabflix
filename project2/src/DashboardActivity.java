

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


/**
 * Servlet implementation class DashboardActivity
 */
@WebServlet("/DashboardActivity")
public class DashboardActivity extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DashboardActivity() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String starName = request.getParameter("name");
		String birthyear = request.getParameter("birth");
		
		boolean metadata  = false;
		if (starName == null && birthyear == null) {
			metadata = true;
		}
		
		String title = request.getParameter("title");
		String movieYear = request.getParameter("movieYear");
		String director = request.getParameter("director");
		String genre = request.getParameter("genre");
		boolean addMovie = false;
		if (title != null && movieYear != null && director != null && genre != null) {
			addMovie = true;
		}
		
		String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();
        if (!metadata) {
	        if (!addMovie && starName.equals("")) {
	    			responseJsonObject.addProperty("message", "star name cannot be empty");
	    			out.write(responseJsonObject.toString());	
	    			out.close();
	        		return;
	        }
        }
        
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Statement statement = dbcon.createStatement();
            String query = "";
            
            //check if maxID table in DB
			query = "show tables like 'maxId'";
			System.out.println(query);
			ResultSet rs = statement.executeQuery(query);
			boolean exist = rs.next();
			if (!exist) 
			{
				//create the maxId table
				query = "create table maxId (movieMaxId INTEGER, starMaxId INTEGER);";
				System.out.println(query);
				int rs_create = statement.executeUpdate(query);
				
				//get max star count
				query = "select max(id) from stars;";
				System.out.println(query);
				rs = statement.executeQuery(query);
				String starMax = "";
				while (rs.next()) {
					starMax = rs.getString("max(id)");
				}
				int starMaxCount = Integer.parseInt(starMax.substring(2));
				System.out.println("starMaxCount: "+starMaxCount);
				
				//get max movie count
				query = "select max(id) from movies;";
				System.out.println(query);
				rs = statement.executeQuery(query);
				String movieMax = "";
				while (rs.next()) {
					movieMax = rs.getString("max(id)");
				}
				int movieMaxCount = Integer.parseInt(movieMax.substring(2));
				System.out.println("movieMaxCount: "+movieMaxCount);
				
				//store the max count into maxId table
				query = "insert into maxId values("+ movieMaxCount + "," + starMaxCount+");";
				System.out.println(query);
				rs_create = statement.executeUpdate(query);
				if (rs_create == 1) {
					System.out.println("store the max count into maxId table success");
				}
			}
			
            if (!metadata && !addMovie) 
            {
            		//currently doing the star insert
            		query = "select * from stars where name='"+starName+"';";
            		System.out.println(query);
            		rs = statement.executeQuery(query);
            		exist = rs.next();
            		if (exist) {
            			responseJsonObject.addProperty("message", "Duplicate star, insert failed!");
            		}
            		else {
            			//insert the new star
            			query = "select starMaxId from maxId;";
            			System.out.println(query);
            			rs = statement.executeQuery(query);
            			
            			int maxStarId=0;
            			if (rs.next())
            				maxStarId = rs.getInt("starMaxId") + 1;
            			
            			//update the maxStarId in maxId table
            			query = "update maxId set starMaxId = "+maxStarId;
            			int rs_update = statement.executeUpdate(query);
            			if (rs_update == 1) {
            				System.out.println("rs_update, success: " + maxStarId);
            			}
            			
            			String newStarId= "nm"+String.valueOf(maxStarId);
            			query = "insert into stars values('"+newStarId+"','"+starName+"',";
            			System.out.println(query);
            			if (!birthyear.equals("")) {
                    		query += birthyear;
            			}else {
            				query += "null";
            			}
            			query +=");";
            			System.out.println(query);
            			int rs_star = statement.executeUpdate(query);
            			if (rs_star == 1) {
                    		responseJsonObject.addProperty("message", "Insert Successful");
                    }
            			else {
            				responseJsonObject.addProperty("message", "Insert failed, something happened!");
            			} 			
            		}
            		out.write(responseJsonObject.toString());	
            }
            else if (!addMovie) {
            		query = "show tables;";
            		ResultSet rs_meta = statement.executeQuery(query);
            		JsonArray jsonTableArray = new JsonArray();
            		List<String> tables = new ArrayList<>();
            		while (rs_meta.next()) {
            			String table = rs_meta.getString("Tables_in_moviedb");
            			tables.add(table);
            		}
            		for (String table : tables) {
            			query = "show columns from "+table;
            			ResultSet table_rs = statement.executeQuery(query);
            			JsonArray jsonArray = new JsonArray();
            			while (table_rs.next()) {
            				JsonObject jsonObject = new JsonObject();
            				jsonObject.addProperty("table_name", table);
            				jsonObject.addProperty("table_field", table_rs.getString("Field"));
            				jsonObject.addProperty("table_type", table_rs.getString("Type"));
            				jsonArray.add(jsonObject);
            			}
            			jsonTableArray.add(jsonArray);
            		}
        			
            		out.write(jsonTableArray.toString());
            		rs_meta.close();
            }
            else {
            		System.out.println("before call procedure");
            		CallableStatement stmt = dbcon.prepareCall("{call add_movie(?,?,?,?,?,?,?)}");
            		stmt.setString(1,title);
            		int movieYearInt = (movieYear.equals("")? 0: Integer.parseInt(movieYear));
            		stmt.setInt(2,movieYearInt);
            		stmt.setString(3,director);
            		stmt.setString(4,starName);
            		int starBirthYear = (birthyear.equals("")? 0: Integer.parseInt(birthyear));
            		stmt.setInt(5,starBirthYear);
            		stmt.setString(6,genre);
            		stmt.registerOutParameter(7, java.sql.Types.VARCHAR);
            		stmt.execute();
            		responseJsonObject.addProperty("message",stmt.getString(7));
        	        System.out.println(stmt.getString(7));
        	        out.write(responseJsonObject.toString());
            }
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
