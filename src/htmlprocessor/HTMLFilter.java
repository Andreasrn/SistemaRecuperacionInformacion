/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htmlprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Andrea
 */
public abstract class HTMLFilter {

    /**
     * Deletes the head tag of an HTML file
     *
     * @param path File with the tag we want to remove
     */
    public static void removeHead(String path){
        String fileName = path.substring(10);

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            PrintWriter printer = new PrintWriter(new FileWriter("documents\\" + "NOTAG" + fileName));
            
            Boolean deleteContent = false;
            String line = br.readLine();
            
            while (line != null) {
                if (line.equals("<head>")) {
                    deleteContent = true;
                } else if (line.equals("</head>")) {
                    deleteContent = false;
                }

                if (!deleteContent && !line.equals("</head>")) {
                    printer.println(line);
                }

                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /**
     * Deletes the DOCTYPE part of an HTML file
     * @param path File whose doctype part will be removed
     */
    public static void removeDoctype(String path){
        String fileName = path.substring(10);

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            PrintWriter printer = new PrintWriter(new FileWriter("documents\\" + "NOTAG" + fileName));
            String line = br.readLine();
            
            while (line != null) {
                if (!line.contains("<!DOCTYPE")) {
                    printer.println(line);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void printFile(String path) throws IOException{

            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            String line = br.readLine();
            
            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }
    }
    
    /**
     * Keeps only the content in body tag
     * @param path path of the file
     */
    public static void removeAllButBody(String path){
        String fileName = path.substring(10);

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            PrintWriter printer = new PrintWriter(new FileWriter("documents\\" + "NEW-" + fileName));
            
            Boolean deleteContent = true;
            String line = br.readLine();
                                
            while (line != null) {
                if (line.startsWith("<body")) {
                    deleteContent = false;
                } else if (line.equals("</body>")) {
                    deleteContent = true;       
                }

                if (!deleteContent && !line.contains("<body")) {
                    printer.flush();
                    printer.println(line);
                }

                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
