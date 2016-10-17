package kraan;

import java.io.File;
import java.io.FileReader;
import java.net.URL;

import model.Yard;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 
        try {
        	//File file = new File("./data/1_50_50_10_TRUE_60_25_100.json");
        	File file = new File("./data/testInput.json");
        	
        	Problem uwProbleem = Problem.fromJson(file);
 
        	
        	Yard y = new Yard(uwProbleem);
        	y.printOutYard();
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
