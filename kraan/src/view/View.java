package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import controller.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import kraan.Problem;
import kraan.Slot;


/**
 * Created by Pieter-Jan on 17/10/2016.
 */
public class View implements Observer {

    private Controller controller;

    public void setController(Controller c) {
        this.controller = c;
    }

    @FXML
    private ComboBox<String> dropDown;

    @FXML
    private VBox vBox;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    void doStep(ActionEvent event) {

    }

    @FXML
    void doComplete(ActionEvent event) {

    }

    public void initField() {
        Problem huidigProbleem = controller.getHuidigProbleem();
        int aantalLevels = huidigProbleem.getMaxLevels();

        initVBox();
        initDropDown(aantalLevels);
    }

    public void resetField(){
        //Reset van gantry velden
        int aantalElementenBehouden = 3;
        int aantalElementen = vBox.getChildren().size();
        for(int i = aantalElementen ; i>aantalElementenBehouden; i--){
            vBox.getChildren().remove(i-1);
        }

        dropDown.getItems().clear();
    }

    private void initVBox() {
        int aantalGantries = controller.getHuidigProbleem().getGantries().size();

        for (int i = 1; i <= aantalGantries; i++) {
            String gantryLabelName = "Gantry " + i;
            Label gantry = new Label(gantryLabelName);
            gantry.setPadding(new Insets(5, 5, 5, 5));

            TextField textField = new TextField();
            textField.setEditable(false);
            textField.setId("gantryText" + i);
            textField.setPromptText("Container ID");
            textField.setPadding(new Insets(0, 5, 5, 5));

            vBox.getChildren().addAll(gantry, textField);
        }
    }

    private void initDropDown(int aantalLevels) {
        for (int i = 0; i < aantalLevels; i++) {
            dropDown.getItems().add("Level " + i);
        }

        dropDown.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String oldValue, String newValue) {
                if(newValue != null) {
                    int level = Integer.parseInt(newValue.split(" ")[1]);
                    anchorPane.getChildren().clear();
                    showLevel(level);
                }
            }
        });
    }

    private void showLevel(int level) {
        Problem huidigProbleem = controller.getHuidigProbleem();

        int lengteX = (huidigProbleem.getMaxX() - 10) / 10;
        int lengteY = huidigProbleem.getMaxY() / 10;

        makeGrid(lengteX * 2, lengteY * 2);
        fillGrid(level);
    }

    private void makeGrid(int lengteX, int lengteY) {
//        gridPane.getChildren().clear();

        GridPane gridPane = new GridPane();
        gridPane.setId("gridID");

        AnchorPane.setTopAnchor(gridPane,0.0);
        AnchorPane.setBottomAnchor(gridPane,0.0);
        AnchorPane.setLeftAnchor(gridPane,0.0);
        AnchorPane.setRightAnchor(gridPane,0.0);

        anchorPane.getChildren().add(gridPane);

        //Aantal kolommen zetten
        for (int j = 0; j < lengteX; j++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / lengteX);
            gridPane.getColumnConstraints().add(colConst);
        }

        //Aantal rijen zetten
        for (int j = 0; j < lengteY; j++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / lengteY);
            gridPane.getRowConstraints().add(rowConst);
        }
    }

    private void fillGrid(int level) {
        List<Slot> slots = controller.getHuidigProbleem().getSlots();

        GridPane gridPane = (GridPane) anchorPane.lookup("#gridID");
        int cx = 0;
        int cy = 0;
        for (Slot s : slots) {
            if (s.getZ() == level) {

                if (s.getItem() != null) {
                    cx = s.getCenterX() / 5;
                    cy = s.getCenterY() / 5;
                }

                List<Pane> canvassen = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    Pane canvas = new Pane();
                    canvassen.add(canvas);
                }

                canvassen.get(0).setStyle("-fx-background-color: lightgrey; -fx-border-color: black lightgrey lightgrey black;");
                canvassen.get(1).setStyle("-fx-background-color: lightgrey; -fx-border-color: lightgrey lightgrey black black;");
                canvassen.get(2).setStyle("-fx-background-color: lightgrey; -fx-border-color: black black lightgrey lightgrey;");
                canvassen.get(3).setStyle("-fx-background-color: lightgrey; -fx-border-color: lightgrey black black lightgrey;");
                if(cx != 0 && cy != 0) {
                    gridPane.add(canvassen.get(0), cx - 1, cy - 1);
                    gridPane.add(canvassen.get(1), cx - 1, cy);
                    gridPane.add(canvassen.get(2), cx, cy - 1);
                    gridPane.add(canvassen.get(3), cx, cy);
                }
            }
        }
    }

    @FXML
    void loadFileBigNietGeschrankt(ActionEvent event) {
        controller.setFileBigNietGeschrankt();
        resetField();
        initField();
    }

    @FXML
    void loadFileBigSchrankt(ActionEvent event) {
        controller.setFileBigGeschrankt();
        resetField();
        initField();
    }

    @FXML
    void loadFileSmall(ActionEvent event) {
        controller.setFileSmall();
        resetField();
        initField();
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
