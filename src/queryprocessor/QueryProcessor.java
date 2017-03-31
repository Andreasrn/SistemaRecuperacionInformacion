package queryprocessor;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.tartarus.snowball.ext.spanishStemmer;

import java.io.*;
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

    public String processQuery(String query) {
        String _query = Jsoup.parse(query).text();

        _query = _query.toLowerCase();

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

        System.out.printf("La consulta inicial era: %s\nLa consulta ahora es: %s", query,output);
        return output;
    }
}
