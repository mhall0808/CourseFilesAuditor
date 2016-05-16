/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coursefilesauditor;

import ContentType.CSV;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author hallm8
 */
public class Manifest {

    String CSVFile = new String();
    String CSVName = new String();
    String manifestLocation = new String();
    ArrayList<CSV> CSVList = new ArrayList<>();
    ArrayList<String> HTMLLinks = new ArrayList<>();
    String OrgUnitID = new String();
    int totalBroken = 0;

    public Manifest() {

    }

    public Manifest(String newLocation) {
        manifestLocation = newLocation;
    }

    public void gatherCSV() {
        // First, we have to crack open the DOM parser and pull out some info.
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(manifestLocation);

            // Normalize the document.  
            doc.getDocumentElement().normalize();

            // Find the root element.  Necessary to find the Course ID.
            Element root = (Element) doc.getElementsByTagName("manifest").item(0);
            OrgUnitID = root.getAttribute("identifier").replace("D2L_", "");
            System.out.println("OU Found! : " + OrgUnitID);

            // We have the OUID, so now we can link to the website.
            CSVFile += "https://byui.brightspace.com/d2l/home/" + OrgUnitID + "\n";
            // Now we have enough to make line 2 as well.
            CSVFile += "'Module', 'Content Page Name'\n";

            // Well, that was all the easy stuff, unfortunately.  So here we go!
            NodeList resources = doc.getElementsByTagName("resource");
            for (int i = 0; i < resources.getLength(); i++) {
                Element resource = (Element) resources.item(i);
                if ((resource.getAttribute("d2l_2p0:material_type").equals("content")
                        || resource.getAttribute("d2l_2p0:material_type").equals("contentlink"))) //Do nothing
                {
                    if (resource.getAttribute("href").contains("http:\\")
                            || resource.getAttribute("href").contains("https:\\")
                            || resource.getAttribute("href").contains(".html")) {
                        
                    }
                    else{
                        totalBroken++;
                        System.out.println("Resource identifier: " + resource.getAttribute("identifier"));
                    }
                } // Now that we know everything that our files do NOT contain, we
                // can sort the ones that are probably problematic.  We will 
                // deal with HTML files later.
                else {

                }

            }

            System.out.println(totalBroken);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(Manifest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    public void writeCSV() {
        try (PrintWriter out = new PrintWriter(CSVName + ".csv")) {
            out.println(CSVFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Manifest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
