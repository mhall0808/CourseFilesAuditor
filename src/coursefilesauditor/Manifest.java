/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coursefilesauditor;

import ContentType.CSV;
import ContentType.Content;
import ContentType.HTMLPages;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author hallm8
 */
public class Manifest {

    String className = new String();
    String OrgUnitID = new String();
    String manifestLocation = new String();
    int totalBroken = 0;
    
    ArrayList<CSV> CSVList = new ArrayList<>();
    
    /**
     * 
     */
    public Manifest() {

    }

    /**
     * 
     * @param newLocation 
     */
    public Manifest(String newLocation) {
        manifestLocation = newLocation;
    }

    /**
     * 
     */
    public void gatherCSV() {
        // First, we have to crack open the DOM parser and pull out some info.
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(manifestLocation + "\\imsmanifest.xml");

            // Normalize the document.  
            doc.getDocumentElement().normalize();

            // Find the root element.  Necessary to find the Course ID.
            Element root = (Element) doc.getElementsByTagName("manifest").item(0);
            OrgUnitID = root.getAttribute("identifier").replace("D2L_", "");
            System.out.println("OU Found! : " + OrgUnitID);

            // Well, that was all the easy stuff, unfortunately.  So here we go!
            NodeList resources = doc.getElementsByTagName("resource");
            NodeList modules = doc.getElementsByTagName("item");

            
            for (int i = 0; i < resources.getLength(); i++) {
                Element resource = (Element) resources.item(i);
                switch (resource.getAttribute("d2l_2p0:material_type")) {
                    case "content":
                    case "contentlink":
                        if (resource.getAttribute("href").contains("http:")
                                || resource.getAttribute("href").contains("https:")
                                || resource.getAttribute("href").contains("/d2l/common/dialogs/quickLink/quickLink.d2l?ou={orgUnitId}")) {
                        } /**
                         * For this, we are going to just go ahead and parse all
                         * html files listed. Narrowing this allows us to see each
                         * HTML node, which we will then link to and use.
                         */
                        else if (resource.getAttribute("href").contains(".html")) {
                            HTMLPages html = new HTMLPages(manifestLocation + 
                                    resource.getAttribute("href"));
                            Element module = (Element) modules.item(i);
                            ArrayList<String> nNameAndLocation = new ArrayList<>();
                            nNameAndLocation.add(module.getTextContent());
                            nNameAndLocation = findModules(module, nNameAndLocation);
                            html.setNameAndLocation(nNameAndLocation);
                            CSVList.add(html);
                            
                        } else {
                            // Now that we know everything that our files do NOT contain, we
                            // can sort the ones that are probably problematic.  We will
                            // deal with HTML files later.
                            totalBroken++;
                            System.out.println("Resource identifier: " + resource.getAttribute("identifier"));
                            Content content = new Content();
                            Element module = (Element) modules.item(i);
                            ArrayList<String> nNameAndLocation = new ArrayList<>();
                            nNameAndLocation.add(module.getTextContent());
                            nNameAndLocation = findModules(module, nNameAndLocation);
                            content.setNameAndLocation(nNameAndLocation);
                            CSVList.add(content);
                        }   break;
                    case "d2lquiz":
                        break;
                }
            }
            
            for (CSV brokenList : CSVList) {
                brokenList.gatherBroken();
                totalBroken += brokenList.getNumBroken();
            }
            
            
            System.out.println(totalBroken);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(Manifest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * FIND MODULES This is a recursive function that cycles through modules
     * until it reaches the root node. This is done to list the module location
     * of the links used.
     *
     * @return
     */
    private ArrayList<String> findModules(Node moduleNode, ArrayList<String> newList) {
        newList.add(moduleNode.getTextContent());
        System.out.println("module name: " + moduleNode.getChildNodes().item(1).getTextContent());
        if (moduleNode.getParentNode().getNodeName().equals("")) {
            newList = findModules(moduleNode.getParentNode(), newList);
        }
        return newList;
    }
}
