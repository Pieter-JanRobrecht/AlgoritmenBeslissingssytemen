package model;

import java.util.HashMap;
import java.util.List;

import kraan.Problem;
import kraan.Slot;

public class Yard {
	private int width, length, height;
	private Slot[][] yard;
	private HashMap<Integer, Slot> itemIDList = new HashMap<Integer, Slot>();

	public Yard(Problem probleem) {
		System.out.println("Yard initiating..");
		initializeYard(probleem);
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

	public void initializeYard(Problem probleem) {
		this.height = probleem.getMaxLevels();
		this.width = probleem.getMaxY() / 10;
		this.length = probleem.getMaxX() / 10-1;
		this.yard = new Slot[length + (length - 1) * width][height];
		
		System.out.println("Setting parameters.. \n Height: " + height + "\n Width: " + width + "\n Length: " + length);

		for (Slot s : probleem.getSlots()) {
			// Slot in Yard plaatsen
			// yard[s.getId()%(length*width)][s.getId()/(length*width)] = s; -->
			// don't be drunk rhino ._.
			if(s.isStorage()) {
				int xCoords = s.getCenterX() / 10;
				int yCoords = s.getCenterY() / 10;

				// detect if staggered...
				//System.out.println("DEBUG - " + s.getXMin() + " | "+ Math.floor(s.getXMin() / 10) + " | "+ s.getXMin() / 10);
				if (Math.floor(s.getXMin() / 10) != s.getXMin() / 10) {
					// ([yCoords*length+xCoords]-yCoords) ZIE EXCEL
					System.out.println("DEBUG (STAGGERED) - [" + (width * length + yCoords * (length - 1) + xCoords)
							+ "] || [" + s.getZ() + "]");
					yard[width * length + yCoords * (length - 1) + xCoords][s.getZ()] = s;
					if (s.getItem() != null)
						itemIDList.put(s.getItem().getId(), s);
				} else { // not staggered
					System.out.println("DEBUG - [ yCoord: " + yCoords + " xCoords: " + xCoords + " -> " + (yCoords * length + xCoords) + "] || [" + s.getZ() + "]");
					yard[yCoords * length + xCoords][s.getZ()] = s;
					if (s.getItem() != null)
						itemIDList.put(s.getItem().getId(), s);
				}
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
		for(int i = 0; i < 10; i++) {
			System.out.println();
		}
		System.out.println("Visualizing yard!");
		for(int i = 0; i < height; i++){
			System.out.println("============ layer " + i +" ============");
			for(int j = 0; j < width; j++) {
				for(int k = 0; k < length; k++) {
					Slot s = yard[j*length+k][i];
					if(s == null) {
						System.out.print(" - ");
					} else {
						if(s.getItem() != null) {
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

	public void printHash() {
//		System.out.println(itemIDList);
		System.out.println("Printing Hash Map!");
		for (Integer id: itemIDList.keySet()){
			
            int slot = itemIDList.get(id).getId();
            int container = itemIDList.get(id).getItem().getId();
            System.out.println("Hash id:" + id + "\t Slot id:" + slot + "\t Container id:"+container);  


} 
	}
}
