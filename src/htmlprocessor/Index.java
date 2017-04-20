package htmlprocessor;

import sri.ClaseSerializable;
import sri.Pair;

import java.io.*;
import java.util.*;

/**
 * Created by Andrea on 10/03/2017.
 */
public class Index {

    private HashMap<String, HashMap<String,Double>> indexByWords, indexByDocs;
    private HashSet<String> words, documents;
    private HashMap<String,Pair<Double,HashMap<String, Double>>> weights;

    sri.Pair<String,Integer> biggestDoc, smallestDoc;

    /**
     * Constructor.
     * @param collection path od the folder containing the collection
     */
    public Index(String collection) throws Exception {

        int maxTok = 0, minTok = 99999999;
        smallestDoc = new Pair<>(null,null);
        biggestDoc = new Pair<>(null,null);

        words = new HashSet<>();
        documents = new HashSet<>();
        indexByWords = new HashMap<>();
        indexByDocs = new HashMap<>();

        File folder = new File(collection);

        if (!folder.exists()) throw new Exception("INDEX: Folder 'collection' doesn't exist.");

        String text;
        for (File document: folder.listFiles()){
            text = getText(document.getPath());

            String[] listOfWords = text.split("\n");

            if (listOfWords.length > maxTok){
                maxTok = listOfWords.length;
                biggestDoc.setFirst(document.getName());
                biggestDoc.setSecond(maxTok);
            }

            if (listOfWords.length < minTok){
                minTok = listOfWords.length;
                smallestDoc.setFirst(document.getName());
                smallestDoc.setSecond(minTok);
            }

            documents.add(document.getName());

            indexByDocs.put(document.getName(),new HashMap<>());

            for (String word: listOfWords){
                words.add(word);

                if (!indexByWords.containsKey(word)){
                    indexByWords.put(word, new HashMap<>());
                    indexByWords.get(word).put(document.getName(),1.0);
                } else if (indexByWords.containsKey(word) && !indexByWords.get(word).containsKey(document.getName())) {
                    indexByWords.get(word).put(document.getName(),1.0);
                } else if (indexByWords.containsKey(word) && indexByWords.get(word).containsKey(document.getName()))  {
                    indexByWords.get(word).put(document.getName(), indexByWords.get(word).get(document.getName())+1);
                }


                if (!indexByDocs.get(document.getName()).containsKey(word)){
                    indexByDocs.get(document.getName()).put(word,1.0);
                } else{
                    indexByDocs.get(document.getName()).put(word, indexByDocs.get(document.getName()).get(word)+1);
                }
            }

        }

        normalizeFreq();

        calculateWeights();

        ClaseSerializable<HashMap<String,Pair<Double,HashMap<String,Double>>>> output = new ClaseSerializable<>(weights);
        output.escribirObjeto("index");


    }

    /**
     * It normalizes the frequency of each word dividing by max freq in that document.
     */
    private void normalizeFreq(){
        double maxFreq;

        for (Map.Entry<String,HashMap<String,Double>> entry: indexByDocs.entrySet()) {
            maxFreq = 0;
            for (Map.Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                if (entry2.getValue() > maxFreq) maxFreq = entry2.getValue();
            }

            for (Map.Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                entry2.setValue(entry2.getValue()/maxFreq);
                if (!Double.isNaN(entry2.getValue())){
                    indexByWords.get(entry2.getKey()).put(entry.getKey(),entry2.getValue());
                } else {
                    indexByWords.get(entry2.getKey()).put(entry.getKey(),0.0);
                }

            }
        }


    }

    /**
     * Returns the number of documents in the collection.
     * @return number of documents
     */
    public int getSizeOfCollection(){
        return documents.size();
    }

    /**
     * Returns the number of different words in the whole collection
     * @return number of words
     */
    public int getSizeOfDictionary() { return words.size(); }

    /**
     * Returns a pair containing both the document which contains more words, and the amount of them.
     * @return pair with document,number of words
     */
    public sri.Pair<String,Integer> getBiggestDocument(){
        return biggestDoc;
    }

    /**
     * Returns a pair containing both the document which contains less words, and the amount of them.
     * @return pair with document,number of words
     */
    public sri.Pair<String, Integer> getSmallestDocument() { return smallestDoc; }

    /**
     * Returns the text of a given file.
     * @param path path of the file whose text will be returned
     * @return text contained in file
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

    /**
     * It creates the table containing the weight of each word in each document
     */
   private void calculateWeights(){

        int NUM_DOCS = getSizeOfCollection();

        weights = new HashMap<>();

        double df;
        double idf;
        double sumCuadrado = 0;
        double norma = 0;

        for (Map.Entry<String,HashMap<String,Double>> word: indexByWords.entrySet()){

            if (word.getKey().equals("mund")) System.out.println("MUND");

            df = word.getValue().entrySet().size();

            idf = (float) Math.log10(NUM_DOCS/df);

            if (word.getKey().equals("mund")) System.out.println("IDF: "+idf);

            weights.put(word.getKey(),new Pair(idf, new HashMap<>()));

            if (word.getKey().equals("mund")) System.out.println("Peso: "+weights.get(word.getKey()).getFirst());

            sumCuadrado = 0;

            for (Map.Entry<String,Double> document: word.getValue().entrySet()){
                weights.get(word.getKey()).getSecond().put(document.getKey(), document.getValue() * idf);

                sumCuadrado += Math.pow(weights.get(word.getKey()).getSecond().get(document.getKey()),2);
            }

            if (word.getKey().equals("mund")) System.out.println("Suma cuadrado: "+sumCuadrado);

            norma = (float) Math.sqrt(sumCuadrado);


            if (!Double.isNaN(norma)){
                for (Map.Entry<String,Double> document: word.getValue().entrySet()){
                    weights.get(word.getKey()).getSecond().put(document.getKey(),weights.get(word.getKey()).getSecond().get(document.getKey())/norma);

                }
            } else {
                for (Map.Entry<String,Double> document: word.getValue().entrySet()){
                    if (Double.isNaN(norma)) norma = 0;
                    weights.get(word.getKey()).getSecond().put(document.getKey(),0.0);

                }
            }


        }


   }

}
