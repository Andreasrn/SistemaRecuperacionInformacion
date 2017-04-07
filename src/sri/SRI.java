/**
 * Information Retrieval System
 * @Author: Andrea Serrano Urea
 */
package sri;

import queryprocessor.QueryProcessor;

import java.io.File;
import java.lang.reflect.Parameter;
import java.util.*;



public class SRI {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */

    public static void main(String[] args) throws Exception {
        Boolean indexExist = false;
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
                        CollectionProcessor.processCollection();
                    }

                } else {
                    CollectionProcessor.processCollection();
                }

            } else if (option.equals("2")){
                System.out.print("Enter your query: \n");
                query = scan.nextLine();

                query = qp.processQuery(query);

                System.out.printf("You processed query is '%s'.\n",query);

                HashMap<String,Double> queryWeights = qp.calculateWeights(query);

                PriorityQueue<Pair<String,Double>> retrievedDocs = qp.calculateSimilarity(queryWeights);
                int numDocs =  retrievedDocs.size();
                Pair<String,Double> doc;

                System.out.println("Results:");

                for (int i = 0; i < numDocs; i++){
                    doc = retrievedDocs.poll();
                    System.out.printf("%s -> %s\n",doc.getFirst(),doc.getSecond());
                }



            }
        } while (!option.equals("3"));



    }

}