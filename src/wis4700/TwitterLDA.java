package wis4700;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wis4700.jgibblda.Estimator;
import wis4700.jgibblda.Inferencer;
import wis4700.jgibblda.LDACmdOption;

/**
 * This class provides several methods for utilizing LDA Analysis for topic 
 * identification. The purpose of these functions is to allow a user to input
 * a CSV file containing users and tweets from Twitter. The functions provided
 * here are then used to parse the tweets, stem and remove stop words, and 
 * then estimate an LDA model based of the data.
 * 
 * Once the model has been created, an inferece can be performed on the CSV 
 * data. When inferece has been performed, the user can then run the evaluation
 * step. This step analyzes the raw data and calculates the weight of identified
 * topics for individual users.
 * 
 * Finally, once the weight of each topic per user has been calculated, the 
 * labeling function can provide a report in CSV format w/ vectors for the 
 * selected topics and labels. The final function SHOULD only be run once the 
 * generated tword file (From inferecing step) has been read through, and topics
 * of interest have been identified and labeled.
 * @author rhys
 */
public class TwitterLDA {

    private final int NUM_TWORDS;
    private final int NUM_TOPICS;
    private final int totalTwords;

    /**
     * This is the default constructor for the TwitterLDA object. You must pass 
     * 2 integer values to set parameters for the number of Topic Words(twords)
     * and the number of Topics.
     * 
     * @param twords integer number of topic words per topic.
     * @param topics integer number of topics for corpus.
     */
    public TwitterLDA(int twords, int topics) {
        NUM_TWORDS = twords;
        NUM_TOPICS = topics;
        totalTwords = NUM_TWORDS * NUM_TOPICS;
    }

    /**
     * This function reads in a CSV file containing usernames and messages. Each
     * line should be formatted as: "username1, message content here"
     * 
     * The function strips out any URL's in the messages and removes the stop
     * words defined the the Stopwords class. It then stems the message content 
     * and stores the processed data in an array.
     * 
     * @param rawInputFile the correctly formatted CSV file containing input data.
     * @return an ArrayList of processed messages.
     * @throws FileNotFoundException
     */
    public ArrayList<String> readCSV(String rawInputFile) throws FileNotFoundException {
        System.out.println("Reading in data from CSV");
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

    /**
     * This function saves the processed messages from an ArrayList. This 
     * function expects an ArrayList generated by the readCSV function. It will
     * write out the messages to a text file to be used for modeling.
     * 
     * @param data the ArrayList containing processed messages.
     * @param dataFile the file name for output.
     * @throws IOException
     */
    public void saveMessages(ArrayList<String> data, String dataFile) throws IOException {
        System.out.println("Saving parsed CSV data");
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
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * This is a simple function which sets the parameters needed for LDA 
     * analysis. It only accepts the Directory for LDA Model storage, and 
     * the data file name for the input. This data file name should be the 
     * processed CSV and NOT the raw CSV.
     * 
     * @param LDADirectory storage location of the LDA Model.
     * @param dataFileName location of the processed CSV file containing ONLY messages.
     * @return LDACmdOption object with the correct parameters.
     */
    public LDACmdOption setLDAOptions(String LDADirectory, String dataFileName) {
        System.out.println("Set LDA CMD Options");
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
        options.dfile = "../" + dataFileName;
        return options;
    }

    /** 
     * This function is a simple method which performs the inferencing step 
     * based on the options selected in the ldaOption object passed in. The 
     * inferencing step does require that a model already exist. If there is no
     * current model, use the Estimator to create one.
     * 
     * @param ldaOption set of options for inferencing.
     */
    public void performInference(LDACmdOption ldaOption) {
        System.out.println("Do inference for previously unseen (new) data using a previously estimated LDA model");
        Inferencer inferencer = new Inferencer();
        inferencer.init(ldaOption);
        inferencer.inference();
    }

    /**
     * This function calls the estimator to create a new LDA Model from a 
     * processed CSV file. The file for estimation should be specified in the
     * LDACmdOption object before passing it into this method.
     * 
     * @param ldaOption set of options for estimation.
     */
    public void performEstimation(LDACmdOption ldaOption) {
        System.out.println("Performing Estimation");
        //LDA Create new model with settings
        //This estimator creates a new model, but currently a model takes 4 hours
        //to build. This is commented out to avoid issues with overwriting.
        Estimator estimator = new Estimator();
        estimator.init(ldaOption);
        estimator.estimate();
    }

    /**
     * This function performs the user evaluation for the set of data against 
     * the generated model. Before calling the method, you must have parsed the
     * raw CSV, used parsed data for estimation and then for inferencing, then
     * you may use this function to generate results for users.
     * 
     * Essentially, this method takes account of all the topics generated by 
     * the inferencing and the topics twords. These values are loaded into 
     * memory. along with the value weight of each tword. 
     * 
     * Then, the raw CSV file is parsed and its contents are comapared to the 
     * set of topics and twords. When a tword hit is found, the hit is recorded
     * and the value of the hits weight is added to the topic value for that user.
     * 
     * Each user ends up with a set of values, one value for each topic. This
     * data tells us what topics are the most important to each individual user.
     * 
     * The are two files created by this function. The first file is a set of 
     * values for each topic per user. The second file is a set of all the twords
     * for the entire model, and how many times each user had a hit for that tword.
     * 
     * 
     * @param twordsFile path to the location of the tword file generated by inferencing.
     * @param rawInputFile the raw CSV file for user analysis.
     * @param userOutput location of the user output file.
     * @param twordHitOutput location of the tword hit report file.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void evaluateUsers(String twordsFile, String rawInputFile, String userOutput, String twordHitOutput) throws FileNotFoundException, IOException {
        System.out.println("Starting user evaluation");
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
                    twords[i] = splitLine[0].trim();
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
        //This reads the users and messages in for topic hits
        ArrayList<String> users = new ArrayList<>();
        ArrayList<Double[]> idAndVal = new ArrayList<>();
        ArrayList<int[]> idAndHitCount = new ArrayList<>();
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
                        //Create new arrays and fill with 0's
                        Double[] topicArray = new Double[NUM_TOPICS];
                        int[] twordHits = new int[totalTwords];
                        Arrays.fill(topicArray, 0.0);
                        Arrays.fill(twordHits, 0);
                        //Then assign arrays to userID's
                        idAndVal.add(userIndex, topicArray);
                        idAndHitCount.add(userIndex, twordHits);
                    }
                    //After adding user to array
                    //parse more docs
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
        //Write out the calculated results by topic.
        System.out.println("Total number of users: " + users.size());
        FileWriter reportWriter = new FileWriter(userOutput);
        try (BufferedWriter buffReportWriter = new BufferedWriter(reportWriter)) {
            System.out.println("Writing out Topic Report per User to: " + userOutput);
            for (int k = 0; k < users.size(); k++) {
                //Write out the username
                buffReportWriter.write(users.get(k) + "\n");
                Double[] tempVals = idAndVal.get(k);
                for (int l = 0; l < NUM_TOPICS; l++) {
                    //Write out the sum of hit values per topic
                    buffReportWriter.write(l + "," + "");
                    buffReportWriter.write(tempVals[l].toString() + "\n");
                }
            }
            buffReportWriter.flush();
            buffReportWriter.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        //Write out the Hit Count Results by user.
        FileWriter hitWriter = new FileWriter(twordHitOutput);
        try (BufferedWriter buffHitWriter = new BufferedWriter(hitWriter)) {
            System.out.println("Writing out User Key Hit Report to: " + twordHitOutput);
            //CSV Field names
            buffHitWriter.write("twords,");
            for (int l = 0; l < users.size(); l++) {
                //Each user is their own field
                buffHitWriter.write(users.get(l) + ",");
            }
            buffHitWriter.write("\n");
            for (int k = 0; k < twords.length; k++) {
                if (0 == (k % 200)) {
                    //Determine topic key word sets and break up
                    buffHitWriter.write("Topic: " + k / 200 + "\n");
                }
                buffHitWriter.write(twords[k] + ",");
                for (int l = 0; l < users.size(); l++) {
                    //Write out the number of hits per key word by username
                    int[] tempHitArray = idAndHitCount.get(l);
                    String value = Integer.toString(tempHitArray[k]);
                    buffHitWriter.write(value + ",");
                }
                buffHitWriter.write("\n");
            }
            buffHitWriter.flush();
            buffHitWriter.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * This method is for generating the final labeled report of user evaluation.
     * It accepts two arrays, which must be the same size. The arrays should 
     * contain topic numbers in one, and labels for the topics in another.
     * 
     * Before using this function a human must read through the topics and twords
     * to find out what the correct label should be for each topic and what topic
     * number corresponds with each label.
     * 
     * The method processes through the user output from the evaluation step,
     * then only returns the data for topics specified in the the arrays passed
     * in. All users are accounted for and the final result is a file containing
     * only the values for each topic per user in a CSV vector format.
     * 
     * @param topics array containing topic numbers
     * @param labels array containing labels for the topic numbers
     * @param userOutput location of the file created by the evaluation step.
     * @param labeledUserOutput location to save the labeled user evaluation report.
     * @throws FileNotFoundException
     */
    public void labelTopics(String[] topics, String[] labels, String userOutput, String labeledUserOutput) throws FileNotFoundException {
        System.out.println("Producing labeled User Topics document.");
        FileReader userTopicReader = new FileReader(userOutput);
        String splitVal = ",";
        try (BufferedReader userTopicBuffReader = new BufferedReader(userTopicReader)) {
            FileWriter userTopicWriter = new FileWriter(labeledUserOutput);
            try (BufferedWriter userTopicBuffWriter = new BufferedWriter(userTopicWriter)) {
                String line = "";
                int topicCounter = 0;
                userTopicBuffWriter.write("Username,[");
                for (int i = 0; i < labels.length; i++) {
                    userTopicBuffWriter.write(labels[i] + ",");
                }
                userTopicBuffWriter.write("]\n");
                Boolean first = true;
                while ((line = userTopicBuffReader.readLine()) != null) {
                    String[] splitLine = line.split(splitVal);
                    if (splitLine.length <= 1) {
                        if (!first) {
                            userTopicBuffWriter.write("\n");
                        }
                        userTopicBuffWriter.write(line + ",[");
                        topicCounter = 0;
                        first = false;
                    } else if (splitLine[0].equals(topics[topicCounter])) {
                        userTopicBuffWriter.write(splitLine[1] + ",");
                        if (!((topicCounter + 1) >= topics.length)) {
                            topicCounter++;
                        } else {
                            userTopicBuffWriter.write("]");
                        }
                    }
                }
                userTopicBuffWriter.flush();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
