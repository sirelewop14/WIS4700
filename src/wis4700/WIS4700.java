package wis4700;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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

        //Twords, topics
        TwitterLDA analyzer = new TwitterLDA(200, 100);

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

}
