/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htmlprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Andrea
 */
public abstract class HTMLProcessor {
    
    /**
     * Given an HTML file, it extracts its plain text
     * @param path path of the file
     * @return Plain text
     * @throws IOException 
     */
    private static String filter(String path) throws IOException{
        FileReader r = new FileReader(new File(path));
        BufferedReader br = new BufferedReader(r);
        
        String line;
        String text = "";
        while ( (line=br.readLine()) != null) {
            text += line;
        }
        
        return Jsoup.parse(text).text();
    }
    
    /**
     * Given a text, this function returns the same text without capital letters,
     * accents or strange characters. It also puts a '\n' between every word.
     * @param text input text
     * @return normalized text
     */
    private static String normalizeText(String text){

        String outputText = "";

        text = text.toLowerCase();

        text = StringUtils.stripAccents(text);

        text = text.replaceAll("[^a-z0-9-_\\n]", " ");

        String[] listOfWords = text.split("\\s+");

        for (String word: listOfWords){
            outputText += word;
            if (!outputText.endsWith("\n")) outputText += "\n";
        }

        return outputText;

    }

    /**
     * Given a file, it filters and normalizes it. It returns the normalized text.
     * @param path path of the original file
     */
    public static String process(String path){

        try {
            String text = filter(path);
            text = normalizeText(text);

            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
    
}
