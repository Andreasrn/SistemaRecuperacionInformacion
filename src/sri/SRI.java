/**
 * Information Retrieval System
 * @Author: Andrea Serrano Urea
 */
package sri;

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

        System.out.print("Select an option: \n");
        System.out.print("1. Generate index.\n");
        System.out.print("2. Send a query. \n");



        CollectionProcessor.processCollection();


    }

}