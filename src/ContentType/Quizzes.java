/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ContentType;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author hallm8
 */
public class Quizzes extends CSV{
    
    
    
    
    public void quizCheck(){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(location);
            
            // Normalize the document.  
            doc.getDocumentElement().normalize();
            
            // Looks like a piece of cake!  mattext contains any body text.
            NodeList bodyText = doc.getElementsByTagName("mattext");
            
            for (int i = 0; i < bodyText.getLength(); i++){           
                String text = bodyText.item(i).getTextContent();
                System.out.println(text);
                org.jsoup.nodes.Document jDoc = Jsoup.parse(text);
                
            }
            
            
            
            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            System.out.println("Error! Unable to find item!");
            Logger.getLogger(Quizzes.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
}
