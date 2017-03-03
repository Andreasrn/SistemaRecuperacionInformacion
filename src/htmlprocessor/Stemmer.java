package htmlprocessor;
import org.tartarus.snowball.ext.*;

import java.util.ArrayList;

/**
 * Created by Andrea on 03/03/2017.
 */
public abstract class Stemmer {
    public static String extractRoot(String text){

        String outputText = "";
        String[] words = text.split("\n");
        spanishStemmer stemmer = new spanishStemmer();

        for ( String word: words){
            stemmer.setCurrent(word);
            if (stemmer.stem()) outputText += stemmer.getCurrent()+"\n";
        }

        return outputText;
    }
}
