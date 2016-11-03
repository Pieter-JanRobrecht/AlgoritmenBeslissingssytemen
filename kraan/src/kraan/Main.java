package kraan;

import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Yard;
import view.View;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            //Laden van de fxml file waarin alle gui elementen zitten
            FXMLLoader loader = new FXMLLoader();
			InputStream s = null;
			try{
				s = getClass().getClassLoader().getResource("Sample.fxml").openStream();
			}catch(Exception e){

			}
            Parent root = (Parent) loader.load(s);

            //Setten van enkele elementen van het hoofdscherm
            primaryStage.setTitle("Kranen probleem");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            //Ophalen van de controller horende bij de view klasse
            View viewController = loader.<View>getController() ;
            assert(viewController != null);

            Controller controller = new Controller();

            //Link tussen controller en view
            viewController.setController(controller);
            controller.addObserver(viewController);
            controller.setFileSmall();
            viewController.initField();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}