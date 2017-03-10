package htmlprocessor;

import sri.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Andrea on 10/03/2017.
 */
public class Index {

    private HashMap<String, HashMap<String,Integer>> index;
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
        index = new HashMap<>();

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

            for (String word: listOfWords){
                words.add(word);

                if (!index.containsKey(word)){
                    index.put(word, new HashMap<>());
                    index.get(word).put(document.getName(),1);
                }

                index.get(word).put(document.getName(),index.get(word).get(document.getName()+1));
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
