package controller;

import javafx.stage.Stage;
import kraan.Main;
import kraan.Problem;
import model.Yard;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Observable;

/**
 * Created by Pieter-Jan on 20/10/2016.
 */
public class Controller extends Observable {

    private Problem huidigProbleem;

    private int limit, inLimit, outLimit, klok, klokIN, klokOUT;

    private Yard yard;
  

    private Stage stage;


    public Controller(Stage primaryStage) throws IOException {
        stage = primaryStage;
    }


    public void reset() {
        yard = new Yard(huidigProbleem, stage);
        // y.printOutYard();
        // y.printHash();

        inLimit = huidigProbleem.getInputJobSequence().size();
        outLimit = huidigProbleem.getOutputJobSequence().size();
        limit = Math.max(inLimit, outLimit);
        klok = 0;
        klokIN = 0;
        klokOUT = 0;
    }

    public void doStep(boolean notify) throws IOException {
        if (debug && klokIN < huidigProbleem.getInputJobSequence().size())
            System.out.println("IN: " + huidigProbleem.getInputJobSequence().get(klokIN).getItem().getId());
        if (debug && klokOUT < huidigProbleem.getOutputJobSequence().size())
            System.out.println("OUT: " + huidigProbleem.getOutputJobSequence().get(klokOUT).getItem().getId());


  
        if (huidigProbleem.getOutputJobSequence().size() < klokOUT && huidigProbleem.getInputJobSequence().size() < klokIN)
            if (huidigProbleem.getOutputJobSequence().get(klokOUT).getItem().getId() == huidigProbleem.getInputJobSequence().get(klokIN).getItem().getId()) {
                if (yard.executeJob(huidigProbleem.getInputJobSequence().get(klokIN), "DIRECT")) {
                    klokIN++;
                    klokOUT++;
                }
            }


        if (klokOUT < outLimit) {
            if (yard.executeJob(huidigProbleem.getOutputJobSequence().get(klokOUT), "OUTPUT")) {
                klokOUT++;
            }
            // yard.printOutYard();
        }

        if (klokIN < inLimit) {
            if (yard.executeJob(huidigProbleem.getInputJobSequence().get(klokIN), "INPUT")) {
                klokIN++;
            }
            // yard.printOutYard();
        }

        klok = Math.min(klokIN, klokOUT);
        if (notify) {
            setChanged();
            notifyObservers();
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

    public int getLimit() {
        return limit;
    }

    public int getKlok() {
        return klok;
    }

    public int getKlokIN() {
        return klokIN;
    }

    public void setKlokIN(int klokIN) {
        this.klokIN = klokIN;
    }

    public int getKlokOUT() {
        return klokOUT;
    }

    public void setKlokOUT(int klokOUT) {
        this.klokOUT = klokOUT;
    }

    public void setKlok(int klok) {
        this.klok = klok;
    }

    public Yard getYard() {
        return yard;
    }

    public int getInLimit() {
        return inLimit;
    }

    public int getOutLimit() {
        return outLimit;
    }

	public void setHuidigProbleem(Problem huidigProbleem) {
		this.huidigProbleem = huidigProbleem;
	}
   
}
