import java.util.*;
import java.util.Scanner;
import java.io.*;
import java.io.IOException;
import java.lang.System;
import java.nio.file.*;

public class MainMenu {
        
    public static void main(String[] args){

        // Check if the necessary folders are created. If the folders are not created, the program creates the folders
        try{
            if (!Files.exists(Paths.get("output-matrix"))) Files.createDirectories(Paths.get("output-matrix"));
            if (!Files.exists(Paths.get("output-signing"))) Files.createDirectories(Paths.get("output-signing"));
        } catch(IOException ex){
            System.out.println (ex.toString());
        }


        String menu = "****MAIN MENU****\n" +
            "1. Generate Generator Matrix and Parity Check Matrix (Sequential)\n" +
            "2. Generate Generator Matrix and Parity Check Matrix (Parallel)\n" +
            "3. Signing\n" +
            "4. Verification \n" +
            "Q. Quit\n" +
            "Select the operation to be executed:";
        
        String selection;
        Boolean isContinue = true;

        Scanner scanner = new Scanner(System.in);

        while(isContinue){            

            Constant.clearConsole();

            System.out.println(menu);
            selection = scanner.nextLine();
            switch(selection.toUpperCase()){
                case "1":
                    try{
                        RandomRemoveMatrixSeq.main(null);
                    } catch(IOException ex){
                        System.out.println (ex.toString());
                    }
                    Constant.waitConsole();
                    break;
                case "2":
                    try{
                        RandomRemoveMatrixParallel.main(null);
                    } catch(IOException ex){
                        System.out.println (ex.toString());
                    }
                    Constant.waitConsole();
                    break;
                case "3":
                    Signing.main(null);
                    break;
                case "4":
                    Verification.main(null);
                    break;
                case "Q":
                    isContinue = false;
                    break;
                default:
                    break;
            }
            
            System.out.println("");
        }

        scanner.close();
        System.out.println("Exiting program...");
    }
}