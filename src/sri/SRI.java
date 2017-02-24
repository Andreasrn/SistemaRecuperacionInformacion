/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sri;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import htmlprocessor.HTMLProcessor;

import java.io.*;
import java.util.*;

import htmlprocessor.Stopper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;


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

        long time_start, time_end;
        String documentsPath = "documents";
        Scanner keyboard = new Scanner(System.in);
        String option;
        int totalTokensBefore = 0;
        float tokensPerFileBefore = 0;
        float tokensPerFileAfter = 0;
        String[] mostFrequentWordsBefore, mostFrequentWordsAfter;
        int numFiles = 0;
        Stopper stopper = new Stopper();
        int totalTokensAfter = 0;


        /*******************************************************************************/


        System.out.println("Starting HTML processor...");

        do{
            System.out.println("Do you want to process the default folder (./documents)? [Y/N] ");
            option = keyboard.nextLine();
        } while (!option.equals("Y") && !option.equals("y") && !option.equals("N") && !option.equals("n"));

        if (option.equals("N") || option.equals("n")){
            System.out.println("Enter the path of the folder: ");
            documentsPath = keyboard.nextLine();
        }

        File documents = new File(documentsPath);
        if (!documents.exists()){
            System.out.println("The folder "+documentsPath+" doesn't exist. Exiting...");
            System.exit(0);
        }


        time_start = System.currentTimeMillis();

        File folder = new File("processed");
        if (!folder.exists()) folder.mkdir();
        else FileUtils.cleanDirectory(folder);



        File[] listOfDocuments = documents.listFiles();
        numFiles = listOfDocuments.length;

        String text;
        System.out.println("Processing documents...");
        for (File file: listOfDocuments) {
            text = HTMLProcessor.process(file.getPath());
            //text = stopper.deleteEmptyWords(text);

            File archive = new File(folder.getPath() + '/' + file.getName());
            FileUtils.writeStringToFile(archive, text, "UTF-8");

            totalTokensBefore += StringUtils.countMatches(text,"\n");
        }

        time_end = System.currentTimeMillis() - time_start;
        System.out.println("Processing finished. You can find the new files in ./processed folder. Exiting...");

        tokensPerFileBefore = (float) totalTokensBefore / numFiles;
        System.out.println("Total time of processing (including I/O operations): "+ time_end / 1000.0 + " seconds.");
        System.out.printf("Total tokens obtained: %s\nAverage tokens per file: %s", totalTokensBefore, tokensPerFileBefore);

    }

    /**
     * Returns the 5 most frequent words in a given collection
     * @param path path where the documents are stored
     * @return array with the most frequent words
     */
    private String[] mostFrequentWords(String path) throws IOException {
        return null;
    }
}
