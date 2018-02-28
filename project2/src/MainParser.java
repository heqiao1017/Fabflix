import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MainParser {
	int count = 0;
	private List<Movie> Movies;
//	private Map<Movie> Movies;
	Document dom;
	
    public MainParser() {
    		Movies = new ArrayList<>();
//    		Movies = new HashMap<>();
    }
    private void parseXmlFile(String completeFileName) {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            InputStream inputStream= new FileInputStream(completeFileName);
            InputSource is = new InputSource(inputStream);
            is.setEncoding("ISO-8859-1");

            //parse using builder to get DOM representation of the XML file
//            dom = db.parse(completeFileName);
            dom = db.parse(is);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private void parseDocument() {
    		String director="";
    		
    		
        //get the root elememt
        Element docEle = dom.getDocumentElement();

        NodeList nl = docEle.getElementsByTagName("directorfilms");
        if (nl != null && nl.getLength() > 0) {
        		//System.out.println("directorfilms: "+nl.getLength());
        		//loop through all the directorfilms
            for (int i = 0; i < nl.getLength(); i++) {

	        		Element el = (Element) nl.item(i);
	        		
	        		//director list
	        		NodeList directors = el.getElementsByTagName("director");
	        		if (directors != null && directors.getLength() > 0) {
	        			Element dir = (Element) directors.item(0);
	        			director  = getDirectorName(dir);
	        		}
	        		
	        		
	        		//films
	        		NodeList films = el.getElementsByTagName("film");
	        		if (films != null && films.getLength() > 0) {
	        			for (int j = 0; j < films.getLength(); j++) {
	        				Element film_el = (Element) films.item(j);
	        				Movie movie = getMovie(film_el, director);
	        				Movies.add(movie);
	        			}
	        		} 
            }
        }
    }
    
    private String getDirectorName(Element empEl) {
    		return getTextValue(empEl, "dirname");
    }
    
    private Movie getMovie(Element empEl, String director) {
    		String id = getTextValue(empEl, "fid");
        String title = getTextValue(empEl, "t");
        Integer year = getIntValue(empEl, "year");
        String genre = getTextValue(empEl, "cat");

        Movie movie = new Movie(id, title, year, director,genre);

        return movie;
    }
    
    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl == null) {
        		System.out.println("tagName: "+tagName);
        		return null;
        }
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            //textVal = el.getFirstChild().getNodeValue();
            textVal = el.getTextContent();
        }

        return textVal;
    }
    private Integer getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
    		try {
    			return Integer.parseInt(getTextValue(ele, tagName));
    		}
    		catch (NumberFormatException nfe)
    	    {
    	      System.out.println("NumberFormatException: " + nfe.getMessage());
    	      return null;
    	    }
        
    }
    
    public List<Movie> getMovies(String file) {
    	 	parseXmlFile(file);

         //get each employee element and create a Employee object
         parseDocument();
         
         return Movies;
    }
    
    public void runExample(String file) {

        //parse the xml file and get the dom object
        parseXmlFile(file);

        //get each employee element and create a Employee object
        parseDocument();

        //Iterate through the list and print the data
        printData();

    }
    
    private void printData() {

        System.out.println("No of movies '" + Movies.size() + "'.");

        Iterator<Movie> it = Movies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }
    
	public static void main(String[] args) throws FileNotFoundException {
		PrintStream fileStream = new PrintStream("output0.txt");
		System.setOut(fileStream);
		// TODO Auto-generated method stub
		MainParser dpe = new MainParser();

        //call run example
        dpe.runExample("mains243.xml");
	}
	

}
