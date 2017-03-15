/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wis4700;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import wis4700.jgibblda.Inferencer;
import wis4700.jgibblda.LDACmdOption;
import wis4700.jgibblda.Model;
/**
 *
 * @author rhys
 */
public class WIS4700 {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        LDACmdOption ldaOption = new LDACmdOption(); 
        ldaOption.est = true; 
        //ldaOption.inf = true;
        //ldaOption.alpha = (50 / ldaOption.K);
        //ldaOption.beta = 0.1;
        ldaOption.savestep = 100;
        ldaOption.twords = 20;
        ldaOption.dir = "/Users/rhys/LDA_Test/Model"; 
        ldaOption.modelName = "newdocs"; 
        ldaOption.niters = 10;
        ldaOption.dfile = "/../user_tweets_fpl_from_twitter.csv";
        
        FileReader fin = new FileReader("/Users/rhys/LDA_Test/sample_data.csv");
        FileWriter fout = new FileWriter("/Users/rhys/LDA_Test/sample_data_stopped.txt");
        BufferedReader in = new BufferedReader(fin);
        BufferedWriter out = new BufferedWriter(fout);
        while (in.ready()){
            String line = in.readLine();
            System.out.println(line);
            String stemmed = Stopwords.stemString(line);
            String stopped = Stopwords.removeStemmedStopWords(stemmed);
            out.write(stopped+"\n", 0, stopped.length());
            System.out.println(stopped);
        }
        out.flush();
        out.close();
        in.close();
        
        
        
        
       // Model newModel = new Model();
       // newModel.initNewModel(ldaOption);
        
        
        
        
        //newModel.saveModel(ldaOption.modelName);
        
        //Inferencer inferencer = new Inferencer(); 
        
        
        
        
       //Model newModel2 = inferencer.inference();
		
	}
    }

