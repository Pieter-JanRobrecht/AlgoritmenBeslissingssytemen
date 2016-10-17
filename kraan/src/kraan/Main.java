package kraan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

        	Problem uwProbleem = Problem.fromJson(file);
 
        	System.out.println(uwProbleem.toString());
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
