package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import kraan.Problem;
import kraan.Slot;


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
        //Alle vorige tabs verwijderen
        tabPane.getTabs().clear();

        Problem huidigProbleem = controller.getHuidigProbleem();
        int lengteX = (huidigProbleem.getMaxX()-10)/10;
        int lengteZ = huidigProbleem.getMaxLevels();
        int lengteY = huidigProbleem.getMaxY()/10;

        makeTabs(lengteX*2,lengteY*2,lengteZ);
        fillFieldWithContainers();

    }

    private void fillFieldWithContainers() {
        List<Slot> slots = controller.getHuidigProbleem().getSlots();

        //Enkele de slots nemen waar een item in zit
        int cx=0;
        int cy=0;
        for(Slot s : slots){
            if(s.getItem()!=null){
                cx = s.getCenterX()/5;
                cy = s.getCenterY()/5;
            }
            List<Pane> canvassen = new ArrayList<>();
            for(int i =0;i< 4;i++){
                Pane canvas = new Pane();
                canvas.setStyle("-fx-background-color: black;");
                canvassen.add(canvas);
            }

            GridPane grid = (GridPane) tabPane.getTabs().get(s.getZ()).getContent();
            grid.add(canvassen.get(0), cx-1 , cy-1);
            grid.add(canvassen.get(1), cx-1 , cy);
            grid.add(canvassen.get(2), cx , cy-1);
            grid.add(canvassen.get(3), cx , cy);
        }
    }

    private void makeTabs(int lengteX, int lengteY, int lengteZ) {
        //Alle tabs toevoegen
        for (int i = 0; i < lengteZ; i++) {
            Tab tab = new Tab();
            tab.setText("Tab" + i);
            GridPane grid = new GridPane();
            grid.setGridLinesVisible(true);

            //Aantal kolommen zetten
            for (int j = 0; j < lengteY; j++) {
                ColumnConstraints colConst = new ColumnConstraints();
                colConst.setPercentWidth(100.0 / lengteX);
                grid.getColumnConstraints().add(colConst);
            }

            //Aantal rijen zetten
            for (int j = 0; j < lengteX; j++) {
                RowConstraints rowConst = new RowConstraints();
                rowConst.setPercentHeight(100.0 / lengteY);
                grid.getRowConstraints().add(rowConst);
            }

            tab.setContent(grid);
            tabPane.getTabs().add(tab);
        }
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
