/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wis4700;

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
     */
    public static void main(String[] args) {
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
        
        Model newModel = new Model();
        newModel.initNewModel(ldaOption);
        
        newModel.saveModel(ldaOption.modelName);
        
        Inferencer inferencer = new Inferencer(); 
        
        
       //Model newModel2 = inferencer.inference();
		
	}
    }

