package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BidWebSocketHandler extends TextWebSocketHandler {

    private final Map<Integer, Set<WebSocketSession>> auctionSessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        int auctionId = extractAuctionId(session);
        auctionSessions
                .computeIfAbsent(auctionId, k -> ConcurrentHashMap.newKeySet())
                .add(session);
        System.out.println("[WS]: Client connected to auction " + auctionId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        int auctionId = extractAuctionId(session);
        Set<WebSocketSession> sessions = auctionSessions.get(auctionId);
        if (sessions != null) {sessions.remove(session);
            if (sessions.isEmpty()) {auctionSessions.remove(auctionId);}
        }
        System.out.println("[WS]: Client disconnected from auction " + auctionId);
    }

    // Gọi từ AuctionService khi có bid mới
    public void broadcastBid(int auctionId, double newPrice, String status) {
        Set<WebSocketSession> sessions = auctionSessions.get(auctionId);

        System.out.println("[WS] auction " + auctionId + " sessions = " + (sessions == null ? 0 : sessions.size()));

        if (sessions == null || sessions.isEmpty()) {
            System.out.println("[WS] no client connected");
            return;
        }
        try {
            String message = mapper.writeValueAsString(Map.of("auctionId", auctionId, "currentPrice", newPrice, "status", status ));
            System.out.println("[WS] sending: " + message);

            TextMessage textMessage = new TextMessage(message);
            for (WebSocketSession session : sessions) {
                if (session == null || !session.isOpen()) {
                    sessions.remove(session);
                    continue;
                }

                try {
                    session.sendMessage(textMessage);
                } catch (Exception e) {
                    sessions.remove(session);
                    e.printStackTrace();
                }
            }

            if (sessions.isEmpty()) {
                auctionSessions.remove(auctionId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int extractAuctionId(WebSocketSession session) {
        String path = session.getUri().getPath(); // /ws/auctions/{auctionId}/bids
        String[] parts = path.split("/");
        return Integer.parseInt(parts[3]);
    }
}
