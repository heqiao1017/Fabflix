

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class Checkout
 */
@WebServlet("/Checkout")
public class Checkout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Checkout() {
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
        
        String creditcard = request.getParameter("creditcard");
//        creditcard = creditcard.replaceAll(Pattern.quote("+"), " ");
        System.out.println("creditcard: "+ creditcard);
        
        String expiration = request.getParameter("expiration");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        
        
        response.setContentType("application/json");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {
//            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
//            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    
        	
        	
        	
        	
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            
           
        
        
        
        // Declare our statement
            Statement statement = dbcon.createStatement();
            String query = "select * from creditcards where id ='"+creditcard+"' and firstName='"+firstname+"' and lastName ='"+lastname+"' and expiration='"+expiration+"'";
            System.out.println(query);
            

            ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();
            
            //credit card validation fail
            if (!rs.next()){
            		out.write(jsonArray.toString());
                rs.close();
                statement.close();
                dbcon.close();
                return;
            	}
            System.out.println("line 84");
            
            //record the transaction in the sales table and then show the confirmation page
            int transaction_id = 0, customerId=0;
            query = "SELECT COUNT(*) as count FROM sales;";
            rs = statement.executeQuery(query);
            if(rs.next()){
                //System.out.println("transaction_id  before: " + rs.getString("count"));
            		transaction_id = Integer.valueOf(rs.getString("count"));//id
            }
            //System.out.println("transaction_id: " + transaction_id);
            
            //get session attribute
            HttpSession session = request.getSession();
            String email = ((User) session.getAttribute("user")).getUsername();//customer email
            String user_query = "select id from customers where email='" + email+"';";
            System.out.println("user_query:"+user_query);
            rs = statement.executeQuery(user_query);
            if(rs.next()){
            		customerId = Integer.valueOf(rs.getString("id"));//id
            }
            System.out.println("customerId: " + customerId);
            //int customerId = Integer.valueOf(statement.executeQuery(user_query).getString("id"));
            
      
            //get current data
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	    		LocalDate localDate = LocalDate.now();
	    		String saleDate = dtf.format(localDate);//date
	    		System.out.println("saleDate:"+saleDate);
	    		

	    		String movie_query, movie_id = null, sale_query;
	    		jsonArray = new JsonArray();
	    		
    			HashMap<String, Integer> previousItems = (HashMap<String,Integer>)session.getAttribute("previousItems");
    			if (previousItems != null) {
    				for (HashMap.Entry<String, Integer> entry : previousItems.entrySet()) {
    					movie_query = "select id from movies where title='"+entry.getKey()+"'";
    					System.out.println("movie_query:"+movie_query);
    					rs = statement.executeQuery(movie_query);
    					if (rs.next()) {
    						movie_id = rs.getString("id");//movie id
    					}
    					System.out.println("movie_id:"+movie_id);
    					
    				
    					
    				//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    					// Look up our data source
    			        DataSource ds_write = (DataSource) envCtx.lookup("jdbc/WriteDB");
    			        if (ds_write == null)
    			            out.println("ds_write is null.");
    			        Connection dbcon_write = ds.getConnection();
    			        if (dbcon_write == null)
    			            out.println("dbcon_write is null.");
    			        
    			        Statement write_statement = dbcon_write.createStatement();
    			        
    			        
    					sale_query = "INSERT INTO sales VALUES(default,"+customerId+",'"+movie_id+"'"+",'"+saleDate+"');";
    					System.out.println("sale_query:"+sale_query);
    					
    					//statement.executeUpdate(sale_query);
    					write_statement.executeUpdate(sale_query);
    				//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    					
    					
    					
    					transaction_id++;
    					//send sales table as json object back: sales_id, customer id, cusomer email, movieid, movies, salesdate;
	    	    			
	    	            JsonObject jsonObject = new JsonObject();
	    	            jsonObject.addProperty("movie_title", entry.getKey());
	    	            jsonObject.addProperty("movie_qty", String.valueOf(entry.getValue()));
	    	            jsonObject.addProperty("transaction_id", String.valueOf(transaction_id));
	    	            jsonObject.addProperty("customer_email", email);
	    	            System.out.println("movies:"+previousItems.toString());
	    	            //jsonObject.addProperty("movies", movies.toString().replace("[", "").replace("]", "").trim());
	    	            jsonObject.addProperty("sale_date", saleDate);
	    	                
	    	            jsonArray.add(jsonObject);
    				}
    			}
  
            
    			previousItems.clear();
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
