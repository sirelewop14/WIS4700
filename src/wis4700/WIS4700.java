package wis4700;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import wis4700.jgibblda.LDACmdOption;

/**
 * This class provides a simple command line interface for working with the 
 * TwitterLDA class. A user can select option and perform specific steps through
 * the simple menu options. There are a few options which collect parameters from
 * the user and then performs all the needed modeling, inferencing and evaluation
 * steps automatically. I have also included a demo option which runs with pre-
 * defined parameters to process a small file included in the repository for
 * sample runs.
 * 
 * @author rhys
 */
public class WIS4700 {

    /**
     * The main method. This function runs the simple interface for users to 
     * interact with the TwitterLDA class. There are several options available
     * to perform the individual steps of the modeling and analysis process,
     * as well there is a demo option for out-of-the-box operation.
     * 
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner scanner = new Scanner(System.in);
        boolean run = true;

        System.out.println("Please enter the parameters for modeling:");
        System.out.println("All files should have absolute paths!");
        System.out.println("\tLDA Model Directory: ");
        String LDADirectory = scanner.nextLine().trim();

        String rawInputFile;
        String dataFileName;
        String dataFile;
        String userOutput;
        String twordHitOutput;
        String labeledUserOutput;

        //Twords, topics
        TwitterLDA analyzer = new TwitterLDA(200, 100);

        while (run) {
            System.out.println("Please enter the operation to perform: ");
            System.out.println("1: Exit");
            System.out.println("2: Read in new data");
            System.out.println("3: Estimate new Model");
            System.out.println("4: Inference New Data");
            System.out.println("5: Evaluate Users");
            System.out.println("6: Run 2,3,4,5 & Exit");
            System.out.println("7: Label Topics - *Requires code modification*");
            System.out.println("8: Run Demo with defaults");
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
                        dataFile = scanner.nextLine().trim();
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
                        System.out.println("\tEnter T-Word File Name: ");
                        String twordsFile = scanner.nextLine().trim();
                        System.out.println("\tRaw CSV Input File: ");
                        rawInputFile = scanner.nextLine().trim();
                        System.out.println("\tPlease enter the filename for User Output: ");
                        userOutput = scanner.nextLine().trim();
                        System.out.println("\tPlease enter the filename for T-Word Hit Output:");
                        twordHitOutput = scanner.nextLine().trim();
                        analyzer.evaluateUsers(twordsFile, rawInputFile, userOutput, twordHitOutput);
                        break;
                    }
                    case 6: {
                        System.out.println("\tEnter T-Word File Name: ");
                        String twordsFile = scanner.nextLine().trim();
                        System.out.println("\tRaw CSV Input File: ");
                        rawInputFile = scanner.nextLine().trim();
                        System.out.println("\tPlease enter the filename for User Output: ");
                        userOutput = scanner.nextLine().trim();
                        System.out.println("\tPlease enter the filename for T-Word Hit Output:");
                        twordHitOutput = scanner.nextLine().trim();
                        System.out.println("\tProcessed CSV File Name: ");
                        dataFile = scanner.nextLine().trim();
                        ArrayList<String> data = analyzer.readCSV(rawInputFile);
                        analyzer.saveMessages(data, dataFile);
                        //Pause for flush to disk
                        TimeUnit.SECONDS.sleep(5);
                        LDACmdOption options = analyzer.setLDAOptions(LDADirectory, dataFile);
                        analyzer.performEstimation(options);
                        analyzer.performInference(options);
                        analyzer.evaluateUsers(twordsFile, rawInputFile, userOutput, twordHitOutput);
                        run = false;
                        break;
                    }
                    case 7: {
                        System.out.println("\tPlease enter the filename for User Output: ");
                        userOutput = scanner.nextLine().trim();
                        System.out.println("\tPlease enter the filename for Labeled User Output: ");
                        labeledUserOutput = scanner.nextLine().trim();
                        String[] topics = {"1", "5", "10", "18", "19", "26", "28", "33", "38", "46", "55", "61", "71", "84", "87", "89"};
                        String[] labels = {"Tennis", "Rugby", "News", "Family", "Spurs", "FPL", "Love", "LFC", "UK Politics",
                            "American Politics", "Education", "Twitter", "Driving", "Entertainment", "Food and Drink", "Music and Music Videos"};
                        analyzer.labelTopics(topics, labels, userOutput, labeledUserOutput);
                        break;
                    }
                    case 8: {

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
}
