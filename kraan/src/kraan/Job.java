package kraan;


/**
 * Created by Wim on 12/05/2015.
 */
public class Job {

    private int id;

    private Task pickup = new Task(TaskType.PICKUP);
    private Task place = new Task(TaskType.DELIVERY);

    private Item item;


    public Job(int id, Item c, Slot from, Slot to) {
        this.id = id;
        this.item = c;
        this.pickup.slot = from;
        this.place.slot = to;
    }

    public int getId() {
        return id;
    }

    public Task getPickup() {
        return pickup;
    }

    public Task getPlace() {
        return place;
    }

    public Item getItem() {
        return item;
    }

    public class Task {
        private Slot slot;
        private Job job;
        private TaskType type;

        public Task(TaskType taskType) {
            this.type = taskType;
            this.job = Job.this;
        }

        public Slot getSlot() {
            return slot;
        }

        public Job getJob() {
            return job;
        }

        public TaskType getType() {
            return type;
        }
    }

    public static enum TaskType {
        PICKUP,
        DELIVERY
    }

}
