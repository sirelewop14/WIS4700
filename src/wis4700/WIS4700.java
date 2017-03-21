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
import java.util.ArrayList;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
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
        //SearchResponse resp1 = client.prepareSearch("epl").setFrom(0).setSize(10000).get();
        //SearchHits hits = resp1.getHits();
        //SearchHit[] hitArray = hits.getHits();
        
        ArrayList<String> lines = new ArrayList<>();
        
        
        QueryBuilder qb = termQuery("multi", "test");

        SearchResponse scrollResp = client.prepareSearch("epl")
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(qb)
                .setSize(1000).get(); //max of 100 hits will be returned for each scroll
                //Scroll until no hits are returned
        do {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                //Handle the hit...
                String line = "";
            try{
                line = hit.getSource().get("Message").toString();
            } catch (Exception e){
                System.out.println(e);
                System.out.println("At Index: "+hit.getId());
            }
            //Stem and remove stop words
            String stemmed = Stopwords.stemString(line);
            String stopped = Stopwords.removeStemmedStopWords(stemmed);
            lines.add(stopped);
            }

            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
        } while (scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.
        
        
        
        
        
        //Parse data from ES and write out to file
        FileWriter fout = new FileWriter("/Users/rhys/LDA_Test/sample_data_stopped.txt");
        try (BufferedWriter out = new BufferedWriter(fout)) {
            String size = Integer.toString(lines.size());
            out.write(size, 0, size.length());
            out.newLine();
            for (int i = 0; i < lines.size(); i++) {
                out.write(lines.get(i), 0, lines.get(i).length());
                out.newLine();
            }
            out.flush();
        }
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
    public void parseHit(SearchHit hit){
        
    }
    
   
}
