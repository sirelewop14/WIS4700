package wis4700;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    static String dataFile = "/Users/rhys/LDA_Test/sample_data_stopped.txt";
    static String twordsFile = "/Users/rhys/LDA_Test/sample_data_stopped.txt.model-final.twords";
    static String userOutput = "/Users/rhys/LDA_Test/userEvalReport.txt";
    static String twordHitOutput = "/Users/rhys/LDA_Test/twordHitReport.txt";
    final static int NUM_TWORDS = 200;
    final static int NUM_TOPICS = 100;
    static int totalTwords = NUM_TWORDS * NUM_TOPICS;

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
            System.out.println("5: Evaluate Users");
            System.out.println("6: Run 1,2,3,5 & Exit");
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
                } else if (selection == 5) {
                    evaluateUsers();
                } else if (selection == 6) {
                    ArrayList<String> data = readCSV();
                    saveMessages(data);
                    LDACmdOption options = setLDAOptions();
                    performEstimation(options);
                    performInference(options);
                    //evaluateUsers();
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
        FileWriter fout = new FileWriter(dataFile);
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
        options.twords = NUM_TWORDS;
        options.dir = LDADirectory;
        options.modelName = "model-final";
        //ldaOption.niters = 10;
        options.dfile = "../" + dataFile;
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

    public static void evaluateUsers() throws FileNotFoundException, IOException {
        //Total tword array and values for twords
        String[] twords = new String[totalTwords];
        Double[] twordVal = new Double[totalTwords];
        Boolean topicLine = true;
        String splitVal = " ";
        String line;
        int i = 0;
        int j = 0;
        FileReader twordReader = new FileReader(twordsFile);
        //This reads the topic keys and values into two arrays
        try (BufferedReader twordin = new BufferedReader(twordReader)) {
            while ((line = twordin.readLine()) != null) {
                String[] splitLine = line.split(splitVal);
                if (topicLine) {
                    topicLine = false;
                    j++;
                } else {
                    twords[i] = splitLine[0];
                    twordVal[i] = Double.valueOf(splitLine[1]);
                    //System.out.println(twords[i] + "   "+twordVal[i]);
                    i++;
                    if (j == NUM_TWORDS) {
                        j = 0;
                        topicLine = true;
                    } else {
                        j++;
                    }

                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        //This reads the users and messages in
        //For topic hits
        ArrayList<String> users = new ArrayList<>();
        ArrayList<Double[]> idAndVal = new ArrayList<>();
        Double[] topicArray = new Double[NUM_TOPICS];
        //For key hits
        ArrayList<int[]> idAndHitCount = new ArrayList<>();
        int[] twordHits = new int[totalTwords];
        //Fill to 0
        Arrays.fill(topicArray, 0.0);
        Arrays.fill(twordHits, 0);
        //Start parsing input
        String csvLine;
        String csvSplit = ",";
        String messageSplit = " ";
        FileReader csvReader = new FileReader(rawInputFile);
        try (BufferedReader csvIn = new BufferedReader(csvReader)) {
            while ((csvLine = csvIn.readLine()) != null) {
                String stemmed = Stopwords.stemString(csvLine);
                String stopped = Stopwords.removeStemmedStopWords(stemmed);
                String[] csvSplitLine = stopped.split(csvSplit);
                int userIndex;
                if (csvSplitLine.length <= 1) {
                    System.out.println("Bad Line");
                } else {
                    if (!users.contains(csvSplitLine[0])) {
                        //User not in array
                        System.out.println("Adding new user: " + csvSplitLine[0]);
                        users.add(csvSplitLine[0]);
                        userIndex = users.indexOf(csvSplitLine[0]);
                        idAndVal.add(userIndex, topicArray);
                        idAndHitCount.add(userIndex, twordHits);
                    }
                    userIndex = users.indexOf(csvSplitLine[0]);
                    String message[] = csvSplitLine[1].split(messageSplit);
                    for (int k = 0; k < message.length; k++) {
                        for (int l = 0; l < twords.length; l++) {
                            if (twords[l].equals(message[k])) {
                                //Determine Topic
                                int topicNumber = (l / NUM_TWORDS);
                                Double[] temp = idAndVal.get(userIndex);
                                temp[topicNumber] = temp[topicNumber] + twordVal[l];
                                idAndVal.set(userIndex, temp);
                                //Increment Key Hit
                                int[] hitTemp = idAndHitCount.get(userIndex);
                                hitTemp[l]++;
                                idAndHitCount.set(userIndex, hitTemp);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        //Write out the calculated results.
        FileWriter reportWriter = new FileWriter(userOutput);
        try (BufferedWriter buffReportWriter = new BufferedWriter(reportWriter)) {
            System.out.println("Writing out Topic Report per User to: " + userOutput);
            for (int k = 0; k < users.size(); k++) {
                buffReportWriter.write(users.get(k) + "\n");
                Double[] tempVals = idAndVal.get(k);
                for (int l = 0; l < NUM_TOPICS; l++) {
                    buffReportWriter.write(l + "," + "");
                    buffReportWriter.write(tempVals[l].toString() + "\n");
                }
            }
            buffReportWriter.flush();
            buffReportWriter.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        
        FileWriter hitWriter = new FileWriter(twordHitOutput);
        try (BufferedWriter buffHitWriter = new BufferedWriter(hitWriter)) {
            System.out.println("Writing out User Key Hit Report to: " + twordHitOutput);
            buffHitWriter.write("twords,");
            for (int l = 0; l < users.size(); l++) {
                buffHitWriter.write(users.get(l) + ",");
            }
            for (int k = 0; k < twords.length; k++) {
                if (0 == (k % 200)) {
                    buffHitWriter.write("Topic: " + k / 200);
                }
                buffHitWriter.write(twords[k] + ",");
                for (int l = 0; l < users.size(); l++) {
                    int[] tempHitArray = idAndHitCount.get(l);
                    buffHitWriter.write(tempHitArray[k]);
                }
            }

        }
    }
}
