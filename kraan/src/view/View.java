package view;

import java.io.File;
import java.io.IOException;
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
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import kraan.Problem;
import kraan.Slot;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;
import org.json.simple.parser.ParseException;

import javax.management.Notification;


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
    private VBox mainBox;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField stepField;

    @FXML
    void doStep(ActionEvent event) {
        String fieldString = stepField.getText();
        try {
            int aantalStappen = Integer.parseInt(fieldString);

            if (aantalStappen + controller.getKlok() > controller.getLimit())
                throw new Exception();

            for (int i = 0; i < aantalStappen - 1 && controller.getKlok() < controller.getLimit(); i++) {
                controller.doStep(false);
            }
            controller.doStep(true);
        } catch (NumberFormatException e) {
            Notifications.create()
                    .title("ERROR")
                    .text("It's number of steps, not string of steps")
                    .showWarning();
        } catch (Exception ex) {
            int max = controller.getLimit() - controller.getKlok();
            System.out.println(ex.getMessage());
            Notifications.create()
                    .title("ERROR")
                    .text("Amount of steps is too big, maximum allowed is: " + max)
                    .showWarning();
        }
    }

    @FXML
    void doComplete(ActionEvent event) throws IOException {
        int klokIn = controller.getKlokIN();
        int klokOut = controller.getKlokOUT();
        int limitIn = controller.getInLimit();
        int limitOut = controller.getOutLimit();
        while (klokIn < limitIn || klokOut < limitOut) {
            controller.doStep(false);

            klokIn = controller.getKlokIN();
            klokOut = controller.getKlokOUT();
            limitIn = controller.getInLimit();
            limitOut = controller.getOutLimit();
        }
        controller.doStep(true);
//        for (int i = 0; i < controller.getLimit() - 1; i++) {
//            controller.doStep(false);
//        }
//        controller.doStep(true);
    }

    public void initField() {
        Problem huidigProbleem = controller.getHuidigProbleem();
        int aantalLevels = huidigProbleem.getMaxLevels();

        initVBox();
        initDropDown(aantalLevels);
    }

    private void resetField() {
        controller.reset();

        //Reset van gantry velden
        int aantalElementenBehouden = 4;
        int aantalElementen = vBox.getChildren().size();
        for (int i = aantalElementen; i > aantalElementenBehouden; i--) {
            vBox.getChildren().remove(i - 1);
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
                if (newValue != null) {
                    int level = Integer.parseInt(newValue.split(" ")[1]);

                    showLevel(level);
                }
            }
        });
        dropDown.getSelectionModel().select(0);
    }

    private void showLevel(int level) {
        Problem huidigProbleem = controller.getHuidigProbleem();
        anchorPane.getChildren().clear();

        int lengteX = (huidigProbleem.getMaxX() - 10) / 10;
        int lengteY = huidigProbleem.getMaxY() / 10;

        makeGrid(lengteX * 2, lengteY * 2);
        fillGrid(level);
    }

    private void makeGrid(int lengteX, int lengteY) {

        GridPane gridPane = new GridPane();
        gridPane.setId("gridID");

        AnchorPane.setTopAnchor(gridPane, 0.0);
        AnchorPane.setBottomAnchor(gridPane, 0.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);

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
        Slot[][] yard = controller.getYard().getYard();

        GridPane gridPane = (GridPane) anchorPane.lookup("#gridID");
        int cx = 0;
        int cy = 0;

        for (int i = 0; i < yard.length; i++) {
            Slot slot = yard[i][level];
            if (slot != null && slot.getItem() != null) {
                cx = slot.getCenterX() / 5;
                cy = slot.getCenterY() / 5;
            }
            List<Pane> canvassen = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                Pane canvas = new Pane();
                canvassen.add(canvas);
            }

            canvassen.get(0).setStyle("-fx-background-color: lightgrey; -fx-border-color: black lightgrey lightgrey black;");
            canvassen.get(1).setStyle("-fx-background-color: lightgrey; -fx-border-color: lightgrey lightgrey black black;");
            canvassen.get(2).setStyle("-fx-background-color: lightgrey; -fx-border-color: black black lightgrey lightgrey;");
            canvassen.get(3).setStyle("-fx-background-color: lightgrey; -fx-border-color: lightgrey black black lightgrey;");
            if (cx != 0 && cy != 0) {
                gridPane.add(canvassen.get(0), cx - 1, cy - 1);
                gridPane.add(canvassen.get(1), cx - 1, cy);
                gridPane.add(canvassen.get(2), cx, cy - 1);
                gridPane.add(canvassen.get(3), cx, cy);
            }
        }
    }

    @FXML
    void loadFileBigNietGeschrankt(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(
                new File(".")
        );
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog((Stage) dropDown.getScene().getWindow());

        if(file != null){
            try {
                controller.setHuidigProbleem(Problem.fromJson(file));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            resetField();
            initField();
        }else {
            Notifications.create()
                    .title("ERROR")
                    .text("Gelieve een bestand te kiezen")
                    .showError();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        //TODO voor stukken up te daten
        try {
            String output = dropDown.getSelectionModel().getSelectedItem().toString();
            int level = Integer.parseInt(output.split(" ")[1]);

            showLevel(level);
        } catch (Exception e) {
            //Hier moet er toch niks gebeuren.
            //FU RHINO VOOR ALLES TE FUCKEN
        }
    }
}
