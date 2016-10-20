package view;

import java.util.Observable;
import java.util.Observer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;


/**
 * Created by Pieter-Jan on 17/10/2016.
 */
public class View implements Observer {

    @FXML
    private TabPane tabPane;

    @FXML
    void loadFileBigNietGeschrankt(ActionEvent event) {

    }

    @FXML
    void loadFileBigSchrankt(ActionEvent event) {

    }

    @FXML
    void loadFileSmall(ActionEvent event) {

    }


    @Override
    public void update(Observable o, Object arg) {

    }
}
