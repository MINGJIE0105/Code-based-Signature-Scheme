import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.io.IOException;
import java.lang.System;
import java.nio.file.*;
import com.opencsv.CSVWriter; 

public class ParallelProcessMatrix extends Thread {

    // Distributed task
    private List<Integer> partitionedTask;
    // Random Indices for specifying row to be removed
    private List<Integer> randomIndices;
    // Generator Matrix
    private ArrayList<ArrayList<Integer>> generatorMatrixG;
    // Parity Check Matrix
    private ArrayList<ArrayList<Integer>> parityCheckMatrixH;
    private String outputDir;

    private ArrayList<ArrayList<Integer>> tempMatrixG;
    private ArrayList<ArrayList<Integer>> tempMatrixH;
    private List<Integer> tempRandomIndices;
    private PrintWriter out;

    // Default Constructor
    public ParallelProcessMatrix(){
    }

    // Constructor
    public ParallelProcessMatrix(List<Integer> partitionedTask, ArrayList<ArrayList<Integer>> generatorMatrixG, ArrayList<ArrayList<Integer>> parityCheckMatrixH, List<Integer> randomIndices, String outputDir){
        this.partitionedTask = partitionedTask;
        this.generatorMatrixG = generatorMatrixG;
        this.parityCheckMatrixH = parityCheckMatrixH;
        this.randomIndices = randomIndices;
        this.outputDir = outputDir;
    }

    // Start the thread 
    public void run() 
    {
        int taskId;   
        String outputFile, outputFileParity, outputString;

        // Carry out the distributed task
        for(int i=0; i< partitionedTask.size();i++){

            taskId = partitionedTask.get(i);
            outputFile = (taskId==0) ? String.format("%s/G/G.csv",outputDir) : String.format("%s/G/G%d.csv",outputDir,taskId);
            outputFileParity = (taskId==0) ? String.format("%s/H/H.csv",outputDir) : String.format("%s/H/H%d.csv",outputDir,taskId);
            tempMatrixG = new ArrayList<ArrayList<Integer>>(generatorMatrixG);
            tempMatrixH = new ArrayList<ArrayList<Integer>>(parityCheckMatrixH);
            tempRandomIndices = randomIndices.subList(0, taskId);


            // Delete the matrix row based on the random indices
            if(taskId>0) {
                tempRandomIndices.stream().sorted(Comparator.reverseOrder()).forEach(x->tempMatrixG.remove(x.intValue()));
                tempRandomIndices.stream().sorted(Comparator.reverseOrder()).forEach(x->tempMatrixH.remove(x.intValue()));
            }

            try{
                File csvFile = new File(outputFile); 
                FileWriter outputCsvFile = new FileWriter(csvFile); 

                // create CSVWriter with ',' as separator 
                CSVWriter writer = new CSVWriter(outputCsvFile, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

                List<String[]> data = new ArrayList<String[]>(); 
                for(int j=0; j<tempMatrixG.size();j++){
                    outputString = ""+tempMatrixG.get(j);
                    // Remove the []
                    outputString = outputString.substring(1, outputString.length() - 1);
                    String[] rowdata = outputString.split(","); 
                    data.add(rowdata); 
                } 

                // Write the generator matrix to the .csv file
                writer.writeAll(data); 
                writer.close();


                File csvFileParity = new File(outputFileParity); 
                FileWriter outputCsvFileParity = new FileWriter(csvFileParity); 

                writer = new CSVWriter(outputCsvFileParity, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
                data = new ArrayList<String[]>(); 
                for(int j=0; j<tempMatrixH.size();j++){
                    outputString = ""+tempMatrixH.get(j);
                    // Remove the []
                    outputString = outputString.substring(1, outputString.length() - 1);
                    String[] rowdata = outputString.split(","); 
                    data.add(rowdata); 
                } 

                // Write the generator matrix to the .csv file
                writer.writeAll(data); 
                writer.close();

            }
            catch(IOException ex){
                System.out.println (ex.toString());
            }

        }
    }
}