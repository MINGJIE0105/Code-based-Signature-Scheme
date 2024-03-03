import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Constant {
    
    // Clear console
    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
                System.out.print("\033\143");
            }
        } catch (IOException | InterruptedException ex) {}
    }

    //
    public static void waitConsole(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press any key to continue...");
        scanner.nextLine();
    }

    // A customized Matrix multiplication that wil mod each of element by 2
    public static ArrayList<ArrayList<Integer>> matrixMultiplication(ArrayList<ArrayList<Integer>> matrixA, ArrayList<ArrayList<Integer>> matrixB){

        // generatorMatrix.size() is number of row, generatorMatrix.get(0).size() is number of column
        int resultRow = matrixA.size();
        int resultColumn = matrixB.get(0).size();

        int interRow = matrixB.size();

        // Create matrix
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

        for(int counterRow=0; counterRow<resultRow; counterRow++){
        List<Integer> row  = Arrays.asList(new Integer[resultColumn]);       
        for(int counterColumn=0; counterColumn<resultColumn; counterColumn++){
            int sum = 0;
            for(int counterInter=0; counterInter<interRow; counterInter++){
            sum = sum + (matrixA.get(counterRow).get(counterInter) * matrixB.get(counterInter).get(counterColumn));
            row.set(counterColumn, sum);
            }
        }
        row = row.stream().map(e -> e%2).collect(Collectors.toList());
        result.add(new ArrayList<Integer>(row));
        }
        
        return result;
    }
}
