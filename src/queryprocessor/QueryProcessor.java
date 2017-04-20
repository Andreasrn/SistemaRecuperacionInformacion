package queryprocessor;

import org.apache.commons.lang3.StringUtils;
import org.tartarus.snowball.ext.spanishStemmer;
import sri.ClaseSerializable;
import sri.Pair;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Andrea on 31/03/2017.
 */
public class QueryProcessor {
    private TreeSet<String> emptyWords;
    private HashMap<String, Pair<Double,HashMap<String,Double>>> index;

    /**
     * Default constructor which needs the path of StopWords file to work. It loads in memory the index.
     * @param stopWordFilePath
     * @throws IOException
     */
    public QueryProcessor(String stopWordFilePath) throws IOException, ClassNotFoundException {

        emptyWords = new TreeSet<>();

        FileReader r = new FileReader(new File(stopWordFilePath));
        BufferedReader br = new BufferedReader(r);

        String word;
        while ( (word = br.readLine()) != null) emptyWords.add(word);

        ClaseSerializable<HashMap<String, Pair<Double,HashMap<String,Double>>>> indexFile = new ClaseSerializable<>();

        System.out.print("Loading index...\n");
        index = indexFile.leerObjeto("index.obj");
        System.out.print("Index loaded.\n");


    }

    /**
     * It takes a query and normalize it. Empty words are removed and roots are extracted.
     * @param query query to process
     * @return processed query
     */
    public String processQuery(String query) {

        System.out.println("Your query was "+query+'.');

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

        System.out.println("Your query will be treated as "+output+'.');

        return output;
    }


    /**
     * It returns a map associating weights to each word of the query
     * @param query query whose weights are calculated
     * @return Map <Word,Weight>
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public HashMap<String,Double> calculateWeights(String query) throws IOException, ClassNotFoundException {

        System.out.println("Calculating weights of the query...");

        HashMap<String,Double> weights = new HashMap<>();
        HashMap<String,Double> freq = new HashMap<>();
        Double maxFreq = 0.0;


        for (String word: query.split(" ")){

            if (!freq.containsKey(word)) freq.put(word,1.0);
            else freq.put(word,freq.get(word)+1);

            if (freq.get(word) > maxFreq) maxFreq = freq.get(word);

        }


        for (Map.Entry<String,Double> word: freq.entrySet()){
            freq.put(word.getKey(), freq.get(word.getKey())/maxFreq);
        }

        for(Map.Entry<String,Double> word: freq.entrySet()){

            if (index.containsKey(word.getKey())){
                weights.put(word.getKey(), word.getValue() * index.get(word.getKey()).getFirst());
            } else {
                weights.put(word.getKey(), 0.0);
            }


        }

        System.out.println("Weights calculated.");
        Double s = index.get("univers").getFirst();
        return weights;

    }

    public PriorityQueue<Pair<String,Double>> calculateSimilarity(HashMap<String,Double> queryWeights){
        System.out.println("Calculating similarities with documents...");

        PriorityQueue<Pair<String, Double>> output = new PriorityQueue<>(10, (o1,o2) -> {

            Double result = o2.getSecond() - o1.getSecond();

            if (result == 0) return 0;
            if (result > 0) return 1;

            return -1;

        });

        File documentsFolder = new File("stemmer");
        String doc;
        double numerator;
        double sumQueryWeights = 0;
        double sumDocWeights = 0;

        for (Double weight: queryWeights.values()){
            sumQueryWeights += Math.pow(weight,2);
        }

        sumQueryWeights = Math.sqrt(sumQueryWeights);

        for (File document: documentsFolder.listFiles()){

            numerator = 0;

            doc = document.getName();
            sumDocWeights = 0;

            for (String wordInQuery: queryWeights.keySet()){
                if (index.containsKey(wordInQuery)){
                    if (index.get(wordInQuery).getSecond().containsKey(doc)){

                        numerator += (index.get(wordInQuery).getSecond().get(doc) * queryWeights.get(wordInQuery));

                        sumDocWeights += Math.pow(index.get(wordInQuery).getSecond().get(doc),2);
                    }
                }


            }

            sumDocWeights = Math.sqrt(sumDocWeights);

            if (sumDocWeights != 0 && !Double.isNaN(numerator) && !Double.isNaN(sumDocWeights)) output.offer(new Pair<>(doc, numerator / (sumDocWeights*sumQueryWeights)));
            else output.offer(new Pair<>(doc,0.0));


        }

        return output;
    }
}
