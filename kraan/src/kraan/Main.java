package kraan;

import java.io.File;
import java.io.FileReader;
import java.net.URL;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 
        try {
        	File file = new File("./data/1_50_50_10_FALSE_60_25_100.json");

        	
        	Problem uwProbleem = Problem.fromJson(file);
 
        	System.out.println(uwProbleem.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
