/**
 * Information Retrieval System
 * @Author: Andrea Serrano Urea
 */
package sri;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import queryprocessor.QueryProcessor;

import java.io.*;

import java.util.*;
import org.jsoup.Jsoup;



public class SRI {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */

    public static void main(String[] args) throws Exception {


        ArrayList<String> params = loadParameters();
        int STOPWORDS_FILE = 0, DOCUMENTS_FOLDER = 1, PROCESSED_FOLDER = 2, STOPPER_FOLDER = 3, STEMMER_FOLDER = 4, RELEVANT_DOCS = 5;

        Scanner scan = new Scanner(System.in);
        String option;
        String query;
        QueryProcessor qp = new QueryProcessor("StopWords.txt");

        do {
            System.out.print("Select an option: \n");
            System.out.print("1. Generate index.\n");
            System.out.print("2. Send a query. \n");
            System.out.print("3. Exit. \n");

            option = scan.nextLine();

            if (option.equals("1")){
                File index = new File("index.obj");
                if (index.exists()){
                    System.out.print("There is already an index. Would you like to delete it and create another one? [y/n]\n");
                    option = scan.nextLine();

                    if (option.equals("y")){
                        index.delete();
                        CollectionProcessor.processCollection(params.get(STOPWORDS_FILE),params.get(DOCUMENTS_FOLDER),
                                                              params.get(PROCESSED_FOLDER),params.get(STEMMER_FOLDER),
                                                              params.get(STOPPER_FOLDER));
                    }

                } else {
                    CollectionProcessor.processCollection(params.get(STOPWORDS_FILE),params.get(DOCUMENTS_FOLDER),
                                                           params.get(PROCESSED_FOLDER),params.get(STEMMER_FOLDER),
                                                           params.get(STOPPER_FOLDER));
                }

            } else if (option.equals("2")){
                System.out.print("Enter your query: \n");
                query = scan.nextLine();

                query = qp.processQuery(query);

                HashMap<String,Double> queryWeights = qp.calculateWeights(query);

                PriorityQueue<Pair<String,Double>> retrievedDocs = qp.calculateSimilarity(queryWeights);

                Pair<String,Double> doc;

                System.out.println("Results:");

                int numDocuments = Integer.parseInt(params.get(RELEVANT_DOCS));

                if (numDocuments > retrievedDocs.size()) numDocuments = retrievedDocs.size();
                if (retrievedDocs.size() == 0) System.out.println("No documents were found.");

                for (int i = 0; i < numDocuments; i++){
                    doc = retrievedDocs.poll();
                    printResult(i+1,doc,params.get(DOCUMENTS_FOLDER),query);

                }



            }
        } while (!option.equals("3"));



    }

    /**
     * Returns an ArrayList which contains the parameters required for the execution. It contains folder paths or file names.
     * @return ArrayList with the parameters
     * @throws IOException
     */
    private static ArrayList<String> loadParameters() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("conf.data")));

        ArrayList<String> params = new ArrayList<>();

        String param;
        while ((param = br.readLine()) != null ){
            params.add(param);
        }

        return params;
    }


    /**
     * It shows on the screen info about the document retrieved.
     * @param num num of document
     * @param document Pair containing the name of the doc and similarity with the query
     * @param path path where original files are stored
     */
    private static void printResult(int num, Pair<String,Double> document, String path, String query) throws IOException {

        FileReader r = new FileReader(new File(path+"/"+document.getFirst().replace(".txt",".html")));
        BufferedReader br = new BufferedReader(r);

        String line;
        String text = "";
        while ( (line=br.readLine()) != null) {
            text += line;
        }

        Document html = Jsoup.parse(text);

        System.out.printf("Document #%d.\n", num);
        if (document.getSecond().isNaN()) System.out.printf("\tSimilarity: Not avaiable\n");
        else System.out.printf("\tSimilarity: %s %% \n",document.getSecond()*100);


        System.out.printf("\tTitle: %s\n",html.title());

        System.out.println();

        System.out.printf("\tSentence: %s\n", lookForSentenceWhichContains(query.split("\\s"),"documents/"+document.getFirst().replace(".txt",".html")));

    }

    /**
     * It returns the first ocurrence of a sentence containing words of the given query
     * @param words array with the words of the query
     * @param documentPath path of the document which contains some word of the query
     * @return sentence containing max and first match
     * @throws IOException
     */
    private static String lookForSentenceWhichContains(String[] words, String documentPath) throws IOException {

        File document = new File(documentPath);

        if (!document.exists()) throw new FileNotFoundException("File located at "+documentPath+" doesn't exist.\n");

        FileReader r = new FileReader(document);
        BufferedReader br = new BufferedReader(r);

        String line;
        String documentText = "";
        while ( (line=br.readLine()) != null) {
            documentText += line;
        }

        documentText = Jsoup.parse(documentText).text();

        String[] listOfSentences = documentText.split("\\.");
        HashMap<String,String> originalToNormalized = new HashMap<>();
        String original;

        for (String sentence: listOfSentences){

            original = sentence;

            sentence = sentence.toLowerCase();
            sentence = StringUtils.stripAccents(sentence);
            sentence = sentence.replaceAll("[^a-z0-9-._\\n]", " ");

            originalToNormalized.put(original,sentence);
        }

        int matches, maxMatches = 0;
        String output = "";

        for (Map.Entry<String,String> sentence: originalToNormalized.entrySet()){

            matches = 0;

            for (String word: words){
                if (sentence.getValue().contains(word)) matches++;
            }

            if (matches == words.length) return sentence.getKey();
            if (matches > maxMatches){
                maxMatches = matches;
                output = sentence.getKey();
            }
        }

        return output;

    }

}