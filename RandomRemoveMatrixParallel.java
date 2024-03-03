import java.util.*;
import java.util.stream.*;
import java.io.PrintWriter;
import java.io.FileWriter; 
import java.io.IOException;
import java.lang.System;
import java.sql.Timestamp;  
import java.time.Instant;  
import java.nio.file.*;

public class RandomRemoveMatrixParallel extends Thread{

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
        
        int randomRow;
        Timestamp instant= Timestamp.from(Instant.now());  
        String outputDir = ("./output-matrix/"+instant+"-parallel").replace(":","-");
        Files.createDirectories(Paths.get(outputDir));
        Files.createDirectories(Paths.get(outputDir+"/G"));
        Files.createDirectories(Paths.get(outputDir+"/H"));
        String outputFile, outputString;
        PrintWriter out;
        long start, end;

        start = System.currentTimeMillis();

        // Parallel Approach
        // Generate list of index form 0 until matrixRow-1 (matrixRow is not included)
        List<Integer> randomIndices = IntStream.range(0, matrixRow).boxed().collect(Collectors.toList());
        // Generate list of index form 0 until matrixRow-1 (matrixRow-1 is included)
        List<Integer> task = IntStream.rangeClosed(0, matrixRow-1).boxed().collect(Collectors.toList());
        // Shuffle the indices to produce an array of random index
        Collections.shuffle(randomIndices); 
        // Remove the last random index
        randomIndices.remove(randomIndices.size()-1);

        // Set number of thread based on number of Processors
        int NUM_CPU = (Runtime.getRuntime().availableProcessors() > matrixRow) ? matrixRow : Runtime.getRuntime().availableProcessors();
        // Offset, size
        List<Integer> partitionedTask;
        // Calculate maximum tasks for the thread
        int numTask = (int) Math.ceil((float)matrixRow / (float)NUM_CPU);
        int offset = 0, endIdx, taskSize=numTask;

        for(int i=0; i<NUM_CPU; i++){
            
            offset = (i==0) ? 0 : offset+numTask;
            endIdx = offset+taskSize;
            endIdx = (endIdx<matrixRow) ? endIdx : matrixRow;
            
            if(offset > endIdx) break;

            // Distribute the task based on the offset and endIdx
            partitionedTask = task.subList(offset, endIdx);
            
            // Create an object for the parallel processing
            ParallelProcessMatrix parallelProcessing = new ParallelProcessMatrix(partitionedTask, generatorMatrixG, parityCheckMatrixH, randomIndices, outputDir);
            // Invoking Thread using start() method
            parallelProcessing.start();

        }
        
        end = System.currentTimeMillis();
        System.out.println(String.format("\nParallel approach for task [%s]: %o (milliseconds)", instant, end-start));

    }
}