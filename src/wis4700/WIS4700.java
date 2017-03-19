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
import java.net.InetAddress;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;


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
//        LDACmdOption ldaOption = new LDACmdOption(); 
//        ldaOption.est = true; 
//        //ldaOption.inf = true;
//        //ldaOption.alpha = (50 / ldaOption.K);
//        //ldaOption.beta = 0.1;
//        ldaOption.savestep = 100;
//        ldaOption.twords = 20;
//        ldaOption.dir = "/Users/rhys/LDA_Test/Model"; 
//        ldaOption.modelName = "newdocs"; 
//        ldaOption.niters = 10;
//        ldaOption.dfile = "/../user_tweets_fpl_from_twitter.csv";
//        
//        FileReader fin = new FileReader("/Users/rhys/LDA_Test/sample_data.csv");
//        FileWriter fout = new FileWriter("/Users/rhys/LDA_Test/sample_data_stopped.txt");
//        BufferedReader in = new BufferedReader(fin);
//        BufferedWriter out = new BufferedWriter(fout);
//        while (in.ready()){
//            String line = in.readLine();
//            System.out.println(line);
//            String stemmed = Stopwords.stemString(line);
//            String stopped = Stopwords.removeStemmedStopWords(stemmed);
//            out.write(stopped, 0, stopped.length());
//            out.newLine();
//            System.out.println(stopped);
//        }
//        out.flush();
//        out.close();
//        in.close();
          
        
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch_rhys").build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        
        SearchResponse response = client.prepareSearch("epl")
                .setTypes("elps")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("multi", "test"))
                .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))
                .setFrom(0).setSize(60).setExplain(true)
                .get();
        
        SearchHits hits = response.getHits();
        
        SearchHit hitArray[] = hits.getHits();
        
        for (int i = 0; i < hitArray.length; i++) {
            System.out.println(hitArray[i]);
        }
        
        
	}
    }

