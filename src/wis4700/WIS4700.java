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
import java.util.concurrent.TimeUnit;
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
        Scanner scanner = new Scanner(System.in);
        boolean run = true;

        System.out.println("Please enter the parameters for modeling:");
        System.out.println("All files are relative to the LDA Model Directory!");
        System.out.println("\tLDA Model Directory: ");
        String LDADirectory = scanner.nextLine().trim();

        System.out.println("\tEnter T-Word File Name: ");
        String twordsFile = scanner.nextLine().trim();

        String rawInputFile = "";
        String dataFileName = "";
        String dataFile = "";
        String userOutput = "";
        String twordHitOutput = "";
        String labeledUserOutput;
        String labeledHitOutput;
        
        TwitterLDA analyzer = new TwitterLDA();

        while (run) {
            System.out.println("Please enter the operation to perform: ");
            System.out.println("1: Exit");
            System.out.println("2: Read in new data");
            System.out.println("3: Estimate new Model");
            System.out.println("4: Inference New Data");
            System.out.println("5: Evaluate Users");
            System.out.println("6: Run 1,2,3,5 & Exit");
            System.out.println("7: Label Topics");
            System.out.println();
            try {
                int selection = scanner.nextInt();
                switch (selection) {
                    case 1: {
                        run = false;
                        break;
                    }
                    case 2: {
                        System.out.println("\tRaw CSV Input File: ");
                        rawInputFile = scanner.nextLine().trim();
                        System.out.println("\tProcessed CSV File Name: ");
                        dataFileName = scanner.nextLine().trim();
                        dataFile = LDADirectory + dataFileName;
                        ArrayList<String> data = analyzer.readCSV(rawInputFile);
                        analyzer.saveMessages(data, dataFile);
                        break;
                    }
                    case 3: {
                        System.out.println("\tProcessed CSV File Name: ");
                        dataFileName = scanner.nextLine().trim();
                        LDACmdOption options = analyzer.setLDAOptions(LDADirectory, dataFileName);
                        analyzer.performEstimation(options);
                        break;
                    }
                    case 4: {
                        System.out.println("\tProcessed CSV File Name: ");
                        dataFileName = scanner.nextLine().trim();
                        LDACmdOption options = analyzer.setLDAOptions(LDADirectory, dataFileName);
                        analyzer.performInference(options);
                        break;
                    }
                    case 5: {
                        System.out.println("Please enter the filename for User Output: ");
                        userOutput = scanner.nextLine().trim();
                        System.out.println("Please enter the filename for T-Word Hit Output:");
                        twordHitOutput = scanner.nextLine().trim();
                        analyzer.evaluateUsers(userOutput, twordHitOutput);
                        break;
                    }
                    case 6: {                        
                        System.out.println("\tRaw CSV Input File: ");
                        rawInputFile = scanner.nextLine().trim();
                        System.out.println("\tProcessed CSV File Name: ");
                        dataFileName = scanner.nextLine().trim();
                        dataFile = LDADirectory + dataFileName;
                        ArrayList<String> data = analyzer.readCSV(rawInputFile);
                        analyzer.saveMessages(data, dataFile);
                        //Pause for flush to disk
                        TimeUnit.SECONDS.sleep(5);
                        LDACmdOption options = analyzer.setLDAOptions(LDADirectory, dataFileName);
                        analyzer.performEstimation(options);
                        analyzer.performInference(options);
                        analyzer.evaluateUsers();
                        run = false;
                        break;
                    }
                    case 7: {
                        labelTopics();
                        break;
                    }
                    default:
                        break;
                }
            } catch (IOException | InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    

    public static void labelTopics() throws FileNotFoundException {
        //Topics: 1, 5, 10, 18, 19, 26, 28, 33, 38, 46, 55, 61, 71, 84, 87, 89
        System.out.println("Producing labeled topic documents.");
        System.out.println("Producing labeled User Topics document.");
        String[] topics = {"1", "5", "10", "18", "19", "26", "28", "33", "38", "46", "55", "61", "71", "84", "87", "89"};
        String[] labels = {"Tennis", "Rugby", "News", "Family", "Spurs", "FPL", "Love", "LFC", "UK Politics",
            "American Politics", "Education", "Twitter", "Driving", "Entertainment", "Food and Drink", "Music and Music Videos"};
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
