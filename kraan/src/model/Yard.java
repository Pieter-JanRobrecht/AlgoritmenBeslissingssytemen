package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import controller.CSVUtils;
import kraan.Gantry;
import kraan.Item;
import kraan.Job;
import kraan.Main;
import kraan.Problem;
import kraan.Slot;

public class Yard {
	private int width, length, height;
	public static int[] csvparam1 = new int[5];
	public int clock;
	public Gantry gantry;
	public int pickUpPlaceDuration;

	private Slot[][] yard;
	private HashMap<Integer, Slot> itemIDList = new HashMap<Integer, Slot>();
	private List<Slot> slotList;
	private Slot inputSlot, outputSlot;
	private boolean debug = false;
	private List<Gantry> gantries;
	private FileWriter writer;

	public Yard(Problem probleem) {
		clock = 0;
		pickUpPlaceDuration = probleem.getPickupPlaceDuration();

		System.out.println("Yard initiating..");
		initializeYard(probleem);
		gantries = probleem.getGantries();
		gantry = gantries.get(0);
		System.out.println("Yard initiation done!");

		try {
			File hulp = new File(Main.class.getClassLoader().getResource("KRAANOPDRACHT1_Groep_Groep3.csv").toURI());
			writer = new FileWriter(hulp);
			CSVUtils.writeLine(writer, Arrays.asList("gID", "T", "x", "y", "itemInCraneID"));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Slot[][] getYard() {
		return yard;
	}

	public void setYard(Slot[][] yard) {
		this.yard = yard;
	}

	public Slot getInputSlot() {
		return inputSlot;
	}

	public void setInputSlot(Slot inputSlot) {
		this.inputSlot = inputSlot;
	}

	public Slot getOutputSlot() {
		return outputSlot;
	}

	public void setOutputSlot(Slot outputSlot) {
		this.outputSlot = outputSlot;
	}

	public FileWriter getWriter() {
		return writer;
	}

	public void setWriter(FileWriter writer) {
		this.writer = writer;
	}

	public void initializeYard(Problem probleem) {
		this.height = probleem.getMaxLevels();
		this.width = probleem.getMaxY() / 10;
		this.length = probleem.getMaxX() / 10 - 1;
		this.yard = new Slot[width * length + (length - 1) * width][height]; // KEK
		this.slotList = probleem.getSlots();

		if (debug)
			System.out.println(
					"Setting parameters.. \n Height: " + height + "\n Width: " + width + "\n Length: " + length);

		for (Slot s : probleem.getSlots()) {
			// Slot in Yard plaatsen
			// yard[s.getId()%(length*width)][s.getId()/(length*width)] = s; -->
			// don't be drunk rhino ._.

			if (s.isStorage()) {
				int xCoords = s.getCenterX() / 10;
				int yCoords = s.getCenterY() / 10;

				// detect if staggered...
				// System.out.println("DEBUG - " + s.getXMin() + " | "+
				// Math.floor(s.getXMin() / 10) + " | "+ s.getXMin() / 10);
				if (Math.floor(s.getXMin() / 10) != s.getXMin() / 10) {
					// ([yCoords*length+xCoords]-yCoords) ZIE EXCEL
					// if(debug) System.out.println("DEBUG (STAGGERED) - [" +
					// (width * length + yCoords * (length - 1) + xCoords)
					// + "] || [" + s.getZ() + "]");
					yard[(width * length - 1) + (yCoords * (length - 1) + (xCoords + 1))][s.getZ()] = s;
					if (s.getItem() != null)
						itemIDList.put(s.getItem().getId(), s);
				} else { // not staggered
					// if(debug) System.out.println("DEBUG - [ yCoord: " +
					// yCoords + " xCoords: " + xCoords + " -> "
					// + (yCoords * length + xCoords) + "] || [" + s.getZ() +
					// "]");
					yard[yCoords * length + xCoords][s.getZ()] = s;
					if (s.getItem() != null)
						itemIDList.put(s.getItem().getId(), s);
				}
			} else if (s.isInput()) { // inputslot
				inputSlot = s;
			} else { // outputslot
				outputSlot = s;
			}
		}
	}

	public Slot getSlotWithID(int itemId) {
		try {
			return itemIDList.get(itemId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void printOutYard() {
		for (int i = 0; i < 2; i++) {
			System.out.println();
		}
		System.out.println("Visualizing yard!");
		for (int i = 0; i < height; i++) {
			System.out.println("============ layer " + i + " ============");
			for (int j = 0; j < width; j++) {
				for (int k = 0; k < length; k++) {
					Slot s = yard[j * length + k][i];
					if (s == null) {
						System.out.print(" - ");
					} else {
						if (s.getItem() != null) {
							System.out.print(s.getItem().getId());
						} else {
							System.out.print("[ ]");
						}
					}
					System.out.print(" \t\t ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}

	public boolean addItem(Item i) {
		boolean found = false;
		for (int j = 0; j < slotList.size(); j++) {
			Slot temp = slotList.get(j);
			if (temp.getItem() == null) {
				temp.setItem(i);
				itemIDList.put(i.getId(), temp);

				if (debug)
					System.out.println("DEBUG - We found a suiteable slot! Placing item " + i.toString() + " in "
							+ temp.toString());
				found = true;

				// Begin positie van gantry
				int x = gantry.getStartX();
				int y = gantry.getStartY();

				double tijdBewegenNaarIngang = (Math.abs(inputSlot.getCenterX()) + x) * gantry.getXSpeed()
						+ (Math.abs(inputSlot.getCenterY() + y) * gantry.getYSpeed());
				int pickUp = 2 * pickUpPlaceDuration;
				double tijdBewegenNaarPlaats = (Math.abs(inputSlot.getCenterX()) + temp.getCenterX())
						* gantry.getXSpeed()
						+ (Math.abs(inputSlot.getCenterY() + temp.getCenterY()) * gantry.getYSpeed());
				int place = 2 * pickUpPlaceDuration;

				// Plaats gantry updaten
				gantry.setStartX(temp.getCenterX());
				gantry.setStartY(temp.getCenterY());

				clock += tijdBewegenNaarIngang + pickUp + tijdBewegenNaarPlaats + place;

				// csv
				csvparam1[0] = gantries.get(0).getId();
				csvparam1[1] = temp.getCenterX();
				csvparam1[2] = temp.getCenterY();
				csvparam1[3] = temp.getItem().getId();
				csvparam1[4] = clock;

				try {
					CSVUtils.writeLine(writer, Arrays.asList("" + Yard.csvparam1[0], "" + Yard.csvparam1[4],
							"" + Yard.csvparam1[1], "" + Yard.csvparam1[2], "" + Yard.csvparam1[3]));
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}

				j = slotList.size() + 10;
			}
		}
		return found;
	}

	public boolean digItem(Item i) {
		boolean succes = false;

		if (debug)
			System.out.println("DEBUG - Item info: " + i.toString());
		Slot core = itemIDList.get(i.getId());
		if (core == null) {
			if (debug)
				System.out.println("ERRORDEBUG - " + i.toString());
		} else {
			if (maakVrij(core)) {
				// mogen nu vrij bewegen!
				succes = true;
				// itemIDList.remove(core.getItem().getId());

				// Begin positie van gantry
				int x = gantry.getStartX();
				int y = gantry.getStartY();

				double tijdBewegenNaarIngang = (Math.abs(inputSlot.getCenterX()) + x) * gantry.getXSpeed()
						+ (Math.abs(inputSlot.getCenterY() + y) * gantry.getYSpeed());
				int pickUp = 2 * pickUpPlaceDuration;
				double tijdBewegenNaarPlaats = (Math.abs(inputSlot.getCenterX()) + core.getCenterX())
						* gantry.getXSpeed()
						+ (Math.abs(inputSlot.getCenterY() + core.getCenterY()) * gantry.getYSpeed());
				int place = 2 * pickUpPlaceDuration;

				// Plaats gantry updaten
				gantry.setStartX(core.getCenterX());
				gantry.setStartY(core.getCenterY());

				clock += tijdBewegenNaarIngang + pickUp + tijdBewegenNaarPlaats + place;

				// csv
				csvparam1[0] = gantries.get(0).getId();
				csvparam1[1] = core.getCenterX();
				csvparam1[2] = core.getCenterY();
				csvparam1[3] = core.getItem().getId();
				csvparam1[4] = clock;

				try {
					CSVUtils.writeLine(writer, Arrays.asList("" + Yard.csvparam1[0], "" + Yard.csvparam1[4],
							"" + Yard.csvparam1[1], "" + Yard.csvparam1[2], "" + Yard.csvparam1[3]));
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}

				core.setItem(null); // --> zogezegd naar eindslot gemoved en
				// verwijdert uit yard
			}
		}
		return succes;
	}

	public boolean maakVrij(Slot s) {
		if (debug)
			System.out.println("DEBUG - Trying to empty slot.. " + s.toString());
		boolean vrij = false;
		if (s.getZ() < height) {
			int xCoords = s.getCenterX() / 10;
			int yCoords = s.getCenterY() / 10;

			if (Math.floor(s.getXMin() / 10) != s.getXMin() / 10) {
				// staggered - WERKT NOG NIET HEHEXD
				if (debug)
					System.out.println("DEBUG - STAGGERED");
				if (debug)
					System.out.print("DEBUG - yCoords: " + yCoords + " xCoords: " + xCoords + " -> "
							+ ((xCoords - 1) + (yCoords * length)) + " Z level: " + (s.getZ() + 1) + " || ");
				if (debug)
					System.out.println((yard[(xCoords - 1) + (yCoords * length)][s.getZ()].getItem() == null));

				if (yard[(xCoords - 1) + (yCoords * length)][s.getZ()].getItem() == null) {
					vrij = true;
				} else {
					vrij = false;
					if (s.getZ() + 1 == height || maakVrijBoven(yard[(xCoords - 1) + (yCoords * length)][s.getZ()])) {
						yard[(xCoords - 1) + (yCoords * length)][s.getZ()].setItem(null);
						return true;
					}
				}

				if (yard[(xCoords - 1) + (yCoords * length) + 1][s.getZ()] == null) {
					vrij = true;
				} else {
					vrij = false;
					if (s.getZ() + 1 == height
							|| maakVrijBoven(yard[(xCoords - 1) + (yCoords * length) + 1][s.getZ()])) {
						yard[(xCoords - 1) + (yCoords * length) + 1][s.getZ()].setItem(null);
						return true;
					}
				}

				if (vrij)
					return true;
			} else { // not staggered
				if (debug)
					System.out.print("DEBUG - yCoords: " + yCoords + " xCoords: " + xCoords + " -> "
							+ (yCoords * length + xCoords) + " Z level: " + (s.getZ() + 1) + " || ");
				if (debug)
					System.out.println((yard[yCoords * length + xCoords][s.getZ()].getItem() == null));
				if (yard[yCoords * length + xCoords][s.getZ()].getItem() == null) {
					return true;
				} else {
					vrij = false;
					if (s.getZ() + 1 == height || maakVrijBoven(yard[yCoords * length + xCoords][s.getZ() + 1])) {
						// yard[yCoords * length +
						// xCoords][s.getZ()].setItem(null);
						return true;
					}
				}
			}
		} else
			vrij = true;
		return vrij;
	}

	public boolean maakVrijBoven(Slot s) {
		if (debug)
			System.out.println("DEBUG - Trying to empty slot.. " + s.toString());
		boolean vrij = false;
		if (s.getZ() < height) {
			int xCoords = s.getCenterX() / 10;
			int yCoords = s.getCenterY() / 10;

			if (Math.floor(s.getXMin() / 10) != s.getXMin() / 10) {
				// staggered- WERKT NOG NIET HEHEXD
				if (debug)
					System.out.println("DEBUG - STAGGERED");
				if (debug)
					System.out.print("DEBUG - yCoords: " + yCoords + " xCoords: " + xCoords + " -> "
							+ ((xCoords - 1) + (yCoords * length)) + " Z level: " + (s.getZ() + 1) + " || ");
				if (debug)
					System.out.println((yard[(xCoords - 1) + (yCoords * length)][s.getZ()].getItem() == null));
				if (yard[(xCoords - 1) + (yCoords * length)][s.getZ()].getItem() == null) {
					vrij = true;
				} else {
					vrij = false;
					if (s.getZ() + 1 == height
							|| maakVrijBoven(yard[(xCoords - 1) + (yCoords * length)][s.getZ() + 1])) {
						if (moveItem(yard[(xCoords - 1) + (yCoords * length)][s.getZ()]))
							yard[(xCoords - 1) + (yCoords * length)][s.getZ()].setItem(null);
						return true;
					}
				}

				if (yard[(xCoords - 1) + (yCoords * length) + 1][s.getZ()].getItem() == null) {
					vrij = true;
				} else {
					vrij = false;
					if (s.getZ() + 1 == height
							|| maakVrij(yard[(xCoords - 1) + (yCoords * length) + 1][s.getZ() + 1])) {
						if (moveItem(yard[(xCoords - 1) + (yCoords * length) + 1][s.getZ()]))
							yard[(xCoords - 1) + (yCoords * length) + 1][s.getZ()].setItem(null);
						return true;
					}
				}

				if (vrij)
					return true;
			} else { // not staggered
				if (debug)
					System.out.print("DEBUG - yCoords: " + yCoords + " xCoords: " + xCoords + " -> "
							+ (yCoords * length + xCoords) + " Z level: " + (s.getZ() + 1) + " || ");
				if (debug)
					System.out.println((yard[yCoords * length + xCoords][s.getZ()].getItem() == null));
				if (yard[yCoords * length + xCoords][s.getZ()].getItem() == null) {
					return true;
				} else {
					vrij = false;
					if (s.getZ() + 1 == height || maakVrijBoven(yard[yCoords * length + xCoords][s.getZ() + 1])) {
						if (moveItem(yard[yCoords * length + xCoords][s.getZ()]))
							yard[yCoords * length + xCoords][s.getZ()].setItem(null);
						return true;
					}
				}
			}
		} else
			vrij = true;
		return vrij;
	}

	public boolean moveItem(Slot s) {
		if (debug)
			System.out.println("DEBUG - Moving item (" + s.getItem().getId() + ")");
		boolean succes = false;
		for (int j = 0; j < slotList.size(); j++) {
			Slot temp = slotList.get(j);
			if (temp.getItem() == null) {
				temp.setItem(s.getItem());
				succes = true;
				j = slotList.size() + 10;
				itemIDList.put(temp.getItem().getId(), temp);
				if (debug)
					System.out.println("DEBUG - We found a suiteable slot! " + temp.toString());

				// Begin positie van gantry
				int x = gantry.getStartX();
				int y = gantry.getStartY();

				double tijdBewegenNaarIngang = (Math.abs(inputSlot.getCenterX()) + x) * gantry.getXSpeed()
						+ (Math.abs(inputSlot.getCenterY() + y) * gantry.getYSpeed());
				int pickUp = 2 * pickUpPlaceDuration;
				double tijdBewegenNaarPlaats = (Math.abs(inputSlot.getCenterX()) + temp.getCenterX())
						* gantry.getXSpeed()
						+ (Math.abs(inputSlot.getCenterY() + temp.getCenterY()) * gantry.getYSpeed());
				int place = 2 * pickUpPlaceDuration;

				// Plaats gantry updaten
				gantry.setStartX(temp.getCenterX());
				gantry.setStartY(temp.getCenterY());

				clock += tijdBewegenNaarIngang + pickUp + tijdBewegenNaarPlaats + place;

				// csv
				csvparam1[0] = gantries.get(0).getId();
				csvparam1[1] = temp.getCenterX();
				csvparam1[2] = temp.getCenterY();
				csvparam1[3] = temp.getItem().getId();
				csvparam1[4] = clock;

				try {
					CSVUtils.writeLine(writer, Arrays.asList("" + Yard.csvparam1[0], "" + Yard.csvparam1[4],
							"" + Yard.csvparam1[1], "" + Yard.csvparam1[2], "" + Yard.csvparam1[3]));
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return succes;
	}

	public boolean executeJob(Job j, String mode) {
		boolean succes = false;
		if (mode == "INPUT") {
			System.out.println("TASKHANDLER - adding " + j.getItem().toString());
			succes = addItem(j.getItem());

			if (!succes) {
				System.out.println("We failed to handle item " + j.getItem().toString() + " (IN) for now, backlogging");
			} else
				System.out.println("We succeeded in handling (IN) item " + j.getItem().toString());
		} else if (mode == "OUTPUT") {
			System.out.println("TASKHANDLER - removing " + j.getItem().toString());
			succes = digItem(j.getItem());

			if (!succes) {
				System.out
						.println("We failed to handle item " + j.getItem().toString() + " (OUT) for now, backlogging");
			} else
				System.out.println("We succeeded in handling (OUT) item " + j.getItem().toString());
		}

		return succes;
	}

	public void printHash() {
		// System.out.println(itemIDList);
		System.out.println("Printing Hash Map!");
		for (Integer id : itemIDList.keySet()) {
			int slot = itemIDList.get(id).getId();
			int container = itemIDList.get(id).getItem().getId();
			System.out.println("Hash id:" + id + "\t Slot id:" + slot + "\t Container id:" + container);
		}
	}
}