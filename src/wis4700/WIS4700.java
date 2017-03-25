package wis4700;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wis4700.jgibblda.Estimator;
import wis4700.jgibblda.Inferencer;
import wis4700.jgibblda.LDACmdOption;
import wis4700.jgibblda.Model;

/**
 *
 * @author rhys
 */
public class WIS4700 {

    static String rawInputFile = "/users/rhys/LDA_Test/user_tweets_fpl_from_twitter.csv";
    static String LDADirectory = "/Users/rhys/LDA_Test/Model";

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner scanner = new Scanner(System.in);
        boolean run = true;
        
        while (run) {
            System.out.println("Please enter the operation to perform: ");
            System.out.println("1: Read in new data ");
            System.out.println("2: Estimate new Model");
            System.out.println("3: Inference New Data");
            System.out.println("4: Exit");
            System.out.println();
            try {
                int selection = scanner.nextInt();
                if (selection == 1) {
                    ArrayList<String> data = readCSV();
                    saveMessages(data);
                } else if (selection == 2) {
                    LDACmdOption options = setLDAOptions();
                    performEstimation(options);
                } else if (selection == 3) {
                    LDACmdOption options = setLDAOptions();
                    performInference(options);
                } else if (selection == 4) {
                    run = false;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static ArrayList<String> readCSV() throws FileNotFoundException {
        ArrayList<String> messages = new ArrayList<>();
        String splitVal = ",";
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        FileReader fread = new FileReader(rawInputFile);
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
        return messages;
    }

    public static void saveMessages(ArrayList<String> data) throws IOException {
        FileWriter fout = new FileWriter("/Users/rhys/LDA_Test/sample_data_stopped.txt");
        try (BufferedWriter out = new BufferedWriter(fout)) {
            String size = Integer.toString(data.size());
            out.write(size, 0, size.length());
            out.newLine();
            //Print number of documents
            System.out.println("Total number of good lines: " + data.size());
            for (int i = 0; i < data.size(); i++) {
                out.write(data.get(i), 0, data.get(i).length());
                out.newLine();
            }
            out.flush();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static LDACmdOption setLDAOptions() {
        LDACmdOption options = new LDACmdOption();
        options.est = true;
        //ldaOption.inf = true;
        //ldaOption.alpha = (50 / ldaOption.K);
        //ldaOption.beta = 0.1;
        options.savestep = 500;
        options.twords = 200;
        options.dir = LDADirectory;
        options.modelName = "model-final";
        //ldaOption.niters = 10;
        options.dfile = "../sample_data_stopped.txt";
        return options;
    }

    public static void performInference(LDACmdOption ldaOption) {
        System.out.println("Do inference for previously unseen (new) data using a previously estimated LDA model");
        Inferencer inferencer = new Inferencer();
        inferencer.init(ldaOption);
        Model newModel = inferencer.inference();
        
        for (int i = 0; i < newModel.phi.length; ++i) {
            //phi: K * V 
            System.out.println("-----------------------\ntopic" + i + " : ");
            System.out.println("start printing");
            for (int j = 0; j < 10; ++j) {
                System.out.println(inferencer.globalDict.id2word.get(j) + "\t" + newModel.phi[i][j]);
            }
        }
    }

    public static void performEstimation(LDACmdOption ldaOption) {
        //LDA Create new model with settings
        //This estimator creates a new model, but currently a model takes 4 hours
        //to build. This is commented out to avoid issues with overwriting.
        Estimator estimator = new Estimator();
        estimator.init(ldaOption);
        estimator.estimate();
    }

}
