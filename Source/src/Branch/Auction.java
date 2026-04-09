import java.util.*;
import java.io.Serializable;

public class Auction implements Serializable {
    private int id;
    private String owner;
    private Date createdAt;
    private Date terminateAt;
    private boolean isInCountDown;

    //used as soon as the database is created
}