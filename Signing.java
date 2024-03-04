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

public class Signing {
  public static void main(String[] args){
    
    String selection;
    Boolean isContinue = true;

    Scanner scanner = new Scanner(System.in);

    String menuOne = "\n***Signing***\n" +
            ">a. Load the Generator and Parity-check Matrices, then encode\n" +
            ">b. Back to main menu\n" +
            ">q. Exit";

    while(isContinue){
        System.out.println(menuOne);
        selection = scanner.nextLine();
        switch(selection.toUpperCase()){
            case "A":
              Signing.encode("output-matrix");
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

  public static void encode(String path) {
    
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
            System.out.println(">>There is no matrix, please generate matrix");
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
            
            Signing.processMatrix(csvPath);
            
            end = System.currentTimeMillis();
            System.out.println(String.format("\nSigning: %o (milliseconds)", end-start));
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
    String pathOfGenerator = String.format("./%s/G", path);
    String pathOfChecking = String.format("./%s/H", path);

    // 
    String targetGenerator = "";
    String targetChecking = "";

    // Object to store Generator Matrix and Parity-checking Matrix
    ArrayList<ArrayList<Integer>> generatorMatrix = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> parityCheckMatrix = new ArrayList<ArrayList<Integer>>();


    // Get the total number of Matrices in the directory
    File f = null;
    String[] folders; 

    int totalMatrix, randomMatrixToBeUsed;
    int minNumber = 0;
    ArrayList<ArrayList<Integer>> randomMessage = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> encodedMessage = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> error = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> encodedMessageWithError = new ArrayList<ArrayList<Integer>>();

    // Random object
    Random randomGenerator = new Random();


    System.out.println();

    try {

      f = new File(pathOfGenerator);                                
      folders = f.list();

      totalMatrix = folders.length;

      // Randomly generate an integer between minNumber and totalMatrix where totalMatrix refers to the amount of the generator matrices or parity checking matrices
      // totalMatrix is excluded because the filename from 0 until totalMatrix-1 where 0 is not displayed 
      randomMatrixToBeUsed = randomGenerator.nextInt((totalMatrix - minNumber)) + minNumber;

      // Set the path for the randomly chosen Generator Matrix and Parity-checking Matrix
      targetGenerator = (randomMatrixToBeUsed == 0) ? String.format("%s/G.csv", pathOfGenerator) : String.format("%s/G%d.csv", pathOfGenerator, randomMatrixToBeUsed);
      targetChecking = (randomMatrixToBeUsed == 0) ? String.format("%s/H.csv", pathOfChecking) : String.format("%s/H%d.csv", pathOfChecking, randomMatrixToBeUsed);

        System.out.println("\t>>>> 1. Loading Generator Matrix and Parity-checking Matrix");
        System.out.println(String.format("\t>>>> Loading Generator Matrix from %s", targetGenerator));

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
  
        // filereader as parameter 
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


        // Generate random message based on the given vector dimension (generator matrix row)
        int vectorDimension = generatorMatrix.size();
        randomMessage = Signing.generateRandomVector(vectorDimension);

        // Calculate mG
        encodedMessage = Constant.matrixMultiplication(randomMessage, generatorMatrix);

        
        
        // Copy data from randomMessage to randomMessageFlipped, both refer to different object
        encodedMessageWithError = new ArrayList<ArrayList<Integer>> (encodedMessage.stream().map(e -> new ArrayList<Integer>(e)).collect(Collectors.toList()));
        error = new ArrayList<ArrayList<Integer>> (encodedMessage.stream().map(e -> new ArrayList<Integer>(e)).collect(Collectors.toList()));
        

        /* 
        // Random generate Error Bit
        for(int i = 0; i<error.size(); i++){
          for(int j=0; j<error.get(0).size(); j++){
            error.get(i).set(j, randomGenerator.nextBoolean() ? 0 : 1);
          }
        }
        
        
        for(int i = 0; i<encodedMessageWithError.size(); i++){
          for(int j=0; j<encodedMessageWithError.get(0).size(); j++){
            int value = encodedMessageWithError.get(i).get(j);
            int bitError = error.get(i).get(j);
            encodedMessageWithError.get(i).set(j, (value+bitError)%2);
          }
        }
        */

        // This generatr only one bit error
        // Randomly flip one bit in the random message to simulate error bit
        int randomError = randomGenerator.nextInt((vectorDimension - 0)) + 0;
        Integer bitValue = encodedMessageWithError.get(0).get(randomError);
        encodedMessageWithError.get(0).set(randomError, (bitValue.intValue()==0) ? 1 : 0);
        
        
          // Display the result
          System.out.print("\n\t>>>> Random Message (m): ");
          randomMessage.get(0).stream().forEach(e -> System.out.print(String.format("%d ", e)));
          System.out.println();
          System.out.println();

          System.out.println(String.format("\t>>>> Generator Matrix (G%s): ", (randomMatrixToBeUsed==0) ? "" : Integer.toString(randomMatrixToBeUsed)));
          generatorMatrix.stream().forEach(e -> System.out.println("\t\t" + e));
          System.out.println();

          /* 
          System.out.println(String.format("\t>>>> Parity-checking Matrix (H%s): ", (randomMatrixToBeUsed==0) ? "" : Integer.toString(randomMatrixToBeUsed)));
          parityCheckMatrix.stream().forEach(e -> System.out.println("\t\t" + e));
          System.out.println();
          */ 
          System.out.println();

          System.out.println(String.format("\t>>>> Encoded Message (mG%s): ", (randomMatrixToBeUsed==0) ? "" : Integer.toString(randomMatrixToBeUsed)));
          encodedMessage.stream().forEach(e -> System.out.println("\t\t" + e));
          System.out.println();

          /*
          System.out.println(String.format("\t>>>> Error (e): "));
          error.stream().forEach(e -> System.out.println("\t\t" + e));
          System.out.println();
          //System.out.println(String.format("\t>>>> Index of Error Bit: %d\n", randomError)); 
          */

          System.out.println(String.format("\t>>>> Encoded Message with Error (mG%s + e): ", (randomMatrixToBeUsed==0) ? "" : Integer.toString(randomMatrixToBeUsed)));
          encodedMessageWithError.stream().forEach(e -> System.out.println("\t\t" + e));
          System.out.println();

                   

          // Output the result to csv files
          Timestamp instant= Timestamp.from(Instant.now());  
          String resultDir = ("./output-signing/"+instant).replace(":","-");
          Files.createDirectories(Paths.get(resultDir));
          String outputEncodedMessageE = String.format("%s/mGe.csv", resultDir);
          String outputRandomMessage = String.format("%s/m.csv", resultDir);

          /*
          System.out.println(String.format("\t>>>> Writing result to %s", resultDir));
           */
          System.out.println("\t==== ====");

          // Copy and paste the Generator Matrix as G.csv
          Path copied = Paths.get(String.format("%s/G.csv", resultDir));
          Path originalPath = Paths.get(targetGenerator);
          Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

          // Copy and paste the Parity-checking Matrix as H.csv
          copied = Paths.get(String.format("%s/H.csv", resultDir));
          originalPath = Paths.get(targetChecking);
          Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);


          // Output the 2d Arraylist to csv file
          File csvFile = new File(outputEncodedMessageE); 
          FileWriter outputCsvFile = new FileWriter(csvFile); 

          // create CSVWriter with ',' as separator 
          CSVWriter writer = new CSVWriter(outputCsvFile, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
          List<String[]> data = new ArrayList<String[]>(); 
          for(int j=0; j<encodedMessageWithError.size();j++){
              String outputString = ""+encodedMessageWithError.get(j);
              // Remove the [] from the string
              outputString = outputString.substring(1, outputString.length() - 1);
              String[] rowdata = outputString.split(","); 
              data.add(rowdata); 
          } 

          // Write the generator matrix to the .csv file
          writer.writeAll(data); 
          writer.close();


          csvFile = new File(outputRandomMessage); 
          outputCsvFile = new FileWriter(csvFile); 

          // create CSVWriter with ',' as separator 
          writer = new CSVWriter(outputCsvFile, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
          data = new ArrayList<String[]>(); 
          for(int j=0; j<randomMessage.size();j++){
              String outputString = ""+randomMessage.get(j);
              // Remove the [] from the string
              outputString = outputString.substring(1, outputString.length() - 1);
              String[] rowdata = outputString.split(","); 
              data.add(rowdata); 
          } 

          // Write the generator matrix to the .csv file
          writer.writeAll(data); 
          writer.close();

    } catch(Exception e) {
      // if any error occurs
      e.printStackTrace();
   }
  }

  // A method to check the number is integer or not
  public static boolean isInteger(String number){
    try{
        Integer.parseInt(number);
    }catch(Exception e ){
        return false;
    }
    return true;
  }

  // Method to generate identity matrix
  public static ArrayList<ArrayList<Integer>> generateRandomVector(int vectorDimension){

    Random randomGenerator = new Random();

    // Identity Matrix
    ArrayList<ArrayList<Integer>> randomVector = new ArrayList<ArrayList<Integer>>();

    List<Integer> row  = Arrays.asList(new Integer[vectorDimension]); 
    row = row.stream().map(e -> e = randomGenerator.nextBoolean() ? 0 : 1)
    .collect(Collectors.toList());

    randomVector.add(new ArrayList<Integer>(row));
    return randomVector;
  }
}