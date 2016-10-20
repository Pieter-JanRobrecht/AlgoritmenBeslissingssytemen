package controller;

import kraan.Main;
import kraan.Problem;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Observable;

/**
 * Created by Pieter-Jan on 20/10/2016.
 */
public class Controller extends Observable{
//    setChanged();
//    notifyObservers();

    private Problem huidigProbleem;

    public Controller() {
    }

    public void setFileBigNietGeschrankt() {
        File file = null;

        try {
            file = new File(Main.class.getClassLoader().getResource("1_50_50_10_FALSE_60_25_100.json").toURI());
        } catch (URISyntaxException e) {
            System.out.println("Problem with loading file: 1_50_50_10_FALSE_60_25_100.json");
            System.out.println("Using a different method to load the file");
            file = new File("./data/1_50_50_10_FALSE_60_25_100.json");
        }

        try {
            huidigProbleem = Problem.fromJson(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Problem with parsing the file: 1_50_50_10_FALSE_60_25_100.json");
        }
    }

    public void setFileBigGeschrankt() {
        File file = null;

        try {
            file = new File(Main.class.getClassLoader().getResource("1_50_50_10_TRUE_60_25_100.json").toURI());
        } catch (URISyntaxException e) {
            System.out.println("Problem with loading file: 1_50_50_10_TRUE_60_25_100.json");
            System.out.println("Using a different method to load the file");
            file = new File("./data/1_50_50_10_TRUE_60_25_100.json");
        }

        try {
            huidigProbleem = Problem.fromJson(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Problem with parsing the file: 1_50_50_10_TRUE_60_25_100.json");
        }
    }

    public void setFileSmall() {
        File file = null;

        try {
            file = new File(Main.class.getClassLoader().getResource("testInput.json").toURI());
        } catch (URISyntaxException e) {
            System.out.println("Problem with loading file: testInput.json");
            System.out.println("Using a different method to load the file");
            file = new File("./data/testInput.json");
        }

        try {
            huidigProbleem = Problem.fromJson(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Problem with parsing the file: testInput.json");
        }
    }

    public Problem getHuidigProbleem() {
        return huidigProbleem;
    }
}
