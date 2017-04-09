package sri;

import htmlprocessor.HTMLProcessor;
import htmlprocessor.Index;
import htmlprocessor.Stemmer;
import htmlprocessor.Stopper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by Andrea on 31/03/2017.
 */
public abstract class CollectionProcessor {

    /**
     * Generates a collection given a set of html files.
     * @param stopWords path of the file containing words to be removed
     * @param docs path of the original files of the collection
     * @param processed path where processed docs are stored
     * @param stop path where files without stop words are stored
     * @throws Exception
     */
    public static void processCollection(String stopWords, String docs, String processed,String stemmer, String stop) throws Exception {
        /******************************VARIABLES DECLARATION******************************/

        int totalTokensAfter = 0;
        int totalTokensBefore = 0;
        float tokensPerFileBefore = 0;
        float tokensPerFileAfter = 0;
        int minTokensBe = 9999, maxTokensBe = 0, minTokensAf = 9999, maxTokensAf = 0;

        int totalTokensStemmer = 0;
        float tokensPerFileStemmer = 0;
        int minTokensStemmer = 9999, maxTokensStemmer = 0;


        /***************************END VARIABLES DECLARATION***************************/

        System.out.println("Starting HTML processor...");

        long time_start = System.currentTimeMillis(); //<--------We count the time starting from here.

        File documentsFolder = new File(docs);
        checkIfFolderExists(documentsFolder);

        File processedFolder = new File(processed);
        createOrEmptyFolder(processedFolder);

        File[] listOfDocuments = documentsFolder.listFiles();
        int numFiles = listOfDocuments.length;

        System.out.println("Filtering and normalizing documents...");

        String text;
        for (File file: listOfDocuments) {
            text = HTMLProcessor.process(file.getPath());

            File filteredFile = new File(processedFolder.getPath() + '/' + file.getName().replace(".html",".txt"));

            int tokens = StringUtils.countMatches(text,"\n");

            if (tokens < minTokensBe) minTokensBe = tokens;
            if (tokens > maxTokensBe) maxTokensBe = tokens;


            FileUtils.writeStringToFile(filteredFile, text, "UTF-8");
            totalTokensBefore += tokens;
        }


        ArrayList<sri.Pair<String,Integer>> mostFreqBe = mostFrequentWords(processed);

        File stopperFolder = new File(stop);
        createOrEmptyFolder(stopperFolder);

        System.out.println("Erasing empty words...");

        Stopper stopper = new Stopper(stopWords);

        listOfDocuments = processedFolder.listFiles();
        for (File file: listOfDocuments){
            text = stopper.deleteEmptyWords(file.getPath());

            File archive = new File( stopperFolder.getPath() + '/' + file.getName());
            FileUtils.writeStringToFile(archive, text, "UTF-8");

            int tokens = StringUtils.countMatches(text,"\n");

            if (tokens < minTokensAf) minTokensAf = tokens;
            if (tokens > maxTokensAf) maxTokensAf = tokens;
            totalTokensAfter += tokens;
        }

        ArrayList<sri.Pair<String,Integer>> mostFreqAf = mostFrequentWords(stop);

        File stemmerFolder = new File(stemmer);
        createOrEmptyFolder(stemmerFolder);

        System.out.println("Extracting roots...");

        listOfDocuments = stopperFolder.listFiles();
        for (File file: listOfDocuments){
            text = Stemmer.extractRoot(file.getPath());

            File archive = new File(stemmerFolder.getPath() + "/" + file.getName());

            FileUtils.writeStringToFile(archive, text, "UTF-8");

            int tokens = StringUtils.countMatches(text,"\n");

            if (tokens < minTokensStemmer) minTokensStemmer = tokens;
            if (tokens > maxTokensStemmer) maxTokensStemmer = tokens;

            totalTokensStemmer += tokens;
        }

        ArrayList<sri.Pair<String,Integer>> mostFreqStemmer = mostFrequentWords(stemmer);


        System.out.println("Generating index...");
        Index index = new Index(stemmer);

        tokensPerFileAfter = (float) totalTokensAfter / numFiles;

        long time_end = System.currentTimeMillis() - time_start;
        System.out.println("Processing finished. You can find the new files in " + stemmer+" folder. Exiting...");

        System.out.println("########################## STATS ##############################\n");

        tokensPerFileBefore = (float) totalTokensBefore / numFiles;

        System.out.println("General\n");
        System.out.println("-Total time of processing (including I/O operations): "+ time_end / 1000.0 + " seconds.");
        System.out.println("Normalizing stage\n");
        System.out.printf("-Total tokens obtained: %s\n-Average tokens per file: %s\n", totalTokensBefore, tokensPerFileBefore);
        System.out.print("-Most frequent words: ");
        for (int i = 0; i < mostFreqBe.size(); i++){
            System.out.printf("%s(%d times) ",mostFreqBe.get(i).getFirst(),mostFreqBe.get(i).getSecond());
        }
        System.out.println();
        System.out.printf("-Max tokens contained in a document: %s\n-Min tokens contained in a document: %s\n", maxTokensBe, minTokensBe);
        System.out.println();

        System.out.println("Stopper stage\n");
        System.out.printf("-Total tokens obtained: %s\n-Average tokens per file: %s\n", totalTokensAfter, tokensPerFileAfter);
        System.out.print("-Most frequent words: ");
        for (int i = 0; i < mostFreqAf.size(); i++){
            System.out.printf("%s(%d times) ",mostFreqAf.get(i).getFirst(),mostFreqAf.get(i).getSecond());
        }
        System.out.println();
        System.out.printf("-Max tokens contained in a document: %s\n-Min tokens contained in a document: %s\n", maxTokensAf, minTokensAf);
        System.out.println();

        System.out.println("Stemmer stage\n");

        tokensPerFileStemmer = totalTokensStemmer / (float)numFiles;
        System.out.printf("-Total tokens obtained: %s\n-Average tokens per file: %s\n", totalTokensStemmer, tokensPerFileStemmer);
        System.out.print("-Most frequent words: ");
        for (int i = 0; i < mostFreqStemmer.size(); i++){
            System.out.printf("%s(%d times) ",mostFreqStemmer.get(i).getFirst(),mostFreqStemmer.get(i).getSecond());
        }
        System.out.println();
        System.out.printf("-Max tokens contained in a document: %s\n-Min tokens contained in a document: %s\n", maxTokensStemmer, minTokensStemmer);
        System.out.println();

        System.out.println("Collection information\n");

        System.out.printf("-Collection size: %s\n-Number of different words: %s\n",index.getSizeOfCollection(),index.getSizeOfDictionary());
        System.out.printf("-Largest document: %s containing %s words.\n", index.getBiggestDocument().getFirst(),index.getBiggestDocument().getSecond());
        System.out.printf("-Smallest document: %s containing %s words.\n", index.getSmallestDocument().getFirst(),index.getSmallestDocument().getSecond());
        System.out.println();
    }


    /**
     * Returns the 5 most frequent words in a given collection
     * @param path path where the documents are stored
     * @return array with the most frequent words
     */
    private static ArrayList<sri.Pair<String,Integer>> mostFrequentWords(String path) throws IOException {
        PriorityQueue<Pair<String, Integer>> listOfWords = new PriorityQueue<>(10,(o1, o2) -> {
            return ((int) o2.getSecond() - (int) o1.getSecond());
        });

        HashMap<String,Integer> mapOfWords = new HashMap<>();

        BufferedReader br;
        String word;
        ArrayList outputList = new ArrayList();

        File[] listOfFiles = new File(path).listFiles();

        for (File file: listOfFiles){
            br = new BufferedReader(new FileReader(file));

            while ( (word = br.readLine()) != null) {
                if (mapOfWords.containsKey(word)) {
                    mapOfWords.put(word, mapOfWords.get(word) + 1);
                } else {
                    mapOfWords.put(word, 1);
                }
            }
        }

        for (Map.Entry<String,Integer> entry: mapOfWords.entrySet()){
            sri.Pair<String,Integer> tuple = new sri.Pair<String,Integer>(entry.getKey(),entry.getValue());
            listOfWords.offer(tuple);
        }

        for (int i = 0; i < 5; i++){
            outputList.add(new sri.Pair<String,Integer>(listOfWords.peek().getFirst(),listOfWords.poll().getSecond()));
        }

        return outputList;


    }

    /**
     * It checks whether a folder exists or not. If it doesn't, the program ends.
     * @param folder folder to be checked
     */
    private static void checkIfFolderExists(File folder){
        if (!folder.exists()){
            System.out.println("Folder "+folder.getName()+" doesn't exist. Exiting...");
            System.exit(0);
        }
    }

    /**
     * It creates a new folder, or erases everything inside an existing one.
     * @param folder folder to be created or emptied
     * @throws IOException
     */
    private  static void createOrEmptyFolder(File folder) throws IOException {
        if (!folder.exists()) folder.mkdir();
        else FileUtils.cleanDirectory(folder);
    }

}
