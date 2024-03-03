import java.util.*;
import java.util.stream.*;
import java.lang.Math;

public class GeneratorMatrixGenerator {

    // Generator Matrix G
    private ArrayList<ArrayList<Integer>> generatorMatrix;
    // Parity-Check Matrix H
    private ArrayList<ArrayList<Integer>> parityCheckMatrix;

    // Defult Constructor
    public GeneratorMatrixGenerator(){
        this.generatorMatrix = new ArrayList<ArrayList<Integer>>();
        this.parityCheckMatrix = new ArrayList<ArrayList<Integer>>();
    }

    public ArrayList<ArrayList<Integer>> getGeneratorMatrix(){
        return this.generatorMatrix;
    }

    public ArrayList<ArrayList<Integer>> getParityCheckMatrix(){
        return this.parityCheckMatrix;
    }


    public ArrayList<ArrayList<Integer>> generateIdentityMatrix(){

        Scanner userInput = new Scanner(System.in);

        // Identity Matrix
        ArrayList<ArrayList<Integer>> identityMatrix = new ArrayList<ArrayList<Integer>>();
        int matrixDimension;

        System.out.println("Enter the dimension for the Identity Matrix:");
        matrixDimension = Integer.parseInt(userInput.nextLine());
        userInput.close();

        List<Integer> row  = Arrays.asList(new Integer[matrixDimension]); 
        for(int i = 0; i<matrixDimension; i++){
            // Fill in 0 for each row
            row = row.stream().map(x ->  0).collect(Collectors.toList());

            row.set(i, 1);

            // Add the row to the identity matrix
            identityMatrix.add(new ArrayList<Integer>(row));
        }
        return identityMatrix;
    }


    // Method to generate identity matrix
    public ArrayList<ArrayList<Integer>> generateIdentityMatrix(int matrixDimension){

        // Identity Matrix
        ArrayList<ArrayList<Integer>> identityMatrix = new ArrayList<ArrayList<Integer>>();

        List<Integer> row  = Arrays.asList(new Integer[matrixDimension]); 
        for(int i = 0; i<matrixDimension; i++){
            // Fill in 0 for each row
            row = row.stream().map(x ->  0).collect(Collectors.toList());

            row.set(i, 1);

            // Add the row to the identity matrix
            identityMatrix.add(new ArrayList<Integer>(row));
        }
        return identityMatrix;
    }


    // Method to convert the given Integer to binary vector
    public List<Integer> integerToBinaryVector(int input, int digit){
        // Convert the input from integer to binary string (with specific digit)
        String binaryString = String.format("%" + digit + "s", Integer.toBinaryString(input)).replace(' ', '0');

        // Convert the binary string to list
        List<Integer> binaryVector = binaryString.chars().mapToObj(c -> (Integer) (c-'0')).toList();

        return binaryVector;
    }


    // Method to generate generator matrix for Hamming Code
    public void matricesGeneration(){

        // Parameter for Hamming Code
            // Message Length = 2^r - r - 1
            // Block length = 2^r - 1
        int r, messageLength, blockLength;
         // Generator Matrix
        ArrayList<ArrayList<Integer>> generatorMatrix = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> parityCheckMatrix = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> matrixP = new ArrayList<ArrayList<Integer>>();
        Scanner userInput = new Scanner(System.in);


        System.out.println("Enter the parameter r for the Hamming Code (r>2):");
        r = Integer.parseInt(userInput.nextLine());
        blockLength = ((int) Math.pow(2,r)) - 1;
        messageLength = ((int) Math.pow(2,r)) - r - 1;
        //userInput.close();

        System.out.println(String.format("Generating generator matrix for Hamming Code(%d,%d)", blockLength, messageLength));

        ArrayList<ArrayList<Integer>> identityMatrix = generateIdentityMatrix(r);

        List<Integer> possibleNumber = IntStream.range(0, (int) Math.pow(2,r)).boxed().collect(Collectors.toList());
        for(int i=0; i<possibleNumber.size(); i++){
            matrixP.add(new ArrayList<Integer> (integerToBinaryVector(possibleNumber.get(i), r)));
        }

        matrixP.removeAll(identityMatrix);
        matrixP.remove(0);
        Collections.shuffle(matrixP);

        ArrayList<ArrayList<Integer>> identityMatrix2 = generateIdentityMatrix(matrixP.size());

        for(int i=0; i<matrixP.size(); i++){
            generatorMatrix.add(identityMatrix2.get(i));
            generatorMatrix.get(i).addAll(matrixP.get(i));
        }
        
        this.generatorMatrix = generatorMatrix;


        parityCheckMatrix.addAll(matrixP);
        parityCheckMatrix.addAll(identityMatrix);
        this.parityCheckMatrix = parityCheckMatrix;
    }


     

}