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
import java.util.Set;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CastParser {
	
	private List<String> movieIds;
	private List<String> actors;
	Document dom;
	
    public CastParser() {
    		movieIds = new ArrayList<>();
    		actors = new ArrayList<>();
    }
    
    private void parseXmlFile(String completeFileName) {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            
//            InputStream inputStream= new FileInputStream(completeFileName);
//            InputSource is = new InputSource(inputStream);
//            is.setEncoding("ISO-8859-1");

            //parse using builder to get DOM representation of the XML file
            dom = db.parse(completeFileName);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    

    private void parseDocument() {
		String movieId, actor;
		
	    //get the root elememt
	    Element docEle = dom.getDocumentElement();
	
	    NodeList nl = docEle.getElementsByTagName("filmc");

	    if (nl != null && nl.getLength() > 0) {
	    		//loop through all the <filmc>
	        for (int i = 0; i < nl.getLength(); i++) {
	  
	        	
	        		NodeList ml = ((Element) nl.item(i)).getElementsByTagName("m");
	        		
	        		if (ml != null && ml.getLength() > 0) {
	        			
	        			movieId = "";
	        
	        			for (int j = 0; j < ml.getLength(); j++) {
	        				
	        				Element m_el = (Element) ml.item(j);
	        				
        					Element movieId_el = (Element) m_el.getElementsByTagName("f").item(0);
        					movieId = movieId_el.getTextContent();
	        		
	        				Element actor_el = (Element) m_el.getElementsByTagName("a").item(0);
	        				actor = actor_el.getTextContent();
	        				
	        				movieIds.add(movieId);
	        				actors.add(actor);
	        			}
	        		}
	        }
	    }
    }
     
    
    public void runCastParser(String file) {

        //parse the xml file and get the dom object
        parseXmlFile(file);

        //get each employee element and create a Employee object
        parseDocument();

        //Iterate through the list and print the data
//        printData();

    }
    
    public List<String> getMovieIds() {
    		return movieIds;
    }
    public List<String> getActors() {
		return actors;
    }
    
    private void printData() {
    	
        Iterator<String> m_it = movieIds.iterator();
        Iterator<String> a_it = actors.iterator();
        while (m_it.hasNext()) {
            System.out.println("movieId: " + m_it.next() + "  actor: " + a_it.next());
        }
        
        System.out.println("No of movieIds '" + movieIds.size() + "'.");
        System.out.println("No of actors '" + actors.size() + "'.");
    }

	public static void main(String[] args) throws FileNotFoundException {
		PrintStream fileStream = new PrintStream("output2.txt");
		System.setOut(fileStream);
		// TODO Auto-generated method stub
		CastParser dpe = new CastParser();

        //call run example
        dpe.runCastParser("casts124.xml");
        dpe.printData();
	}

}
