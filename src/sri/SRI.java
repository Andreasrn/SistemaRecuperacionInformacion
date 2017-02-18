/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sri;

import htmlprocessor.HTMLFilter;
import java.io.IOException;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Andrea
 */
public class SRI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        HTMLFilter.getText("documents/es_26142.html");
        
    }
}
