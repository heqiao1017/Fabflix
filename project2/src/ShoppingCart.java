

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class ShoppingCart
 */
@WebServlet("/ShoppingCart")
public class ShoppingCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShoppingCart() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException 
    {
    		System.out.println("Entering shopping cart.java");
		HttpSession session = request.getSession();
		HashMap<String, Integer> previousItems = (HashMap<String,Integer>)session.getAttribute("previousItems");
		if (previousItems == null) {
			previousItems = new HashMap<String, Integer>();
			session.setAttribute("previousItems", previousItems);
		}
		
		String newItem = request.getParameter("title");//get the movie title
		if (newItem!=null) newItem = newItem.replaceAll(Pattern.quote("+"), " ");
		System.out.println("shopping cart: newItem: "+newItem);
		
		String qty = request.getParameter("qty");//get the movie title
		System.out.println("shopping cart: qty: "+qty);
		
//		String checkout = request.getParameter("checkout");//checkout:only need to display the result
//		System.out.println("shopping cart: checkout: "+checkout);
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JsonArray jsonArray = new JsonArray();
		
		synchronized(previousItems) {
			if (newItem!=null) {
				if (!previousItems.containsKey(newItem)) {
					previousItems.put(newItem, 1);
				}
				else {
					if (qty != null) {
						if (qty.equals("0") || qty.equals(""))
							previousItems.remove(newItem);
						else previousItems.put(newItem, Integer.valueOf(qty));
					}
					else previousItems.put(newItem, previousItems.get(newItem) + 1);
				}
				session.setAttribute("previousItems", previousItems);
			}

			if (previousItems.size() != 0) {
				for (HashMap.Entry<String, Integer> entry : previousItems.entrySet()) {
					JsonObject jsonObject = new JsonObject();
			        jsonObject.addProperty("movie_title", entry.getKey());
			        jsonObject.addProperty("movie_quantity", entry.getValue());
			        jsonArray.add(jsonObject);
				}

			}
		}
		
        out.write(jsonArray.toString());
		
}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
