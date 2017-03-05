package htmlprocessor;
import org.tartarus.snowball.ext.*;

import java.io.*;

/**
 * Created by Andrea on 03/03/2017.
 */
public abstract class Stemmer {

    /**
     * Given a file, it keeps the root of each word and returns the result.
     * @param path of the file.
     * @return text with only roots
     */
    public static String extractRoot(String path) throws IOException {

        String inputText = getText(path), outputText = "";
        String[] words = inputText.split("\n");
        spanishStemmer stemmer = new spanishStemmer();

        for ( String word: words){
            stemmer.setCurrent(word);
            if (stemmer.stem()) outputText += stemmer.getCurrent()+"\n";
        }

        return outputText;
    }

    /**
     * Given a text file, it returns its content
     * @param path of the file
     * @return text contained in the file
     * @throws IOException
     */
    private static String getText(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(path)));

        String outputText = "", line;

        while ( (line = br.readLine()) != null){
            outputText += line + "\n";
        }

        return outputText;
    }
}
