package Branch;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Bid implements Serializable {
    private LocalDateTime start;
    private double bid;
    private Member member;

    public void setStart(LocalDateTime startTime) {
        this.start = startTime;
    }

    public void setBid(double initialBid) {
        this.bid = initialBid;
    }

    public void setMember(Member client) {
        this.member = client;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public double getBid() {
        return bid;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public String toString() {
        return "Bid{" +
                "createdAt=" + start +
                ", bid=" + bid +
                ", bidder='" + member + '\'' +
                '}';
    }
}