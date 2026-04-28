package Branch;

public abstract class Entity {
    private int id;
    private String name;

    public Entity(String name) {
        this(0, name);
    }

    public Entity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }

    public String getName() {
        return name;
    }
}
