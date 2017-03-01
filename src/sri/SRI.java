/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sri;

import htmlprocessor.HTMLProcessor;

import java.io.*;
import java.util.*;

import htmlprocessor.Stopper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;


/**
 *
 * @author Andrea
 */
public class SRI {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        /******************************VARIABLES DECLARATION******************************/

        String documentsPath = "documents";

        Scanner keyboard = new Scanner(System.in);
        String option;
        String[] mostFrequentWordsBefore, mostFrequentWordsAfter;
        Stopper stopper = new Stopper();
        int totalTokensAfter = 0;
        int totalTokensBefore = 0;
        float tokensPerFileBefore = 0;
        float tokensPerFileAfter = 0;
        int minTokensBe = 9999, maxTokensBe = 0, minTokensAf = 9999, maxTokensAf = 0;


        /***************************END VARIABLES DECLARATION***************************/


        System.out.println("Starting HTML processor...");

        do{
            System.out.println("Do you want to process the default folder (./documents)? [Y/N] ");
            option = keyboard.nextLine();
        } while (!option.equals("Y") && !option.equals("y") && !option.equals("N") && !option.equals("n"));

        if (option.equals("N") || option.equals("n")){
            System.out.println("Enter the path of the folder: ");
            documentsPath = keyboard.nextLine();
        }

        long time_start = System.currentTimeMillis(); //<--------We count the time starting from here.

        File documentsFolder = new File(documentsPath);
        checkIfFolderExists(documentsFolder);

        File processedFolder = new File("processed");
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


        ArrayList<String> mostFreqBe = mostFrequentWords("processed");

        File stopperFolder = new File("stopper");
        createOrEmptyFolder(stopperFolder);

        System.out.println("Erasing empty words...");

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

        ArrayList<String> mostFreqAf = mostFrequentWords("stopper");

        tokensPerFileAfter = (float) totalTokensAfter / numFiles;

        long time_end = System.currentTimeMillis() - time_start;
        System.out.println("Processing finished. You can find the new files in ./stopper folder. Exiting...");

        System.out.println("########################## STATS ##############################");

        tokensPerFileBefore = (float) totalTokensBefore / numFiles;
        System.out.println("Total time of processing (including I/O operations): "+ time_end / 1000.0 + " seconds.");
        System.out.printf("Total tokens obtained before applying Stopper: %s\nAverage tokens per file before applying Stopper: %s\n", totalTokensBefore, tokensPerFileBefore);
        System.out.printf("Total tokens obtained after applying Stopper: %s\nAverage tokens per file after applying Stopper: %s\n", totalTokensAfter, tokensPerFileAfter);

        System.out.print("Most frequent words before applying Stopper: ");
        for (String word: mostFreqBe) System.out.print(word+" ");

        System.out.println();

        System.out.print("Most frequent words after applying Stopper: ");
        for (String word: mostFreqAf) System.out.print(word+" ");
        System.out.println();

        System.out.printf("Max tokens contained in a document before processing: %s\nMin tokens contained in a document before processing: %s\n", maxTokensBe, minTokensBe);
        System.out.printf("Max tokens contained in a document after processing: %s\nMin tokens contained in a document after processing: %s\n", maxTokensAf, minTokensAf);
    }

    /**
     * Returns the 5 most frequent words in a given collection
     * @param path path where the documents are stored
     * @return array with the most frequent words
     */
    private static ArrayList mostFrequentWords(String path) throws IOException {
        PriorityQueue<Map.Entry> listOfWords = new PriorityQueue<>(10,(o1, o2) -> {
            return ((int) o2.getValue() - (int) o1.getValue());
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

        for (Map.Entry entry: mapOfWords.entrySet()){
            listOfWords.offer(entry);
        }

        for (int i = 0; i < 5; i++){
            outputList.add(listOfWords.poll().getKey());
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
