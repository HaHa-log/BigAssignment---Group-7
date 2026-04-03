public abstract class Item extends Entity {
    private String startPrice;

    public Item(String id, String name, String startPrice) {
        super(id, name);
        this.startPrice = startPrice;
    }
}
