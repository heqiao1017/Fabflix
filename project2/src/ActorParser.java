
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

public class ActorParser {
	private List<Star> Stars;
	Document dom;
	
    public ActorParser() {
    	Stars = new ArrayList<>();
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
            //dom = db.parse(completeFileName);
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

        NodeList nl = docEle.getElementsByTagName("actor");
        if (nl != null && nl.getLength() > 0) {
        		System.out.println("actor: "+nl.getLength());
            for (int i = 0; i < nl.getLength(); i++) {

	        		Element el = (Element) nl.item(i);
	        		Star star = getStar(el);
	        		Stars.add(star); 
            }
        }
    }
    
    private Star getStar(Element empEl) {
    		String name = getTextValue(empEl, "stagename");
        Integer year = getIntValue(empEl, "dob");
        Star star = new Star(null, name, year);

        return star;
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
            textVal = el.getTextContent();
        }

        return textVal;
    }
    private Integer getIntValue(Element ele, String tagName) {
    		try {
    			return Integer.parseInt(getTextValue(ele, tagName));
    		}
    		catch (NumberFormatException nfe)
    	    {
    	      System.out.println("NumberFormatException: " + nfe.getMessage());
    	      return null;
    	    }
        
    }
    public List<Star> getStars(String file) {
    		parseXmlFile(file);
    		parseDocument();
    		return Stars;
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

        System.out.println("No of movies '" + Stars.size() + "'.");

        Iterator<Star> it = Stars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }
    
	public static void main(String[] args) throws FileNotFoundException {
		PrintStream fileStream = new PrintStream("output1.txt");
		System.setOut(fileStream);
		ActorParser dpe = new ActorParser();
        dpe.runExample("actors63.xml");
	}
	

}

