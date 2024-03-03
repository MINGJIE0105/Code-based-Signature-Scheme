import java.util.*;
import java.util.stream.*;
import java.io.*; 
import java.lang.System;
import java.sql.Timestamp;  
import java.time.Instant;  
import java.nio.file.*;
import com.opencsv.CSVWriter; 

public class RandomRemoveMatrixSeq extends Thread{

    public static void main(String[] args) throws IOException{

        GeneratorMatrixGenerator generator = new GeneratorMatrixGenerator();
        generator.matricesGeneration();

        ArrayList<ArrayList<Integer>> generatorMatrixG = generator.getGeneratorMatrix();
        ArrayList<ArrayList<Integer>> parityCheckMatrixH = generator.getParityCheckMatrix();

        int matrixRow = generatorMatrixG.size();

        for(int i=0; i<matrixRow; i++){
            System.out.println(generatorMatrixG.get(i));
        }


        System.out.println("Start to randomly remove rows from Generator Matrix");

        // Random object
        Random randomGenerator = new Random();
        
        // Sequential Approach
        int randomRow;
        Timestamp instant= Timestamp.from(Instant.now());  
        String outputDir = ("./output-matrix/"+instant+"-seq").replace(":","-");
        Files.createDirectories(Paths.get(outputDir));
        Files.createDirectories(Paths.get(outputDir+"/G"));
        Files.createDirectories(Paths.get(outputDir+"/H"));
        String outputFile, outputFileParity, outputString;
        PrintWriter out;
        long start, end;

        File csvFile; 

        start = System.currentTimeMillis();
        
        for(int i =0; i<matrixRow; i++){

            // Set the output path to .csv file
            outputFile = (i==0) ? String.format("%s/G/G.csv",outputDir) : String.format("%s/G/G%d.csv",outputDir,i);
            
            csvFile = new File(outputFile); 
            FileWriter outputCsvFile = new FileWriter(csvFile); 

            // create CSVWriter with ',' as separator 
            CSVWriter writer = new CSVWriter(outputCsvFile, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

            // Random index of the row
            randomRow = randomGenerator.nextInt((generatorMatrixG.size()-1) - 0) + 0;
            if(i>0) {
                generatorMatrixG.remove(randomRow);
                parityCheckMatrixH.remove(randomRow);
            }

            List<String[]> data = new ArrayList<String[]>(); 
            for(int j=0; j<generatorMatrixG.size();j++){
                outputString = ""+generatorMatrixG.get(j);
                // Remove the [] from the string
                outputString = outputString.substring(1, outputString.length() - 1);
                String[] rowdata = outputString.split(","); 
                data.add(rowdata); 
            } 

            // Write the generator matrix to the .csv file
            writer.writeAll(data); 
            writer.close();


            // Output parity check matrix
            outputFileParity = (i==0) ? String.format("%s/H/H.csv",outputDir) : String.format("%s/H/H%d.csv",outputDir,i);
            csvFile = new File(outputFileParity); 
            outputCsvFile = new FileWriter(csvFile); 
            writer = new CSVWriter(outputCsvFile, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

            data = new ArrayList<String[]>(); 
            for(int j=0; j<parityCheckMatrixH.size();j++){
                outputString = ""+parityCheckMatrixH.get(j);
                // Remove the [] from the string
                outputString = outputString.substring(1, outputString.length() - 1);
                String[] rowdata = outputString.split(","); 
                data.add(rowdata); 
            } 

            // Write the generator matrix to the .csv file
            writer.writeAll(data); 
            writer.close();
        }
        
        end = System.currentTimeMillis();
        System.out.println(String.format("\nSequential approach for task [%s]: %o (milliseconds)", instant, end-start));
    }
}