
public class Movie {
	private String id;
	private String title;
	private Integer year;
	private String director;
	private String genre_in_movie;
	
	public Movie() {
		 
	}
	public Movie(String id, String title, Integer year, String director,String genre_in_movie) {
		 this.id = id;
		 this.title = title;
		 this.year = year;
		 this.director = director;
		 this.genre_in_movie = genre_in_movie;
	}

	public String getGenre_in_movie() {
		return genre_in_movie;
	}
	public void setGenre_in_movie(String genre_in_movie) {
		this.genre_in_movie = genre_in_movie;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	@Override
	public String toString() {
		return "Movie [id=" + id + ", title=" + title + ", year=" + year + ", director=" + director
				+ ", genre_in_movie=" + genre_in_movie + "]";
	}
}
