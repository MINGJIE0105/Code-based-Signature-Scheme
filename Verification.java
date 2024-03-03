import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.Instant;

import com.opencsv.*; 

public class Verification {
    public static void main(String[] args){
        String selection;
        Boolean isContinue = true;

        Scanner scanner = new Scanner(System.in);

        String menuOne = "\n***Verification***\n" +
                ">a. Load the m, mG+e and H, then decode\n" +
                ">b. Back to main menu\n" +
                ">q. Exit";

        while(isContinue){
            System.out.println(menuOne);
            selection = scanner.nextLine();
            switch(selection.toUpperCase()){
                case "A":
                    Verification.decode("output-signing");
                    break;
                case "B":
                    isContinue = false;
                    break;
                case "Q":
                    System.out.println("Exiting program...");
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    }

    public static void decode(String path){

        // Handle user's selection
        String selection;
        Boolean isContinue = true;
        Scanner scanner = new Scanner(System.in);
        
        File f = null;
        String[] folders; 
        
        try {    
            while(isContinue){
            int folderIdx = 0;

            // create new file
            f = new File(path);                                
            // array of files and directory
            folders = f.list();
            // for each name in the path array

            if(folders.length == 0){
                System.out.println(">>There is no signed message, please proceed to signing");
                isContinue = false;
                continue;
            }

            for(String folder:folders) {
                // prints filename and directory name
            System.out.println(String.format(">> [%d] %s", ++folderIdx, folder));
            }
            System.out.println(String.format(">> [C] Cancel"));

            System.out.println(String.format("\nSelect the folder to be loaded:"));
            selection = scanner.nextLine();

            switch(selection.toUpperCase()){
                case "C":
                    isContinue = false;
                    break;
                default:
                    // If it is not integer, not continue to read the csv
                    if(!Signing.isInteger(selection)) break;

                    // If the number from user is larger than the amount of available folder, not continue to read the csv
                    if(Integer.parseInt(selection) > folders.length) break;

                    String csvPath = String.format("%s/%s", path, folders[Integer.parseInt(selection)-1]);
                    System.out.println(csvPath);

                    long start, end;
                    start = System.currentTimeMillis();

                    Verification.processMatrix(csvPath);

                    end = System.currentTimeMillis();
                    System.out.println(String.format("\nVerification: %o (milliseconds)", end-start));
                    Constant.waitConsole();

                    break;
                }
            }
            
        } catch(Exception e) {
            // if any error occurs
            e.printStackTrace();
        }
    }

    public static void processMatrix(String path){

        // Path of storing matrices
        String targetGenerator = String.format("./%s/G.csv", path);
        String targetChecking = String.format("./%s/H.csv", path);
        String targetM = String.format("./%s/m.csv", path);
        String targetW = String.format("./%s/mGe.csv", path);

        ArrayList<ArrayList<Integer>> parityCheckMatrix = new ArrayList<ArrayList<Integer>>(); // H, parity checking matrix
        ArrayList<ArrayList<Integer>> encodedMessageWithError = new ArrayList<ArrayList<Integer>>(); // mG + e (w), encoded message with Error        
        ArrayList<ArrayList<Integer>> message = new ArrayList<ArrayList<Integer>>(); // m, random message
        ArrayList<ArrayList<Integer>> generatorMatrix = new ArrayList<ArrayList<Integer>>(); // G, generator matrix


        System.out.println("\t>>>> 1. Loading m, w, H and G");

        try {

            // Create an object of filereader 
            // class with CSV file as a parameter. 
            FileReader filereader = new FileReader(targetGenerator); 
    
            // filereader as parameter 
            CSVReader csvReader = new CSVReader(filereader); 
            
            String[] nextRecord; 
            // Read the matrix from csv, row-by-row 
            while ((nextRecord = csvReader.readNext()) != null) { 
                List<Integer> intValues = Arrays.asList(nextRecord).stream()
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList()); // back to List
                
                // Add the data to generatorMatrix
                generatorMatrix.add(new ArrayList<Integer>(intValues));
            } 

            filereader = new FileReader(targetChecking); 
            csvReader = new CSVReader(filereader); 
            nextRecord = null;
            // Read the matrix from csv, row-by-row 
            while ((nextRecord = csvReader.readNext()) != null) { 
                List<Integer> intValues = Arrays.asList(nextRecord).stream()
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList()); // back to List
                
                // Add the data to parity-checking matrix
                parityCheckMatrix.add(new ArrayList<Integer>(intValues));
            } 

            filereader = new FileReader(targetM); 
            csvReader = new CSVReader(filereader); 
            nextRecord = null;
            // Read the matrix from csv, row-by-row 
            while ((nextRecord = csvReader.readNext()) != null) { 
                List<Integer> intValues = Arrays.asList(nextRecord).stream()
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList()); // back to List
                
                // Add the data to parity-checking matrix
                message.add(new ArrayList<Integer>(intValues));
            } 

            filereader = new FileReader(targetW); 
            csvReader = new CSVReader(filereader); 
            nextRecord = null;
            // Read the matrix from csv, row-by-row 
            while ((nextRecord = csvReader.readNext()) != null) { 
                List<Integer> intValues = Arrays.asList(nextRecord).stream()
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList()); // back to List
                
                // Add the data to parity-checking matrix
                encodedMessageWithError.add(new ArrayList<Integer>(intValues));
            } 

        } catch(Exception e) {
            // if any error occurs
            e.printStackTrace();
        }

        System.out.println("\t>>>> 2. Loaded m, w, H and G");

        // Display the loaded variables
        System.out.print("\n\t>>>> m: ");
        message.get(0).stream().forEach(e -> System.out.print(String.format("%d ", e)));
        System.out.println();

        System.out.print("\n\t>>>> G: \n");
        generatorMatrix.stream().forEach(e -> System.out.println("\t\t" + e));
        System.out.println();

        System.out.print("\n\t>>>> H: \n");
        parityCheckMatrix.stream().forEach(e -> System.out.println("\t\t" + e));
        System.out.println();

        System.out.print("\n\t>>>> w: ");
        encodedMessageWithError.get(0).stream().forEach(e -> System.out.print(String.format("%d ", e)));
        System.out.println();

        ArrayList<ArrayList<Integer>> wH = Constant.matrixMultiplication(encodedMessageWithError, parityCheckMatrix);
        System.out.print("\n\t>>>> wH: \n");
        wH.stream().forEach(e -> System.out.println("\t\t" + e));
        System.out.println();

        int bitPosition = Verification.binaryVectorToInteger(wH) - 1;

        ArrayList<ArrayList<Integer>> error = generateErrorWithPosition(encodedMessageWithError.get(0).size(), bitPosition);
        System.out.print("\n\t>>>> e: \n");
        error.stream().forEach(e -> System.out.println("\t\t" + e));
        System.out.println();

        ArrayList<ArrayList<Integer>> wWithError = new ArrayList<ArrayList<Integer>> (encodedMessageWithError.stream().map(e -> new ArrayList<Integer>(e)).collect(Collectors.toList()));
        int bitValue = wWithError.get(0).get(bitPosition);
        wWithError.get(0).set(bitPosition, (bitValue==0) ? 1 : 0);
        System.out.print("\n\t>>>> w + e: \n");
        wWithError.stream().forEach(e -> System.out.println("\t\t" + e));
        System.out.println();

        ArrayList<ArrayList<Integer>> mG = Constant.matrixMultiplication(message, generatorMatrix);
        System.out.print("\n\t>>>> mG: \n");
        mG.stream().forEach(e -> System.out.println("\t\t" + e));
        System.out.println();

        System.out.print("\n\t>>>> Comparing mG and w+e: \n");
        if(compareMatrices(mG, wWithError)){
            System.out.print("\t\t True \n");
        }else{
            System.out.print("\t\t False \n");
        }
        System.out.println("\t==== ====");
    }

    public static Boolean compareMatrices(ArrayList<ArrayList<Integer>> matrixA, ArrayList<ArrayList<Integer>> matrixB){

        for(int i = 0; i<matrixA.size(); i++){
            for(int j=0; j<matrixA.get(0).size(); j++){
                int bitA = matrixA.get(i).get(j);
                int bitB = matrixB.get(i).get(j);

                if(bitA != bitB) return false;
            }
          }

        return true;
    }

    public static int binaryVectorToInteger(ArrayList<ArrayList<Integer>> input){

        int result = 0;

        int power = input.get(0).size() - 1;

        for(int i = 0; i< input.get(0).size(); i++){
            int bit = input.get(0).get(i);
            result = result + (bit * (int) Math.pow(2, power--));
        }

        return result;
    }


    public static ArrayList<ArrayList<Integer>> generateErrorWithPosition(int vectorDimension, int position){

        Random randomGenerator = new Random();

        // Identity Matrix
        ArrayList<ArrayList<Integer>> randomVector = new ArrayList<ArrayList<Integer>>();

        List<Integer> row  = Arrays.asList(new Integer[vectorDimension]); 
        row = row.stream().map(e -> e = 0).collect(Collectors.toList());
        row.set(position, 1);

        randomVector.add(new ArrayList<Integer>(row));
        return randomVector;
    }
}
