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
        ldaOption.inf = true; 
        ldaOption.dir = "~/LDA_Test/Model"; 
        ldaOption.modelName = "newdocs"; 
        ldaOption.niters = 100;
        ldaOption.dfile = "~/LDA_Test/user_tweets_fpl_from_twitter.csv";
        
        Inferencer inferencer = new Inferencer(); 
        inferencer.init(ldaOption);
        
       // Model newModel = inferencer.inference();
		
	}
    }

