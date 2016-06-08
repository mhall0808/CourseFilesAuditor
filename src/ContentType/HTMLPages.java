/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ContentType;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author hallm8
 */
public class HTMLPages extends CSV {

    public HTMLPages() {
        location = new String();
    }

    public HTMLPages(String location) {
        this.location = location;
    }

    /**
     * This function will open up the given HTML location, and parse through it,
     * discovering if there are any sort of links that use internal links.
     *
     * All we will do at this point is up a ticker, and add it to the list.
     */
    public void checkPage() {
        File input = new File(location);

        // Input.exists is a very useful file, I've noticed.
        if (input.exists()) {
            try {
                Document doc = Jsoup.parse(input, "UTF-8");
                //First, let's set up things until the body text.
                Elements hrefs = doc.getElementsByTag("a");

                for (Element href : hrefs) {
                    if (href.attr("href").contains("http:")
                            || href.attr("href").contains("https:")
                            || href.attr("href").contains(".html")) {
                        // Do nothing.
                    } else {
                        numBroken++;
                    }
                }

            } catch (IOException ex) {
                System.out.println("Error!! Unable to open file!");
            }
        } else {
            System.out.println("Tried to clean HTML and doesn't exist!!");
        }
        if (numBroken > 0){
         // Push to FireBase   
        }
    }
}
