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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    private boolean debugL = false;
    private boolean debugM = false;
    private List<Gantry> gantries;
    private FileWriter writer;

    private boolean staggered;

    private Stage stage;

    public Yard(Problem probleem, Stage stage) {
        this.stage = stage;
        System.out.println("Yard initiating..");

        for (int i = 0; i < probleem.getSlots().size(); i++) {
            Slot s = probleem.getSlots().get(i);
            if (s.getZ() != 0 && s.getCenterX() % 10 == 0) {
                // staggered is true
                System.out.println("Staggered detected!");
                staggered = true;
                i = probleem.getSlots().size();
                // jump out for :D
            } else if (s.getZ() == 2)
                i = probleem.getSlots().size();
        }

        initializeYard(probleem);
        gantries = probleem.getGantries();

        initWriter(probleem);

        System.out.println("Yard initiation done!");
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
        this.yard = new Slot[width * length + (length - 1) * width + 1][height]; // KEK
        this.slotList = probleem.getSlots();

        if (debug || debugL)
            System.out.println(
                    "Setting parameters.. \n Height: " + height + "\n Width: " + width + "\n Length: " + length);

        for (Slot s : probleem.getSlots()) {
            // Slot in Yard plaatsen
            if (s.isStorage()) {
                int xCoords = s.getCenterX() / 10;
                int yCoords = s.getCenterY() / 10;

                if (staggered) {
                    if (s.getZ() % 2 == 0) {
                        // even (0 ook even) ==> NOT STAGGERED
                        if (debugL)
                            System.out.println("NS | " + (yCoords * length + xCoords) + " | " + s.toString());
                        yard[yCoords * length + xCoords][s.getZ()] = s;

                    } else {
                        // oneven ==> STAGGERED
                        if (debugL)
                            System.out.println("S | " + (width * length + (yCoords * length + xCoords - yCoords))
                                    + " | " + s.toString());
                        yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ()] = s;

                    }

                    if (s.getItem() != null)
                        itemIDList.put(s.getItem().getId(), s);
                } else { // not staggered
                    if (debugL)
                        System.out.println("NS | " + (yCoords * length + xCoords) + " | " + s.toString());

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

    // niet eens nodig???
    private void fillSideContainers(Problem probleem) {
        for (Slot s : probleem.getSlots()) {
            if (s.getZ() % 2 == 0) {
                // even (0 ook even) ==> NOT STAGGERED
                int verlies = s.getZ() / 2;
                if (s.getXMax() / 10 < verlies || s.getXMin() / 10 > (length - verlies)) {
                    s.setItem(new Item(-1));
                    if (debug)
                        System.out.println("DEBUG - disabling slot (z=" + s.getZ() + "): " + s.toString());
                }
            } else {
                // oneven ==> STAGGERED
                int verlies = s.getZ() / 2;
                if (s.getXMax() / 10 < verlies || s.getXMin() / 10 > (length - verlies)) {
                    s.setItem(new Item(-1));
                    if (debug)
                        System.out.println("DEBUG - disabling slot (z=" + s.getZ() + "): " + s.toString());
                }
            }
        }
    }

    private void initWriter(Problem probleem) {
        try {
            clock = 0;
            pickUpPlaceDuration = probleem.getPickupPlaceDuration();
            gantry = gantries.get(0);


            FileChooser fileChooser = new FileChooser();
            try {
                fileChooser.setInitialDirectory(
                        //                new File(".")
                        new File(Main.class.getClassLoader().getResource(".").toURI())
                );
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            fileChooser.setTitle("Open CSV File");
            File hulp = fileChooser.showOpenDialog(stage);

//			File hulp = new File(Main.class.getClassLoader().getResource("KRAANOPDRACHT2_Groep3.csv").toURI());
            writer = new FileWriter(hulp);
            CSVUtils.writeLine(writer, Arrays.asList("gID", "T", "x", "y", "itemInCraneID"));
            writer.flush();
            for (int i = 0; i < gantries.size(); i++) {
                CSVUtils.writeLine(writer, Arrays.asList(gantries.get(i).getId() + "", clock + "",
                        gantries.get(i).getStartX() + "", gantries.get(i).getStartY() + "", "null"));
                writer.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
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

        // Beweeg naar ingang
        writeMove(inputSlot, gantry, null);
        writePickUp(inputSlot, gantry, i.getId());

        for (int j = 0; j < slotList.size(); j++) {
            Slot temp = slotList.get(j);
            if (temp.getItem() == null) {
                temp.setItem(i);
                itemIDList.put(i.getId(), temp);

                if (debug)
                    System.out.println("DEBUG - We found a suiteable slot! Placing item " + i.toString() + " in "
                            + temp.toString());
                found = true;

                writeMove(temp, gantry, temp.getItem().getId());
                writePlacement(temp, gantry);

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
            if (debug)
                System.out.println("DEBUG - Slot info where item is stored: " + core.toString());
            if (maakVrij(core)) {
                // mogen nu vrij bewegen!
                succes = true;
                // itemIDList.remove(core.getItem().getId());

                writeMove(core, gantry, null);
                writePickUp(core, gantry, i.getId());
                writeMove(outputSlot, gantry, i.getId());
                writePlacement(outputSlot, gantry);

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

            if (staggered) {
                // staggered
                if (s.getZ() % 2 == 0) {
                    if (debug || debugL)
                        System.out.println("\n\nDEBUG - maakVrij op een EVEN (NON STAGGERED, STAGGERED)");
                    // even (0 ook even) ==> NOT STAGGERED
                    if (s.getZ() + 1 < height) {
                        vrij = false;
                        if (xCoords + 1 == length) {
                            // enkel links kijken
                            if (debugL) {
                                System.out.println("DEBUG - " + s.getZ() + " - values y:" + yCoords + " & x:" + xCoords
                                        + ", now trying to empty: " + s.toString());
                                if (yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ() + 1] != null)
                                    System.out.println("We'll need to empty: "
                                            + yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ()
                                            + 1].toString());
                                else
                                    System.out.println("It seems we've found a null slot above us. (1L)");
                            }
                            if (maakVrijBoven(
                                    yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ() + 1],
                                    xCoords, yCoords, s.getZ())) {
                                vrij = true;
                            }
                        } else if (xCoords == 0) {
                            // enkel rechts kijken
                            if (debugL) {
                                System.out.println("DEBUG - " + s.getZ() + " - values y:" + yCoords + " & x:" + xCoords
                                        + ", now trying to empty: " + s.toString());
                                if (yard[width * length + (yCoords * length + 1 - yCoords)][s.getZ() + 1] != null)
                                    System.out.println("We'll need to empty: "
                                            + yard[width * length + (yCoords * length + 1 - yCoords)][s.getZ() + 1]
                                            .toString());
                                else
                                    System.out.println("It seems we've found a null slot above us. (1R)");
                            }
                            if (maakVrijBoven(yard[width * length + (yCoords * length + 1 - yCoords)][s.getZ() + 1],
                                    xCoords, yCoords, s.getZ())) {
                                vrij = true;
                            }
                        } else {
                            // 2 kanten
                            if (debugL) {
                                System.out.println("DEBUG - " + s.getZ() + " - values y:" + yCoords + " & x:" + xCoords
                                        + ", now trying to empty: " + s.toString());
                                if (yard[width * length + (yCoords * length + 1 - yCoords)][s.getZ() + 1] != null)
                                    System.out.println("We'll need to empty: "
                                            + yard[width * length + (yCoords * length + 1 - yCoords)][s.getZ() + 1]
                                            .toString());
                                else
                                    System.out.println("It seems we've found a null slot above us. (2R)");
                                if (yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ() + 1] != null)
                                    System.out.println("We'll need to empty: "
                                            + yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ()
                                            + 1].toString());
                                else
                                    System.out.println("It seems we've found a null slot above us. (2L)");
                            }

                            if (maakVrijBoven(
                                    yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ() + 1],
                                    xCoords, yCoords, s.getZ())
                                    && maakVrijBoven(
                                    yard[width * length + (yCoords * length + (xCoords + 1) - yCoords)][s.getZ()
                                            + 1],
                                    xCoords, yCoords, s.getZ())) {
                                vrij = true;
                            }
                        }

                    } else {
                        System.out.println("DEBUG !1! - Reached topside");
                        return true;
                    }
                } else {
                    if (debug || debugL)
                        System.out.println("\n\nDEBUG - maakVrij op een ONEVEN (STAGGERED, STAGGERED)");
                    // oneven ==> STAGGERED
                    if (s.getZ() + 1 < height) {
                        vrij = false;
                        if (debugL) {
                            System.out.println("DEBUG - " + s.getZ() + " - values y:" + yCoords + " & x:" + xCoords
                                    + ", now trying to empty: " + s.toString());
                            if (yard[yCoords * length + xCoords][s.getZ() + 1] != null)
                                System.out.println("We'll need to empty: "
                                        + yard[yCoords * length + xCoords][s.getZ() + 1].toString());
                            else
                                System.out.println("It seems we've found a null slot above us. (2R)");
                            if (yard[yCoords * length + xCoords - 1][s.getZ() + 1] != null)
                                System.out.println("We'll need to empty: "
                                        + yard[yCoords * length + xCoords - 1][s.getZ() + 1].toString());
                            else
                                System.out.println("It seems we've found a null slot above us. (2L)");
                        }
                        // altijd 2 kanten ;-;
                        if (maakVrijBoven(yard[yCoords * length + xCoords][s.getZ() + 1], xCoords, yCoords, s.getZ())
                                && maakVrijBoven(yard[yCoords * length + xCoords - 1][s.getZ() + 1], xCoords, yCoords,
                                s.getZ())) {
                            vrij = true;
                        }
                    } else {
                        System.out.println("DEBUG !2! - Reached topside");
                        return true;
                    }
                }
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

                    if (s.getZ() + 1 == height || maakVrijBoven(yard[yCoords * length + xCoords][s.getZ() + 1], xCoords,
                            yCoords, s.getZ())) {
                        return true;
                    }
                }
            }
        } else
            vrij = true;
        return vrij;
    }

    public boolean maakVrijBoven(Slot s, int coreX, int coreY, int coreZ) {
        if (s == null || s.getItem() == null) // HIHI
            return true;
        if (debug)
            System.out.println("DEBUG - Trying to empty slot.. " + s.toString());
        boolean vrij = false;
        if (s.getZ() < height) {
            int xCoords = s.getCenterX() / 10;
            int yCoords = s.getCenterY() / 10;

            if (staggered) {
                // staggered
                if (debug)
                    System.out.println("DEBUG - STAGGERED");

                if (s.getItem() == null || s.getItem().getId() == -1) {
                    return true;
                } else {
                    if (debug)
                        System.out.println("DEBUG - Trying to empty slot " + s.toString());

                    if (s.getZ() % 2 == 0) {
                        if (debug || debugL)
                            System.out.println("\n\nDEBUG - maakVrijBoven op een EVEN (NON STAGGERED, STAGGERED)");
                        // even (0 ook even) ==> NOT STAGGERED
                        if (s.getZ() + 1 < height) {
                            vrij = false;
                            if (xCoords + 1 == length) {
                                // enkel links kijken
                                if (debugL) {
                                    System.out.println("DEBUG - " + s.getZ() + " - values y:" + yCoords + " & x:"
                                            + xCoords + ", now trying to empty: " + s.toString());
                                    if (yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ()
                                            + 1] != null)
                                        System.out.println("We'll need to empty: "
                                                + yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ()
                                                + 1].toString());
                                    else
                                        System.out.println("It seems we've found a null slot above us. (1L)");
                                }
                                if (maakVrijBoven(
                                        yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ() + 1],
                                        coreX, coreY, coreZ)) {
                                    if (moveItem(s, coreX, coreY, coreZ)) {
                                        s.setItem(null);
                                        vrij = true;
                                    }
                                }
                            } else if (xCoords == 0) {
                                // enkel rechts kijken
                                if (debugL) {
                                    System.out.println("DEBUG - " + s.getZ() + " - values y:" + yCoords + " & x:"
                                            + xCoords + ", now trying to empty: " + s.toString());
                                    if (yard[width * length + (yCoords * length + 1 - yCoords)][s.getZ() + 1] != null)
                                        System.out.println("We'll need to empty: "
                                                + yard[width * length + (yCoords * length + 1 - yCoords)][s.getZ() + 1]
                                                .toString());
                                    else
                                        System.out.println("It seems we've found a null slot above us. (1R)");
                                }
                                if (maakVrijBoven(yard[width * length + (yCoords * length + 1 - yCoords)][s.getZ() + 1],
                                        coreX, coreY, coreZ)) {
                                    if (moveItem(s, coreX, coreY, coreZ)) {
                                        s.setItem(null);
                                        vrij = true;
                                    }
                                }
                            } else {
                                // 2 kanten
                                if (debugL) {
                                    System.out.println("DEBUG - " + s.getZ() + " - values y:" + yCoords + " & x:"
                                            + xCoords + ", now trying to empty: " + s.toString());
                                    if (yard[width * length + (yCoords * length + xCoords - yCoords) + 1][s.getZ()
                                            + 1] != null)
                                        System.out.println("We'll need to empty: " + yard[width * length
                                                + (yCoords * length + xCoords - yCoords) + 1][s.getZ() + 1].toString());
                                    else
                                        System.out.println("It seems we've found a null slot above us. (2R)");
                                    if (yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ()
                                            + 1] != null)
                                        System.out.println("We'll need to empty: "
                                                + yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ()
                                                + 1].toString());
                                    else
                                        System.out.println("It seems we've found a null slot above us. (2L)");
                                }

                                if (maakVrijBoven(
                                        yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ() + 1],
                                        coreX, coreY, coreZ)
                                        && maakVrijBoven(yard[width * length
                                                + (yCoords * length + (xCoords + 1) - yCoords)][s.getZ() + 1], coreX,
                                        coreY, coreZ)) {
                                    if (moveItem(s, coreX, coreY, coreZ)) {
                                        s.setItem(null);
                                        vrij = true;
                                    }
                                }

                            }

                        } else {
                            System.out.println("DEBUG !3! - Reached topside");
                            if (moveItem(s, coreX, coreY, coreZ)) {
                                s.setItem(null);
                            }
                            return true;
                        }
                    } else {
                        if (debug || debugL)
                            System.out.println("\n\nDEBUG - maakVrijBoven op een ONEVEN (STAGGERED, STAGGERED)");
                        // oneven ==> STAGGERED
                        if (s.getZ() + 1 < height) {
                            vrij = false;

                            if (debugL) {
                                System.out.println("DEBUG - " + s.getZ() + " - values y:" + yCoords + " & x:" + xCoords
                                        + ", now trying to empty: " + s.toString());
                                if (yard[yCoords * length + xCoords][s.getZ() + 1] != null)
                                    System.out.println("We'll need to empty: "
                                            + yard[yCoords * length + xCoords][s.getZ() + 1].toString());
                                else
                                    System.out.println("It seems we've found a null slot above us. (2R)");
                                if (yard[yCoords * length + xCoords - 1][s.getZ() + 1] != null)
                                    System.out.println("We'll need to empty: "
                                            + yard[yCoords * length + xCoords - 1][s.getZ() + 1].toString());
                                else
                                    System.out.println("It seems we've found a null slot above us. (2L)");
                            }

                            // altijd 2 kanten ;-;
                            if (maakVrijBoven(yard[yCoords * length + xCoords][s.getZ() + 1], coreX, coreY, coreZ)
                                    && maakVrijBoven(yard[yCoords * length + xCoords - 1][s.getZ() + 1], coreX, coreY,
                                    coreZ)) {
                                if (moveItem(s, coreX, coreY, coreZ)) {
                                    s.setItem(null);
                                    vrij = true;
                                }
                            }

                        } else {
                            System.out.println("DEBUG !4! - Reached topside");
                            if (moveItem(s, coreX, coreY, coreZ)) {
                                s.setItem(null);
                            }
                            return true;
                        }
                    }

                }
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
                    if (s.getZ() + 1 == height
                            || maakVrijBoven(yard[yCoords * length + xCoords][s.getZ() + 1], coreX, coreY, coreZ)) {
                        if (moveItem(s, coreX, coreY, coreZ))
                            s.setItem(null);
                        return true;
                    }
                }
            }
        } else
            vrij = true;
        return vrij;
    }

    public boolean isOnderVrij(Slot s) {
        int xCoords = s.getCenterX() / 10;
        int yCoords = s.getCenterY() / 10;

        if (s.getZ() == 0)
            return false;

        if (staggered) {
            if (s.getZ() % 2 == 0) {
                System.out.println("DEBUG !! (%2==0) - x:" + xCoords + " y: " + yCoords);
                // even (0 ook even) ==> NOT STAGGERED, HIERONDER WEL
                if (yard[width * length + (yCoords * length + (xCoords + 1) - yCoords)][s.getZ() - 1].getItem() == null) {
                    System.out.println("1e IF: " + width * length + (yCoords * length + (xCoords + 1) - yCoords));
                    return true;
                }

                if (yard[width * length + (yCoords * length + xCoords - yCoords)][s.getZ() - 1].getItem() == null) {
                    System.out.println("2e IF: " + width * length + (yCoords * length + xCoords - yCoords));
                    return true;
                }
            } else {
                System.out.println("DEBUG !! (%2!=0) - x:" + xCoords + " y: " + yCoords);
                // oneven ==> STAGGERED, HIERONDER NIET
                if (yard[yCoords * length + xCoords][s.getZ() - 1].getItem() == null) {
                    System.out.println("1e IF: " + yCoords * length + xCoords);
                    return true;
                }

                if (yard[yCoords * length + xCoords - 1][s.getZ() - 1].getItem() == null) {
                    System.out.println("1e IF: " + (yCoords * length + xCoords - 1));
                    return true;
                }
            }

        } else {
            if (yard[yCoords * length + xCoords][s.getZ() - 1].getItem() == null) {
                return true;
            }
        }
        return false;
    }

    public boolean moveItem(Slot s, int coreX, int coreY, int coreZ) {
        if (debug || debugL || debugM)
            System.out.println("DEBUG - Moving from slot (" + s.getId() + "): item (" + s.getItem().getId() + ")");
        boolean succes = false;

        writeMove(s, gantry, null);
        writePickUp(s, gantry, s.getItem().getId());

        if (staggered) {
            for (int j = 0; j < slotList.size(); j++) {
                Slot temp = slotList.get(j);
                if (debugM)

                    System.out.println("DEBUG !! - isEmpty?" + (temp.getItem() == null) + ", tempSlot getCenterX: " + temp.getCenterX() / 10 + ", height difference: " + (s.getZ() - coreZ)
                            + ", coreX: " + coreX + " | ?"
                            + ((temp.getCenterX() / 10 + (s.getZ() - coreZ)) < coreX)
                            + " ?" + ((temp.getCenterX() / 10 - (s.getZ() - coreZ)) > coreX));

                if (temp.getItem() == null && (temp.getZ() <= coreZ || (temp.getCenterX() / 10 + (s.getZ() - coreZ)) < coreX
                        || (temp.getCenterX() / 10 - (s.getZ() - coreZ)) > coreX)) {
//if (temp.getItem() == null && (temp.getCenterX()+1 < s.getCenterX() || temp.getCenterX()-1 > s.getCenterX() )) {
                    if (!isOnderVrij(temp)) {
                        if (debugM)
                            System.out.println("DEBUG - Is not flying, found a suiteable slot (staggered)! " + temp.toString());
                        temp.setItem(s.getItem());
                        succes = true;
                        j = slotList.size() + 10;
                        itemIDList.put(temp.getItem().getId(), temp);
                        if (debug || debugL)
                            System.out.println("DEBUG - We found a suiteable slot (staggered)! " + temp.toString());

                        writeMove(temp, gantry, s.getItem().getId());
                        writePlacement(temp, gantry);
                    } else if (debugM) {
                        System.out.println("###################################################################");
                    }
                }
            }
        } else {
            for (int j = 0; j < slotList.size(); j++) {
                Slot temp = slotList.get(j);
                if (temp.getItem() == null && (temp.getCenterX() != s.getCenterX())) {
                    temp.setItem(s.getItem());
                    succes = true;
                    j = slotList.size() + 10;
                    itemIDList.put(temp.getItem().getId(), temp);
                    if (debug || debugL)
                        System.out.println("DEBUG - We found a suiteable slot (non staggered)! " + temp.toString());

                    writeMove(temp, gantry, s.getItem().getId());
                    writePlacement(temp, gantry);
                }
            }
        }


        return succes;
    }

    public boolean executeJob(Job j, String mode) {
        boolean succes = false;
        if (mode.equals("INPUT")) {
            System.out.println("TASKHANDLER - adding " + j.getItem().toString());
            succes = addItem(j.getItem());

            if (!succes) {
                System.out.println("We failed to handle item " + j.getItem().toString() + " (IN) for now");
            } else
                System.out.println("We succeeded in handling (IN) item " + j.getItem().toString());
        } else if (mode.equals("OUTPUT")) {
            System.out.println("TASKHANDLER - removing " + j.getItem().toString());
            succes = digItem(j.getItem());

            if (!succes) {
                System.out.println("We failed to handle item " + j.getItem().toString() + " (OUT) for now");
            } else
                System.out.println("We succeeded in handling (OUT) item " + j.getItem().toString());
        } else if (mode.equals("DIRECT")) {

            System.out.println("Moving item " + j.getItem().getId() + " from input to output :)");
            writeMove(inputSlot, gantry, null);
            writePickUp(inputSlot, gantry, j.getItem().getId());
            writeMove(outputSlot, gantry, j.getItem().getId());
            writePlacement(outputSlot, gantry);
            //Hier moet kraan sysoke doen van:
            // Move to INPUT
            // pickup
            // Move to OUTPUT
            // drop
            succes = true;
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

    private void writePlacement(Slot temp, Gantry gantry) {
        clock += pickUpPlaceDuration;

        // csv
        csvparam1[0] = gantry.getId();
        csvparam1[1] = temp.getCenterX();
        csvparam1[2] = temp.getCenterY();
        csvparam1[3] = -1;
        csvparam1[4] = clock;

        try {
            CSVUtils.writeLine(writer, Arrays.asList("" + Yard.csvparam1[0], "" + Yard.csvparam1[4],
                    "" + Yard.csvparam1[1], "" + Yard.csvparam1[2], "" + "null"));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writePickUp(Slot temp, Gantry gantry, Integer itemId) {
        clock += pickUpPlaceDuration;

        // csv
        csvparam1[0] = gantry.getId();
        csvparam1[1] = temp.getCenterX();
        csvparam1[2] = temp.getCenterY();
        csvparam1[3] = itemId;
        csvparam1[4] = clock;

        try {
            CSVUtils.writeLine(writer, Arrays.asList("" + Yard.csvparam1[0], "" + Yard.csvparam1[4],
                    "" + Yard.csvparam1[1], "" + Yard.csvparam1[2], "" + Yard.csvparam1[3]));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeMove(Slot temp, Gantry gantry, Integer itemId) {
        double bewegingstijd = Math.max(Math.abs(temp.getCenterX() - gantry.getStartX()) / (gantry.getXSpeed()),
                Math.abs(temp.getCenterY() - gantry.getStartY()) / (gantry.getYSpeed()));

        clock += bewegingstijd;
        gantry.setStartX(temp.getCenterX());
        gantry.setStartY(temp.getCenterY());

        // csv
        csvparam1[0] = gantry.getId();
        csvparam1[1] = temp.getCenterX();
        csvparam1[2] = temp.getCenterY();
        if (itemId != null) {
            csvparam1[3] = itemId;
        }
        csvparam1[4] = clock;

        try {
            if (itemId != null) {
                CSVUtils.writeLine(writer, Arrays.asList("" + Yard.csvparam1[0], "" + Yard.csvparam1[4],
                        "" + Yard.csvparam1[1], "" + Yard.csvparam1[2], "" + Yard.csvparam1[3]));
                writer.flush();
            } else {
                CSVUtils.writeLine(writer, Arrays.asList("" + Yard.csvparam1[0], "" + Yard.csvparam1[4],
                        "" + Yard.csvparam1[1], "" + Yard.csvparam1[2], "" + "null"));
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}