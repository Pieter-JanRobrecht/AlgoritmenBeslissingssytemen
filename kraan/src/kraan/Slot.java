package kraan;

/**
 * Created by Wim on 27/04/2015.
 */
public class Slot {

    private int id;
    private int centerX, centerY, xMin, xMax, yMin, yMax,z;
    private Item item;
    private SlotType type;

    public Slot(int id, int centerX, int centerY, int xMin, int xMax, int yMin, int yMax, int z, SlotType type, Item item) {
        this.id = id;
        this.centerX = centerX;
        this.centerY = centerY;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.z = z;
        this.item = item;
        this.type = type;
    }

	public int getId() {
        return id;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getZ() {
        return z;
    }

    public int getXMin() {
        return xMin;
    }

    public int getXMax() {
        return xMax;
    }

    public int getYMin() {
        return yMin;
    }

    public int getYMax() {
        return yMax;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public SlotType getType() {
        return type;
    }

    public static enum SlotType {
        INPUT,
        OUTPUT,
        STORAGE
    }
    
    public boolean isStorage() {
    	if(this.type.toString().contains("STORAGE")) return true;
    	else return false;
    }
    
    public boolean isInput() {
    	if(this.type.toString().contains("INPUT")) return true;
    	else return false;
    }

    public boolean isOutput() {
    	if(this.type.toString().contains("OUTPUT")) return true;
    	else return false;
    }
    @Override
	public String toString() {
		return "Slot [id=" + id + ", centerX=" + centerX + ", centerY=" + centerY + ", xMin=" + xMin + ", xMax=" + xMax
				+ ", yMin=" + yMin + ", yMax=" + yMax + ", z=" + z + ", item=" + item + ", type=" + type + "]";
	}
}
