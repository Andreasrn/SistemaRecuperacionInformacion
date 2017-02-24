package htmlprocessor;

import java.io.*;
import java.util.TreeSet;

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
     * Given a text, this function returns the same text without empty words
     * @param text text which will be processed
     * @return text free of empty words
     */
    public String deleteEmptyWords(String text){
        String outputText = "";

        String[] listOfWords = text.split("\n");

        for (String word: listOfWords){
            if (!emptyWords.contains(word)) outputText += word + "\n";
        }

        return outputText;

    }
}
