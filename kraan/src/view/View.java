package view;

import java.util.Observable;
import java.util.Observer;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import kraan.Problem;


/**
 * Created by Pieter-Jan on 17/10/2016.
 */
public class View implements Observer {

    private Controller controller;

    public void setController(Controller c){
        this.controller = c;
    }

    @FXML
    private TabPane tabPane;

    private void initTabField(){
        Problem huidigProbleem = controller.getHuidigProbleem();
        int lengteX = huidigProbleem.getMaxX();
//        System.out.println(lengteX);
    }

    @FXML
    void loadFileBigNietGeschrankt(ActionEvent event) {
        controller.setFileBigNietGeschrankt();
        initTabField();
    }

    @FXML
    void loadFileBigSchrankt(ActionEvent event) {
        controller.setFileBigGeschrankt();
        initTabField();
    }

    @FXML
    void loadFileSmall(ActionEvent event) {
        controller.setFileSmall();
        initTabField();
    }

    @FXML
    void startSimulation(ActionEvent event) {
        //TODO write simulation procedure
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
