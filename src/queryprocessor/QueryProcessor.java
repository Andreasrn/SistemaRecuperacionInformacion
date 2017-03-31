package queryprocessor;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.tartarus.snowball.ext.spanishStemmer;
import sri.ClaseSerializable;
import sri.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Andrea on 31/03/2017.
 */
public class QueryProcessor {
    private TreeSet<String> emptyWords;

    /**
     * Default constructor which needs the path of StopWords file to work.
     * @param stopWordFilePath
     * @throws IOException
     */
    public QueryProcessor(String stopWordFilePath) throws IOException {

        emptyWords = new TreeSet<>();

        FileReader r = new FileReader(new File(stopWordFilePath));
        BufferedReader br = new BufferedReader(r);

        String word;
        while ( (word = br.readLine()) != null) emptyWords.add(word);
    }

    /**
     * It takes a query and normalize it. Empty words are removed and roots are extracted.
     * @param query query to process
     * @return processed query
     */
    public String processQuery(String query) {

        String _query = query.toLowerCase();

        _query = StringUtils.stripAccents(_query);

        _query = _query.replaceAll("[^a-z0-9-_\\n]", " ");

        String output = "";

        spanishStemmer stemmer = new spanishStemmer();

        for (String word : _query.split(" ")) {
            if (!emptyWords.contains(word)) {
                stemmer.setCurrent(word);
                if (stemmer.stem()) output += stemmer.getCurrent() + " ";
            }
        }

        output.trim();

        return output;
    }

    public HashMap<String,Double> calculateWeights(String query) throws IOException, ClassNotFoundException {
        HashMap<String,Double> weights = new HashMap<>();
        HashMap<String,Double> freq = new HashMap<>();
        Double maxFreq = 0.0;
        ClaseSerializable<HashMap<String, HashMap <String, Pair<String,Double>>>> indexFile = new ClaseSerializable<>();

        HashMap<String, HashMap <String, Pair<String,Double>>> index = indexFile.leerObjeto("index.obj");

        for (String word: query.split(" ")){

            if (!freq.containsKey(word)) freq.put(word,1.0);
            else freq.put(word,freq.get(word)+1);

            if (freq.get(word) > maxFreq) maxFreq = freq.get(word);

        }

        for (Map.Entry<String,Double> word: freq.entrySet()){
            freq.put(word.getKey(), freq.get(word)/maxFreq);
        }

        for(Map.Entry<String,Double> word: freq.entrySet()){
            //weights.put(word.getKey(), word.getValue() * index.get(word).getSecond()); --> This should work when index changes

        }

        return weights;


    }
}
