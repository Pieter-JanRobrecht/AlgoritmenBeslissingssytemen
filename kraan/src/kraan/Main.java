package kraan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Yard;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 
        try {
			//Misschien zorgt deze lijn voor problemen
			//Zoja, pech gehad
			File file = new File(Main.class.getClassLoader().getResource("1_50_50_10_FALSE_60_25_100.json").toURI());
//        	File file = new File("./data/1_50_50_10_FALSE_60_25_100.json");
			//File file = new File("./data/testInput.json");

        	Problem uwProbleem = Problem.fromJson(file);
 
        	
        	Yard y = new Yard(uwProbleem);
        	//y.printOutYard();
        	//y.printHash();
        	
        	int inLimit = uwProbleem.getInputJobSequence().size();
        	int outLimit = uwProbleem.getOutputJobSequence().size();
        	int limit = Math.max(inLimit, outLimit);
        	for(int i = 0; i < limit; i++) {
        		//input job:
        		if(i<inLimit) {
        			y.executeJob(uwProbleem.getInputJobSequence().get(i), "INPUT");
                	//y.printOutYard();
        		}
        		//output job:
        		if(i<outLimit) {
        			y.executeJob(uwProbleem.getOutputJobSequence().get(i), "OUTPUT");
                	//y.printOutYard();
        		}
        	}
        	
        	System.out.println("Done!");
        	System.out.println("Tasks in backlog IN (" + y.getBacklogIN().size()+"): " + y.getBacklogIN().toString());
        	System.out.println("Tasks in backlog OUT (" + y.getBacklogOUT().size()+"): " + y.getBacklogOUT().toString());
        	System.out.println("\n\n\n\n");
        	for(Job j : y.getBacklogIN()) {
        		y.executeJob(j, "INPUT");
        	}
        	
        	for(Job j : y.getBacklogOUT()) {
        		y.executeJob(j, "OUTPUT");
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			Parent root = (Parent) loader.load(getClass().getClassLoader().getResource("Sample.fxml").openStream());

			primaryStage.setTitle("Kranen probleem");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
