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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import wis4700.jgibblda.Estimator;
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
        //ES Database Connection
        /*
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch_rhys").build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        //Call for data from ES
        //Create array for storing results
        ArrayList<String> lines = new ArrayList<>();
        SearchResponse scrollResp = client.prepareSearch("epl")
                .setScroll(new TimeValue(60000))
                .setSize(1000).get(); //max of 1000 hits will be returned for each scroll
        //Scroll until no hits are returned
        do {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                //Handle the hit...
                String line = "";
                try {
                    line = hit.getSource().get("Message").toString();
                } catch (Exception e) {
                    System.out.println(e);
                    System.out.println("At Index: " + hit.getId());
                }
                //Stem and remove stop words
                String stemmed = Stopwords.stemString(line);
                String stopped = Stopwords.removeStemmedStopWords(stemmed);
                lines.add(stopped);
            }
            //Scroll again
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
        } while (scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.

        //Parse data from ES and write out to file
        FileWriter fout = new FileWriter("/Users/rhys/LDA_Test/sample_data_stopped.txt");
        try (BufferedWriter out = new BufferedWriter(fout)) {
            String size = Integer.toString(lines.size());
            out.write(size, 0, size.length());
            out.newLine();
            //Print number of documents
            System.out.println(lines.size());
            for (int i = 0; i < lines.size(); i++) {
                out.write(lines.get(i), 0, lines.get(i).length());
                out.newLine();
            }
            out.flush();
        }


        
        
        //Empty array now that data is in text doc
        lines.clear();
        lines.trimToSize();
         */
        ArrayList<String> messages = new ArrayList<>();
        String splitVal = ",";
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        FileReader fread = new FileReader("/users/rhys/LDA_Test/user_tweets_fpl_from_twitter.csv");
        String line = "";
        int badCount = 0;
        try (BufferedReader bread = new BufferedReader(fread)) {
            while ((line = bread.readLine()) != null) {
                //System.out.println(line);
                String[] splitLine = line.split(splitVal);
                //System.out.println(splitLine[0]);
                //System.out.println(splitLine[1]);
                if (splitLine.length <= 1) {
                    System.out.println("Bad Line!");
                    badCount++;
                } else {
                    //String urlFree = removeUrl(splitLine[1]);
                    Matcher m = p.matcher(splitLine[1]);
                    int i = 0;
                    String urlFree = "";
                    while (m.find()) {
                        urlFree = splitLine[1].replaceAll(m.group(i), "").trim();
                        i++;
                    }

                    String stemmed = Stopwords.stemString(urlFree);
                    String stopped = Stopwords.removeStemmedStopWords(stemmed);

                    messages.add(stopped);
                }

            }

        } catch (Exception e) {
            System.out.println(line);
            System.out.println(e);
        }

        System.out.println(badCount);

        FileWriter fout = new FileWriter("/Users/rhys/LDA_Test/sample_data_stopped.txt");
        try (BufferedWriter out = new BufferedWriter(fout)) {
            String size = Integer.toString(messages.size());
            out.write(size, 0, size.length());
            out.newLine();
            //Print number of documents
            System.out.println(messages.size());
            for (int i = 0; i < messages.size(); i++) {
                out.write(messages.get(i), 0, messages.get(i).length());
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
        ldaOption.twords = 200;
        ldaOption.dir = "/Users/rhys/LDA_Test/Model";
        ldaOption.modelName = "model-final";
        //ldaOption.niters = 10;
        ldaOption.dfile = "../sample_data_stopped.txt";

//        
//        System.out.println("Do inference for previously unseen (new) data using a previously estimated LDA model"); 
//                Inferencer inferencer = new Inferencer(); 
//                inferencer.init(ldaOption);
// 
//                Model newModel = inferencer.inference(); 
// 
//                for (int i = 0; i < newModel.phi.length; ++i){ 
//                        //phi: K * V 
//                        //System.out.println("-----------------------\ntopic" + i  + " : ");
//                        System.out.println("start printing");
//                        for (int j = 0; j < 10; ++j){ 
//                                System.out.println(inferencer.globalDict.id2word.get(j) + "\t" + newModel.phi[i][j]); 
//                        } 
//                } 
        //This estimator creates a new model, but currently a model takes 4 hours
        //to build. This is commented out to avoid issues with overwriting.
//        Estimator estimator = new Estimator();
//        estimator.init(ldaOption);
//        estimator.estimate();
        //Model newModel = new Model();
        //newModel.initNewModel(ldaOption);
        //newModel.saveModel(ldaOption.modelName);
        //Inferencer inferencer = new Inferencer(); 
        //inferencer.init(ldaOption);
        //Model newModel = inferencer.inference(messages);
    }

    public static String removeUrl(String commentstr) {

        return commentstr;

    }

}
