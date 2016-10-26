package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import kraan.Problem;
import kraan.Slot;

import javax.swing.text.*;


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

    @FXML
    private VBox vBox;

    private void initTabField(){
        //Alle vorige tabs verwijderen
        tabPane.getTabs().clear();

        Problem huidigProbleem = controller.getHuidigProbleem();
        int lengteX = (huidigProbleem.getMaxX()-10)/10;
        int lengteZ = huidigProbleem.getMaxLevels();
        int lengteY = huidigProbleem.getMaxY()/10;

        makeTabs(lengteX*2,lengteY*2,lengteZ);
//        fillFieldWithContainers();

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
            TableView table = (TableView) tabPane.getTabs().get(s.getZ()).getContent();
            table.getColumns().get
//            List<Pane> canvassen = new ArrayList<>();
//            for(int i =0;i< 4;i++){
//                Pane canvas = new Pane();
//                canvassen.add(canvas);
//            }
//
//            GridPane grid = (GridPane) tabPane.getTabs().get(s.getZ()).getContent();
//            canvassen.get(0).setStyle("-fx-background-color: lightgrey; -fx-border-color: black lightgrey lightgrey black;");
//            canvassen.get(1).setStyle("-fx-background-color: lightgrey; -fx-border-color: lightgrey lightgrey black black;");
//            canvassen.get(2).setStyle("-fx-background-color: lightgrey; -fx-border-color: black black lightgrey lightgrey;");
//            canvassen.get(3).setStyle("-fx-background-color: lightgrey; -fx-border-color: lightgrey black black lightgrey;");
//            grid.add(s.pane, cx-1 , cy-1);
//            grid.add(s.pane, cx-1 , cy);
//            grid.add(s.pane, cx , cy-1);
//            grid.add(s.pane, cx , cy);
        }
    }

    private void makeTabs(int lengteX, int lengteY, int lengteZ) {
        //Alle tabs toevoegen
        for (int i = 0; i < lengteZ; i++) {
            Tab tab = new Tab();
            tab.setText("Tab" + i);


            ObservableList<String> nullData = FXCollections.observableArrayList();
            TableView<String> table = new TableView<>();

            table.setEditable(false);

            for (int j = 0; j < lengteX; j++) {
                String kolomNaam = "kolom" + j;
                TableColumn hulp = new TableColumn(kolomNaam);
                hulp.setCellValueFactory(new PropertyValueFactory<String,String>(kolomNaam));
                table.getColumns().add(hulp);
            }

            for(int u =0;u<lengteY;u++) {
//                List<String> lijst = new ArrayList<>();
//                nullData.add(lijst);
//                for (int j = 0; j < lengteX; j++) {
                    nullData.add(""+u);
//                }
            }

            table.setItems(nullData);

            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            tab.setContent(table);
            tabPane.getTabs().add(tab);
        }
    }

    private void initVBox(){
        int aantalGantries = controller.getHuidigProbleem().getGantries().size();

        int aantalElementenBehouden = 3;
        int aantalElementen = vBox.getChildren().size();
        for(int i = aantalElementen ; i>aantalElementenBehouden; i--){
            vBox.getChildren().remove(i-1);
        }

        for(int i = 1; i<=aantalGantries;i++){
            String gantryLabelName = "Gantry " + i;
            Label gantry = new Label(gantryLabelName);
            gantry.setPadding(new Insets(5,5,5,5));

            TextField textField = new TextField();
            textField.setEditable(false);
            textField.setId("gantryText"+ i);
            textField.setPromptText("Container ID");
            textField.setPadding(new Insets(0,5,5,5));

            vBox.getChildren().addAll(gantry, textField);
        }
    }

    @FXML
    void loadFileBigNietGeschrankt(ActionEvent event) {
        controller.setFileBigNietGeschrankt();
        initTabField();
        initVBox();
    }

    @FXML
    void loadFileBigSchrankt(ActionEvent event) {
        controller.setFileBigGeschrankt();
        initTabField();
        initVBox();
    }

    @FXML
    void loadFileSmall(ActionEvent event) {
        controller.setFileSmall();
        initTabField();
        initVBox();
    }

    @FXML
    void doStep(ActionEvent event) {
//        controller.doStep(true);
    }

    @FXML
    void doComplete(ActionEvent event) {
//        controller.doStep(false);
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @FXML
    public void initialize(){


    }
}
