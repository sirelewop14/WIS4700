/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wis4700;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import wis4700.jgibblda.Estimator;
import wis4700.jgibblda.LDACmdOption;

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
        //ES Database Connection
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch_rhys").build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        //Call for data from ES
        SearchResponse resp1 = client.prepareSearch("epl").setFrom(0).setSize(80000).get();
        SearchHits hits = resp1.getHits();
        SearchHit[] hitArray = hits.getHits();
        
        //Parse data from ES and write out to file
        FileWriter fout = new FileWriter("/Users/rhys/LDA_Test/sample_data_stopped.txt");
        BufferedWriter out = new BufferedWriter(fout);
        String size = Integer.toString(hitArray.length);
        out.write(size, 0, size.length());
        out.newLine();
        for (int i = 0; i < hitArray.length; i++) {
            String line = "";
            try{
                line = hitArray[i].getSource().get("Message").toString();
            } catch (Exception e){
                System.out.println(e);
                System.out.println("At Index: "+i);
            }
            //Stem and remove stop words
            String stemmed = Stopwords.stemString(line);
            String stopped = Stopwords.removeStemmedStopWords(stemmed);
            out.write(stopped, 0, stopped.length());
            out.newLine();
        }
        out.flush();
        out.close();
        //LDA Create new model with settings
        LDACmdOption ldaOption = new LDACmdOption(); 
        ldaOption.est = true; 
        //ldaOption.inf = true;
        //ldaOption.alpha = (50 / ldaOption.K);
        //ldaOption.beta = 0.1;
        ldaOption.savestep = 500;
        ldaOption.twords = 10;
        ldaOption.dir = "/Users/rhys/LDA_Test/Model"; 
        ldaOption.modelName = "newdocs"; 
        //ldaOption.niters = 10;
        ldaOption.dfile = "../sample_data_stopped.txt";
        
        Estimator estimator = new Estimator();
        estimator.init(ldaOption);
        
        estimator.estimate();
        
        //Model newModel = new Model();
        //newModel.initNewModel(ldaOption);
        
        //newModel.saveModel(ldaOption.modelName);
        
        
        
        //Inferencer inferencer = new Inferencer(); 
        //inferencer.init(ldaOption);
        //Model newModel = inferencer.inference(messages);
    }
}
