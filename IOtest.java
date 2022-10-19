package byow.Core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class IOtest {

    public static void main(String[] args) {
        try {
            File saveFile = new File("savefile.txt");
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // write text to the file
        try {
            FileWriter myWriter = new FileWriter("savefile.txt");
            myWriter.write("suceed");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter("savefile.txt");
            myWriter.write("suceed");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File myObj = new File("savefile.txt");
            Scanner myReader = new Scanner(myObj);
            String data = myReader.nextLine();
            System.out.println(data);
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
