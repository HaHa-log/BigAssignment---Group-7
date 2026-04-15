package Branch;

public abstract class Entity {
    private int id;
    private String name;

    public Entity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    //Thinking of setting conditions for setName()
}
