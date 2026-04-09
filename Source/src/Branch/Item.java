public abstract class Item extends Entity {
    private String startPrice;

    public Item(int id, String name, String startPrice) {
        super(id, name);
        this.startPrice = startPrice;
    }
}
