/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wis4700;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
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

//        
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch_rhys").build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        
//        SearchResponse response = client.prepareSearch("epl")
//                .setTypes("elps")
//                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                .setQuery(QueryBuilders.termQuery("multi", "test"))
//                .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))
//                .setFrom(0).setSize(60).setExplain(true)
//                .get();
        
        SearchResponse resp1 = client.prepareSearch("epl").get();
        SearchHits hits = resp1.getHits();
        SearchHit[] hitArray = hits.getHits();
        String[] messages = new String[hitArray.length];
        
        for (int i = 0; i < hitArray.length; i++) {
            //Map<String, Object> json = hitArray[i].getSource();
            //InternalSearchHit ishit =  hitArray1.getSource();
            //System.out.println(json.size());
            //System.out.println();
            //System.out.println(hitArray[i].getSource().get("Username"));
            //System.out.println(hitArray[i].getSource().get("Message"));
            messages[i] = hitArray[i].getSource().get("Message").toString();
            
            //System.out.println(hitArray1);
            //System.out.println(hitArray1.getInnerHits());
        }
//        for(int j = 0; j < messages.length; j++){
//            System.out.println(messages[j]);
//        }
        
        
        LDACmdOption ldaOption = new LDACmdOption(); 
        ldaOption.est = true; 
        //ldaOption.inf = true;
        ldaOption.alpha = (50 / ldaOption.K);
        ldaOption.beta = 0.1;
        ldaOption.savestep = 100;
        ldaOption.twords = 20;
        ldaOption.dir = "/Users/rhys/LDA_Test/Model"; 
        ldaOption.modelName = "newdocs"; 
        ldaOption.niters = 10;
        
        //ldaOption.dfile = "/../user_tweets_fpl_from_twitter.csv";
        
        Model newModel = new Model();
        newModel.initNewModel(ldaOption);
        
        newModel.saveModel(ldaOption.modelName);
        
        
        
        Inferencer inferencer = new Inferencer(); 
        //inferencer.init(ldaOption);
        //Model newModel = inferencer.inference(messages);
//        
        // The input stream from the JSON response
//        BufferedInputStream buffer = null;
//
//        // URL objects
//        String url = "";
//        URL urlObject = null;
//        URLConnection con = null;
//        String response = "";
//
//        // JSON objects
//        JSONArray hitsArray = null;
//        JSONObject hits = null;
//        JSONObject source = null;
//        JSONObject json = null;
//
//        try {
//            // get a JSON object from ElasticSearch
//            url = "http://localhost:9200/epl/_search";
//
//            // configure the URL request
//            urlObject = new URL(url);
//            con = urlObject.openConnection();
//            con.setRequestProperty("User-Agent", "Mozilla/5.0");
//
//            buffer = new BufferedInputStream(con.getInputStream());
//
//            while (buffer.available() > 0) {
//                response += (char) buffer.read();
//            }
//
//            buffer.close();
//
//            // parse the JSON response 		
//            json = new JSONObject(response);
//            hits = json.getJSONObject("hits");
//            hitsArray = hits.getJSONArray("hits");
//            
//            int size = hitsArray.length();
//            
//            String[] username = new String[size];
//            String[] message = new String[size];
//            for (int i = 0; i < hitsArray.length(); i++) {
//                JSONObject h = hitsArray.getJSONObject(i);
//                source = h.getJSONObject("_source");
//                //string object = (source.getString("the string you want to get"));
//                //System.out.println(source);
//                username[i] = source.getString("Username");
//                message[i] = source.getString("Message");
//                
//                System.out.println(username[i]);
//                System.out.println(message[i]);
//                
//            }
//        } catch (Exception e) {
//            // handle the exception
//        }

    }
}
