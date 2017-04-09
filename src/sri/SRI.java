/**
 * Information Retrieval System
 * @Author: Andrea Serrano Urea
 */
package sri;
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

                for (int i = 0; i < Integer.parseInt(params.get(RELEVANT_DOCS)); i++){
                    doc = retrievedDocs.poll();
                    printResult(i+1,doc,params.get(DOCUMENTS_FOLDER));
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
    private static void printResult(int num, Pair<String,Double> document, String path) throws IOException {

        FileReader r = new FileReader(new File(path+"/"+document.getFirst().replace(".txt",".html")));
        BufferedReader br = new BufferedReader(r);

        String line;
        String text = "";
        while ( (line=br.readLine()) != null) {
            text += line;
        }

        Document html = Jsoup.parse(text);

        System.out.printf("Document #%d.\n", num);
        System.out.printf("\tSimilarity: %s %% \n",document.getSecond()*100);


        System.out.printf("\t%s\n",html.title());

        System.out.printf("\t<Frase que contiene la consulta>\n");

    }

}