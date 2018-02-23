import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImportDataToDatabase {
	//following are all data from the database
	public static Map<String, Movie> movieHash = new HashMap<>();//key: title id
	public static Map<String, Integer> genreHash = new HashMap<>();//key: genre name, val : genre id
	public static Map<String, HashSet<Integer>> genre_in_movie_Hash = new HashMap<>();//key: movie id |  val: set of genres name
	
	public static Map<String, HashMap<String, Star>> starHash = new HashMap<>();//key: star name
	public static Map<String, HashSet<String>> star_in_movie_Hash = new HashMap<>();//movie  id, stars ids
	public static Map<String, String> star_id_name_map = new HashMap<>();;
	
	//following are all parsed data
	public static List<Movie> Movies;
	public static List<Star> Stars;
	
	public static List<String> movieIds;
	public static List<String> actorNames;
	
	public static String starMax="";
	public static String starIdPrefix="";
	public static Integer starMaxInt;
	public static Integer genreMaxInt;
	
	
	//for parsed data
	public static List<String> pair_of_movieId = new ArrayList<>();
	public static List<String> pair_of_genreName = new ArrayList<>();
	
	public static Map<String, HashSet<Movie>> parsed_movie_Hash = new HashMap<>();//key : movie id
	public static Set<String> pased_movie_id_hash = new HashSet<>();
	public static Map<String, Integer> parsed_genre_Hash = new HashMap<>();
	//public static Map<String, Integer> parsed_genre_name_id_map = new HashMap<>();//to get genreid
	public static Map<String, HashSet<Integer>> parsed_genre_in_movie_Hash = new HashMap<>();//val id; key: genre_id check for dup, check
	

	public static Map<String, HashMap<String, Star>> parsed_star_Hash = new HashMap<>();//key: new generated star id
	public static Map<String, HashSet<String>> parsed_star_in_movie_Hash = new HashMap<>();
	
	public static Set<Star> stars_to_insert = new HashSet<>();
	
	
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException {
//		PrintStream fileStream = new PrintStream("output.txt");
//		System.setOut(fileStream);
		MainParser mp = new MainParser();
		ActorParser ap = new ActorParser();
		CastParser cp = new CastParser();
		
		Movies = mp.getMovies("mains243.xml");
		Stars = ap.getStars("actors63.xml");
		cp.runCastParser("casts124.xml");
		movieIds = cp.getMovieIds();
		actorNames = cp.getActors();
		
		
		Connection conn = null;

        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
            String jdbcURL="jdbc:mysql://localhost:3306/moviedb";
            conn = DriverManager.getConnection(jdbcURL,"mytestuser", "mypassword");
            Statement statement = conn.createStatement();
            
            //query movies table to get all the movies in the database
            String query = "select * from movies;";
            ResultSet rs = statement.executeQuery(query);
            String id = "", title="",director="";
            Integer year = null;
            while (rs.next()) {  
            		id = rs.getString("id");
            		title = rs.getString("title");
            		year = Integer.parseInt(rs.getString("year"));
            		director = rs.getString("director");
            		movieHash.put(id, new Movie(id, title.toLowerCase().trim(), year, director.toLowerCase().trim(), null));
            }
            
            //query genre table to get all the genres in the database
            query = "select * from genres;";
            rs = statement.executeQuery(query);
            String name="";
            Integer genre_id = null;
            while (rs.next()) {  
            		genre_id = Integer.parseInt(rs.getString("id"));
	        		name = rs.getString("name");
	        		genreHash.put(name.toLowerCase().trim(), genre_id);
            }
            
            //query genres_in_movies table to get all the genres_in_movies in the database
            query = "select * from genres_in_movies;";
            rs = statement.executeQuery(query);
            while (rs.next()) {  
            		genre_id = Integer.parseInt(rs.getString("genreId"));
	        		id = rs.getString("movieId");
	        		if (!genre_in_movie_Hash.isEmpty() && genre_in_movie_Hash.containsKey(id)) {
	        			genre_in_movie_Hash.get(id).add(genre_id);
	        			continue;
	        		}
	        		genre_in_movie_Hash.put(id, new HashSet<Integer>());
	        		genre_in_movie_Hash.get(id).add(genre_id);
            }
            
            //query stars table to get all the stars in the database
            query = "select * from stars;";
            rs = statement.executeQuery(query);
            while (rs.next()) {  
            		id = rs.getString("id");
            		name = rs.getString("name");
            		if (rs.getString("birthYear") == null)
            			year = null;
            		else
            			year = Integer.parseInt(rs.getString("birthYear"));
            		if (!starHash.isEmpty() && starHash.containsKey(name)) {
            			HashMap<String, Star> map = starHash.get(name);
            			if (!map.containsKey(id)) {
            				starHash.get(name).put(id, new Star(id, name.toLowerCase().trim(), year));
            				continue;
            			}
            		}
            		starHash.put(name, new HashMap<String, Star>());
            		starHash.get(name).put(id, new Star(id, name.toLowerCase().trim(), year));
            }
            
            //query stars_in_movies table get all the stars_in_movies in the database
            query = "select * from stars_in_movies;";
            rs = statement.executeQuery(query);
            String starId = "";
            while (rs.next()) {  
            		starId = rs.getString("starId");
	        		id = rs.getString("movieId");
	        		if (!star_in_movie_Hash.isEmpty() && star_in_movie_Hash.containsKey(id)) {
	        			star_in_movie_Hash.get(id).add(starId);
	        			continue;
	        		}
	        		star_in_movie_Hash.put(id, new HashSet<String>());
	        		star_in_movie_Hash.get(id).add(starId);
            }
            
            
            query = "select max(id) from stars;";
            rs = statement.executeQuery(query);
            while (rs.next()) { 
            		starMax = rs.getString("max(id)");
            }
            starIdPrefix = starMax.substring(0, 2);
            starMaxInt = Integer.parseInt(starMax.substring(2));
            
            query = "select max(id) from genres;";
            rs = statement.executeQuery(query);
            while (rs.next()) { 
            		genreMaxInt = Integer.parseInt(rs.getString("max(id)"));
            }
            System.out.println("genreMaxInt"+genreMaxInt);
            
	    		PrintStream fileStream = new PrintStream("output.txt");
	    		System.setOut(fileStream);
            //------------------------>from here, using bash insert to insert all the parsed data into the database
            conn.setAutoCommit(false);
            
            PreparedStatement psInsertRecord=null;
            String sqlInsertRecord=null;
            int[] iNoRows=null;
            
            sqlInsertRecord="insert into movies values(?,?,?,?)";
            psInsertRecord=conn.prepareStatement(sqlInsertRecord);
            //========================================================================
            //insert into movies
	    		for (Movie movie : Movies) {
	    			if (movie.getId()==null || movie.getTitle() == null || movie.getYear() == null || movie.getDirector() == null) {
	    				System.out.println("Invalid movies with one of the fields are null");
    					continue;
	    			}
	    			String mtitle = movie.getTitle().toLowerCase().trim();
	    			if (movie.getGenre_in_movie()!=null && !movie.getGenre_in_movie().equals("") && !movie.getId().equals("")) {
	    				pair_of_movieId.add(movie.getId());//unique
	    				pair_of_genreName.add(movie.getGenre_in_movie().toLowerCase().trim());
	    			}
	    			
	    			//check for dup of db
	    			if (movieHash.containsKey(movie.getId())) {
	    				System.out.println("Duplicate movies in moviedb! Where movieId = "+movie.getId());
    					continue;
	    			}
//	    			System.out.println(movie.getId());
//	    			System.out.println(movie.getDirector());
//	    			System.out.println(movieHash.get(movie.getId()));
	    			if (movieHash.get(movie.getId()) != null && movieHash.get(movie.getId()).getTitle() != null && mtitle.equals(movieHash.get(movie.getId()).getTitle()) && 
	    					movie.getDirector().toLowerCase().trim().equals(movieHash.get(movie.getId()).getDirector())
	    					&& movie.getYear().equals(movieHash.get(movie.getId()).getYear())) {
	    				System.out.println("Duplicate movies in moviedb! Where movieId = "+movie.getId());
    					continue;
	    			}
	    			if (!pased_movie_id_hash.isEmpty() && pased_movie_id_hash.contains(movie.getId())) {
	    				System.out.println("Duplicate movies in moviedb! Where movieId = "+movie.getId());
    					continue;
	    			}
	    			pased_movie_id_hash.add(movie.getId());
	    			
	    			//check for dup of parsed
	    			if (!parsed_movie_Hash.isEmpty() && parsed_movie_Hash.containsKey(mtitle)) {
	    				HashSet<Movie> movies = parsed_movie_Hash.get(mtitle);
	    				boolean find  = false;
	    				for (Movie m : movies) {
	    					if(m.getDirector() != null && movie.getDirector().toLowerCase().trim().equals(m.getDirector())
		    						&& m.getYear()!=null && movie.getYear().equals(m.getYear())) {
		    					System.out.println("Duplicate movies in moviedb! Where movieId = "+movie.getId());
		    					find = true;
		    					break;
		    				}
	    				}
	    				if (!find) {
	    					parsed_movie_Hash.get(mtitle).add(movie);
	    					
	    					psInsertRecord.setString(1, movie.getId());
		    	    			psInsertRecord.setString(2, movie.getTitle());
		    	    			psInsertRecord.setInt(3, movie.getYear());
		    	    			psInsertRecord.setString(4,movie.getDirector());
		    	    			psInsertRecord.addBatch();
	    				}
	    				continue;
	    			}
	    			parsed_movie_Hash.put(mtitle, new HashSet<Movie>());
	    			parsed_movie_Hash.get(mtitle).add(movie);
	    			
	    			psInsertRecord.setString(1, movie.getId());
	    			psInsertRecord.setString(2, movie.getTitle());
	    			psInsertRecord.setInt(3, movie.getYear());
	    			psInsertRecord.setString(4,movie.getDirector());
	    			psInsertRecord.addBatch();
	    		}
	    		
	    		iNoRows=psInsertRecord.executeBatch();
	    		
			//========================================================================
	    		//reset PreparedStatement, and insert into genres
	    		psInsertRecord=null;
	    		sqlInsertRecord="insert into genres values(DEFAULT,?)";
	    		iNoRows=null;
	    		psInsertRecord=conn.prepareStatement(sqlInsertRecord);
	    		
	    		for (Movie movie : Movies) {
	    			if (movie.getId()==null || movie.getTitle() == null || movie.getYear() == null || movie.getDirector() == null) {
    					continue;
	    			}
	    			if (movie.getGenre_in_movie() == null) {
	    				System.out.println("Invalid genre is null");
	    				continue;
	    			}
	    			String genre = movie.getGenre_in_movie().split("\\s+")[0];
	    			
	    			if (genreHash.containsKey(genre.toLowerCase().trim())) {
	    				System.out.println("Duplicate genre = "+genre);
	    				continue;
	    			}
	    			if (!parsed_genre_Hash.isEmpty() && parsed_genre_Hash.containsKey(genre.toLowerCase().trim())) {
	    				System.out.println("Duplicate genre = "+genre);
	    				continue;
	    			}
	    			parsed_genre_Hash.put(genre.toLowerCase().trim(),++genreMaxInt);
	    			
	    			psInsertRecord.setString(1, genre);
	    			psInsertRecord.addBatch();
	    		}
	    		iNoRows=psInsertRecord.executeBatch();
	    		

	    		//insert into genres_in_movies
	    		psInsertRecord=null;
	    		sqlInsertRecord="insert into genres_in_movies values(?,?)";
	    		iNoRows=null;
	    		psInsertRecord=conn.prepareStatement(sqlInsertRecord);
	    		int size = pair_of_movieId.size();
	    		Integer tmp;//genre id
	    		for (int i = 0; i < size; i++) {
	    			if (genreHash.containsKey(pair_of_genreName.get(i))) {
	    				tmp = genreHash.get(pair_of_genreName.get(i));
	    			}
	    			else {
	    				tmp = parsed_genre_Hash.get(pair_of_genreName.get(i));
	    			}
	    			if (!pased_movie_id_hash.contains(pair_of_genreName.get(i)) && !movieHash.containsKey(pair_of_genreName.get(i))) {
	    				continue;
	    			}
	    			if (tmp == null) {
	    				continue;
	    			}
	    			if (genre_in_movie_Hash.containsKey(pair_of_movieId.get(i)))  {
	    				if (genre_in_movie_Hash.get(pair_of_movieId.get(i)).contains(tmp)) {
	    					System.out.println("Duplicate genre = "+pair_of_genreName.get(i) + " in movie = "+pair_of_movieId.get(i));
	    					continue;
	    				}
	    			}
	    			if (!parsed_genre_in_movie_Hash.isEmpty() && parsed_genre_in_movie_Hash.containsKey(pair_of_movieId.get(i))) {
	    				if (parsed_genre_in_movie_Hash.get(pair_of_movieId.get(i)).contains(tmp)) {
	    					System.out.println("Duplicate genre = "+pair_of_genreName.get(i) + " in movie = "+pair_of_movieId.get(i));
	    				}
	    				else {
	    					parsed_genre_in_movie_Hash.get(pair_of_movieId.get(i)).add(tmp);
	    					psInsertRecord.setInt(1, tmp);
		    	    			psInsertRecord.setString(2, pair_of_movieId.get(i));
		    	    			psInsertRecord.addBatch();
	    				}
	    				continue;
	    			}
	    			parsed_genre_in_movie_Hash.put(pair_of_movieId.get(i), new HashSet<Integer>());
	    			parsed_genre_in_movie_Hash.get(pair_of_movieId.get(i)).add(tmp);
	    			
	    			psInsertRecord.setInt(1, tmp);
	    			psInsertRecord.setString(2, pair_of_movieId.get(i));
	    			psInsertRecord.addBatch();
	    		}
	    		iNoRows=psInsertRecord.executeBatch();
	    		
	    		
	    		//insert into stars
	    		psInsertRecord=null;
	    		sqlInsertRecord="insert into stars values(?,?,?)";
	    		iNoRows=null;
	    		psInsertRecord=conn.prepareStatement(sqlInsertRecord);
	    		
	    		for (Star star : Stars) {
	    			String starN = star.getName();
	    			Integer y = star.getBirthYear();
	    			if (starN == null) {
	    				System.out.println("Invalid star is null");
	    				continue;
	    			}
	    			
	    			//check dup in db
	    			if (starHash.containsKey(starN)) {
	    				boolean find = false;
	    				HashMap<String, Star> map = starHash.get(starN);
	    				for (Map.Entry<String,Star> entry : map.entrySet()) {
	    					Star s = entry.getValue();
	    					if (s.getName().equals(starN)) {
	    						if (y == null && s.getBirthYear()==null || (y != null && s.getBirthYear().equals(y))) {
	    							System.out.println("Duplicate star: "+starN+" when inserting in stars table");
		    						find = true;
				    				break;
	    						}
	    						
	    					}
	    				}
	    				if (find) {
	    					continue;
	    				}
	    			}
	    			
	    			if (!parsed_star_Hash.isEmpty() && parsed_star_Hash.containsKey(starN)) {
	    				boolean find = false;
	    				HashMap<String, Star> map = parsed_star_Hash.get(starN);
	    				for (Map.Entry<String,Star> entry : map.entrySet()) {
	    					Star s = entry.getValue();
	    					if (s.getName().equals(starN)) {
	    						if (y== null && s.getBirthYear() == null || (y != null && s.getBirthYear()!= null && s.getBirthYear().equals(y))){
		    						System.out.println("Duplicate star: "+starN+" when inserting in stars table");
		    						find = true;
				    				break;
	    						}
	    					}
	    				}
	    				if (!find) {
	    					starMaxInt++;
	    					String sId = starIdPrefix+starMaxInt;
	    					star.setId(sId);
	    					parsed_star_Hash.get(starN).put(sId, star);
	    					psInsertRecord.setString(1, star.getId());
		    	    			psInsertRecord.setString(2, starN);
		    	    			if (star.getBirthYear() == null)
		    	    				psInsertRecord.setNull(3, Types.INTEGER);
		    	    			else psInsertRecord.setInt(3, star.getBirthYear());
		    	    			psInsertRecord.addBatch();
	    				}
	    				continue;
	    			}
	    			
	    			starMaxInt++;
	    			String sId = starIdPrefix+starMaxInt;
	    			star.setId(sId);
	    			parsed_star_Hash.put(starN, new HashMap<String, Star>());
	    			parsed_star_Hash.get(starN).put(sId, star);
	    			
	    			psInsertRecord.setString(1, star.getId());
	    			psInsertRecord.setString(2, starN);
	    			if (star.getBirthYear() == null)
	    				psInsertRecord.setNull(3, Types.INTEGER);
	    			else psInsertRecord.setInt(3, star.getBirthYear());
	    			psInsertRecord.addBatch();
	    		}
	    		iNoRows=psInsertRecord.executeBatch();
	    		
	    		//insert into stars_in_movies
	    		psInsertRecord=null;
	    		sqlInsertRecord="insert into stars_in_movies values(?,?)";
	    		iNoRows=null;
	    		psInsertRecord=conn.prepareStatement(sqlInsertRecord);
	    		size = actorNames.size();
	    		String starN="", mID="", sid = null;

	    		for (int i = 0; i < size; i++) {
	    			boolean find = false;
	    			starN = actorNames.get(i);
	    			mID = movieIds.get(i);
	    			//need to get star id
	    			if (starHash.containsKey(starN)) {
	    				HashMap<String, Star> map = starHash.get(starN);
	    				for (Map.Entry<String,Star> entry : map.entrySet()) {
	    					sid = entry.getKey();
	    					find = true;
		    				break;
	    				}
	    			}
	    			
	    			if (find == false && parsed_star_Hash.containsKey(starN)) {
	    				HashMap<String, Star> map = parsed_star_Hash.get(starN);
	    				for (Map.Entry<String,Star> entry : map.entrySet()) {
	    					sid = entry.getKey();
	    					find = true;
		    				break;
	    				}
	    			}
	    			
	    			if (find ==false) {
	    				continue;
	    				//insert star into star stable
//	    				starMaxInt++;
//		    			String sId = starIdPrefix+starMaxInt;
//		    			Star newS = new Star(sId, starN, null);
//		    			stars_to_insert.add(newS);
//	    				parsed_star_Hash.put(starN, new HashMap<String, Star>());
//	    				parsed_star_Hash.get(starN).put(sId, newS);
//	    				sid = sId;
	    			}
	    			
	    			if (!pased_movie_id_hash.contains(mID) && !movieHash.containsKey(mID)) {
	    				continue;
	    			}
	    			
	    			if (star_in_movie_Hash.containsKey(mID)) {
	    				if (star_in_movie_Hash.get(mID).contains(sid)) {
	    					System.out.println("Duplicate starId = "+sid+ " in movieId = "+mID);
	    					continue;
	    				}
	    			}
	    			
	    			if (!parsed_star_in_movie_Hash.isEmpty() && parsed_star_in_movie_Hash.containsKey(mID)) {
	    				if (parsed_star_in_movie_Hash.get(mID).contains(sid)) {
	    					System.out.println("Duplicate starId = "+sid+ " in movieId = "+mID);	
	    				}
	    				else {
	    					parsed_star_in_movie_Hash.get(mID).add(sid);
	    					psInsertRecord.setString(1, sid);
		    	    			psInsertRecord.setString(2, mID);
		    	    			psInsertRecord.addBatch();
	    				}
	    				continue;
	    			}
	    			
	    			parsed_star_in_movie_Hash.put(mID, new HashSet<String>());
	    			parsed_star_in_movie_Hash.get(mID).add(sid);
	    			psInsertRecord.setString(1, sid);
	    			psInsertRecord.setString(2, mID);
	    			psInsertRecord.addBatch();
	    		}
	    		
	    		iNoRows=psInsertRecord.executeBatch();
	    		
	    		
	    		//insert into stars
//	    		psInsertRecord=null;
//	    		sqlInsertRecord="insert into stars values(?,?,?)";
//	    		iNoRows=null;
//	    		psInsertRecord=conn.prepareStatement(sqlInsertRecord);
//	    		for (Star s : stars_to_insert) {
//	    			psInsertRecord.setString(1, s.getId());
//	    			psInsertRecord.setString(2, s.getName());
//	    			if (s.getBirthYear() == null)
//	    				psInsertRecord.setNull(3, Types.INTEGER);
//	    			else psInsertRecord.setInt(3, s.getBirthYear());
//	    			psInsertRecord.addBatch();
//	    		}
//	    		iNoRows=psInsertRecord.executeBatch();
	    		
	    		conn.commit();
	    		if(psInsertRecord!=null) psInsertRecord.close();
	        if(conn!=null) conn.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
			

	}

}
