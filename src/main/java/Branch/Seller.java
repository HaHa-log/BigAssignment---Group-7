package Branch;

public interface Seller {
    default boolean createAuction(Item item, double startingPrice) {
        System.out.println("Selling stuff...");
        return true;
        //Tạm thời để như này
    }
}