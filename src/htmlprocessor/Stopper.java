package htmlprocessor;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeSet;

import static java.awt.SystemColor.text;

/**
 * Created by Andrea on 24/02/2017.
 */
public class Stopper {

    private TreeSet<String> emptyWords;

    /**
     * Default constructor.
     */
    public Stopper() throws IOException {

        emptyWords = new TreeSet<>();

        FileReader r = new FileReader(new File("StopWords.txt"));
        BufferedReader br = new BufferedReader(r);

        String word;
        while ( (word = br.readLine()) != null) emptyWords.add(word);
    }

    /**
     * Given a file, this function returns its text without empty words
     * @param path path of the file which will be processed
     * @return text free of empty words
     */
    public String deleteEmptyWords(String path) throws IOException {
        String outputText = "";
        BufferedReader br = new BufferedReader(new FileReader(new File (path)));

        ArrayList<String> listOfWords = new ArrayList<>();
        String word;
        while ( (word = br.readLine()) != null ){
            if (!emptyWords.contains(word)) outputText += word + "\n";
        }

        return outputText;

    }
}
