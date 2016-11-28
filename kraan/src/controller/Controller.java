package controller;

import javafx.stage.FileChooser;
import kraan.Job;
import kraan.Main;
import kraan.Problem;
import model.Yard;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Observable;
import java.io.FileWriter;
import java.util.Arrays;

/**
 * Created by Pieter-Jan on 20/10/2016.
 */
public class Controller extends Observable {

	private Problem huidigProbleem;

	private int limit, inLimit, outLimit, klok, klokIN, klokOUT;

	private Yard yard;

	public Controller() throws IOException {

		if (huidigProbleem == null) {
			setFileSmall();
		}
		// String csvFile = "./data/csv/test.csv";
		// FileWriter writer = new FileWriter(csvFile);
		// CSVUtils.writeLine(writer,
		// Arrays.asList("gID","T","x","y","itemInCraneID"));
		// writer.flush();
		reset();
	}

	public void reset() {
		yard = new Yard(huidigProbleem);
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

		if (klokIN < inLimit) {
			if (yard.executeJob(huidigProbleem.getInputJobSequence().get(klokIN), "INPUT")) {
				klokIN++;
			}
			// yard.printOutYard();
		}

		if (klokOUT < outLimit) {
			if (yard.executeJob(huidigProbleem.getOutputJobSequence().get(klokOUT), "OUTPUT")) {
				klokOUT++;
			}
			// yard.printOutYard();
		}

		klok = Math.min(klokIN, klokOUT);
		if (notify) {
			setChanged();
			notifyObservers();
		}

		if (klokIN >= inLimit && klokOUT >= outLimit) {
			yard.getWriter().close();
		}
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
