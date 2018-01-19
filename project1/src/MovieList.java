

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.annotation.WebServlet;


/**
 * Servlet implementation class MovieList
 */
@WebServlet("/MovieList")
public class MovieList extends HttpServlet {
	public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    // Use http GET

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String loginUser = "qiaoh3";
        String loginPasswd = "zj@38971";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        response.setContentType("text/html"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        out.println("<HTML><HEAD><TITLE>MovieDB</TITLE></HEAD>");
        out.println("<BODY><H1>MovieDB</H1>");

        try {
            //Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            
            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = "SELECT title, year, director, GROUP_CONCAT(DISTINCT g.name) AS genres, GROUP_CONCAT(DISTINCT s.name) AS stars, rating FROM ratings r, stars s, stars_in_movies t, genres g, genres_in_movies e, movies m INNER JOIN (SELECT a.movieId AS mid FROM ratings a JOIN movies AS o ON a.movieId = o.id ORDER BY a.rating DESC LIMIT 20) AS v ON v.mid = m.id WHERE r.movieId = m.id AND m.id = t.movieId AND t.starId = s.id AND m.id = e.movieId AND e.genreId = g.id GROUP BY m.id, r.rating ORDER BY r.rating DESC";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            out.println("<TABLE border>");

            // Iterate through each row of rs
            while (rs.next()) {
                // String m_id = rs.getString("id");
                // String m_name = rs.getString("first_name") + " " + rs.getString("last_name");
                // String m_dob = rs.getString("dob");
              String m_title = rs.getString("title");
              String m_year = rs.getString("year");
              String m_director = rs.getString("director");
              String m_genres_list = rs.getString("genres");
              String m_star_list = rs.getString("stars");
              String m_rating = rs.getString("rating");
                // out.println("<tr>" + "<td>" + m_id + "</td>" + "<td>" + m_name + "</td>" + "<td>" + m_dob + "</td>"
                //         + "</tr>");
              out.println("<tr>" + "<td>" + m_title + "</td>" + "<td>" + m_year + "</td>" + "<td>" + m_director + "</td>"
                        + "<td>" + m_genres_list + "</td>" + "<td>" + m_star_list + "</td>" + "<td>" + m_rating + "</td>" + "</tr>");
            }

            out.println("</TABLE>");

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
}


