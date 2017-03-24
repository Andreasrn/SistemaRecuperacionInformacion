package htmlprocessor;

import sri.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Andrea on 10/03/2017.
 */
public class Index {

    private HashMap<String, HashMap<String,Float>> indexByWords, indexByDocs;
    private HashSet<String> words, documents;

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
                    indexByWords.get(word).put(document.getName(),(float)1);
                } else if (indexByWords.containsKey(word) && !indexByWords.get(word).containsKey(document.getName())) {
                    indexByWords.get(word).put(document.getName(),(float)1);
                } else if (indexByWords.containsKey(word) && indexByWords.get(word).containsKey(document.getName()))  {
                    indexByWords.get(word).put(document.getName(), indexByWords.get(word).get(document.getName())+1);
                }


                if (!indexByDocs.get(document.getName()).containsKey(word)){
                    indexByDocs.get(document.getName()).put(word,(float)1.0);
                } else{
                    indexByDocs.get(document.getName()).put(word, indexByDocs.get(document.getName()).get(word)+1);
                }
            }

        }

        normalizeFreq();



    }

    /**
     * It normalizes the frequence of each word dividing by max freq in that document.
     */
    private void normalizeFreq(){
        float maxFreq;

        for (Map.Entry<String,HashMap<String,Float>> entry: indexByDocs.entrySet()) {
            maxFreq = 0;
            for (Map.Entry<String, Float> entry2 : entry.getValue().entrySet()) {
                if (entry2.getValue() > maxFreq) maxFreq = entry2.getValue();
            }

            for (Map.Entry<String, Float> entry2 : entry.getValue().entrySet()) {
                entry2.setValue(entry2.getValue()/maxFreq);
                indexByWords.get(entry2.getKey()).put(entry.getKey(),entry2.getValue());
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


}