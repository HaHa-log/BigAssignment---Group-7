package Branch;

public abstract class Entity {
    private int id;
    private String name;

    public Entity(String name) {
        this.name = name;
    }

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }

    public String getName() {
        return name;
    }
    //Thinking of setting conditions for setName()
}
