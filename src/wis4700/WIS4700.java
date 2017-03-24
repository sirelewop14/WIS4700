package wis4700;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        ArrayList<String> messages = new ArrayList<>();
        String splitVal = ",";
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        FileReader fread = new FileReader("/users/rhys/LDA_Test/user_tweets_fpl_from_twitter.csv");
        String line = "";
        int badCount = 0;
        try (BufferedReader bread = new BufferedReader(fread)) {
            while ((line = bread.readLine()) != null) {
                String[] splitLine = line.split(splitVal);
                if (splitLine.length <= 1) {
                    System.out.println("Bad Line!");
                    badCount++;
                } else {
                    Matcher m = p.matcher(splitLine[1]);
                    int i = 0;
                    String urlFree = "";     
                    if (m.find()) {
                        m.reset();
                        while (m.find()) {
                            urlFree = splitLine[1].replaceAll(m.group(i), "").trim();
                            i++;
                        }
                    } else {
                        urlFree = splitLine[1];
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

        System.out.println("Total bad lines: " + badCount);

        FileWriter fout = new FileWriter("/Users/rhys/LDA_Test/sample_data_stopped.txt");
        try (BufferedWriter out = new BufferedWriter(fout)) {
            String size = Integer.toString(messages.size());
            out.write(size, 0, size.length());
            out.newLine();
            //Print number of documents
            System.out.println("Total number of good lines: " + messages.size());
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
