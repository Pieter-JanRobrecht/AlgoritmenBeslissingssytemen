package model;

import java.util.List;

import kraan.Slot;

//Matrix kan niet overweg met geschrankte containers
public class Matrix {
	
	//Met als conventie X Y Z een rechtsdraaiend assenstelsel
	private Slot[][][] park;

	public Matrix() {
		super();
	}

	public Matrix(List<Slot> slots, int maxXValue, int maxYValue, int maxZValue) {
		super();
		park = new Slot[maxXValue/10][maxYValue/10][maxZValue];
	}

	public Slot[][][] getPark() {
		return park;
	}

	public void setPark(Slot[][][] park) {
		this.park = park;
	}
}
