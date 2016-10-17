package kraan;

import java.io.File;
import java.io.FileReader;
import java.net.URL;

import model.Yard;

import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 
        try {
        	//File file = new File("./data/1_50_50_10_FALSE_60_25_100.json");
			File file = new File("./data/testInput.json");
        	
        	Problem uwProbleem = Problem.fromJson(file);
 
        	
        	Yard y = new Yard(uwProbleem);
        	y.printOutYard();
        	y.printHash();
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
