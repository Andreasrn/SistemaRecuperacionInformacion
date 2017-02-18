/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htmlprocessor;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 *
 * @author Andrea
 */
public abstract class HTMLFilter {
    
    public static void getText(String path) throws IOException{
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8");
        
        String content = "";
        
        Elements elements = doc.getElementsByTag("title");
        for (Element element: elements){
            content += element.text()+' ';
        }
        
        elements = doc.getElementsByTag("link");
        for (Element element: elements){
            content += element.attr("title");
        }
        
        System.out.print(content);
        
    }

}
