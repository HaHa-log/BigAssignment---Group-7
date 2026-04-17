package Branch;

public abstract class Entity {
    private String name;

    public Entity(String name) {
        this.name = name;
    }

    public abstract int getId();

    public String getName() {
        return name;
    }
    //Thinking of setting conditions for setName()
}
