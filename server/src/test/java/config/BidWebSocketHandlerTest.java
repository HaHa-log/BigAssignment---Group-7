package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("BidWebSocketHandler Lifecycle & Broadcasting Test Suite")
public class BidWebSocketHandlerTest {

    private BidWebSocketHandler handler;
    private WebSocketSession session1;
    private WebSocketSession session2;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        handler = new BidWebSocketHandler();
        mapper = new ObjectMapper();

        session1 = mock(WebSocketSession.class);
        session2 = mock(WebSocketSession.class);

        when(session1.getUri()).thenReturn(URI.create("http://localhost:8080/ws/auctions/100/bids"));
        when(session2.getUri()).thenReturn(URI.create("http://localhost:8080/ws/auctions/100/bids"));
    }

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-ConnectionLifecycleSuccess")
        void testEP_ConnectionLifecycleSuccess() throws Exception {
            handler.afterConnectionEstablished(session1);

            when(session1.isOpen()).thenReturn(true);
            handler.broadcastBid(100, 1500.0);

            String expectedJson = mapper.writeValueAsString(Map.of("auctionId", 100, "currentPrice", 1500.0));
            verify(session1, times(1)).sendMessage(eq(new TextMessage(expectedJson)));

            handler.afterConnectionClosed(session1, CloseStatus.NORMAL);

            Mockito.clearInvocations(session1);
            handler.broadcastBid(100, 1600.0);

            verify(session1, never()).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("EP-Valid-BroadcastToActiveSessions")
        void testEP_BroadcastToActiveSessions() throws Exception {
            handler.afterConnectionEstablished(session1);
            handler.afterConnectionEstablished(session2);

            when(session1.isOpen()).thenReturn(true);
            when(session2.isOpen()).thenReturn(true);

            handler.broadcastBid(100, 250.5);

            String expectedJson = mapper.writeValueAsString(Map.of("auctionId", 100, "currentPrice", 250.5));
            verify(session1, times(1)).sendMessage(eq(new TextMessage(expectedJson)));
            verify(session2, times(1)).sendMessage(eq(new TextMessage(expectedJson)));
        }

        @Test
        @DisplayName("EP-Invalid-BroadcastWhenNoClientConnected")
        void testEP_BroadcastWhenNoClientConnected() throws Exception {
            handler.broadcastBid(999, 500.0);

            verify(session1, never()).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("EP-Invalid-BroadcastSkipClosedSession")
        void testEP_BroadcastSkipClosedSession() throws Exception {
            handler.afterConnectionEstablished(session1);

            when(session1.isOpen()).thenReturn(false);

            handler.broadcastBid(100, 300.0);

            verify(session1, never()).sendMessage(any(TextMessage.class));
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-Connection-IsolateSessionsByAuctionId")
        void testBVA_IsolateSessionsByAuctionId() throws Exception {
            when(session2.getUri()).thenReturn(URI.create("http://localhost:8080/ws/auctions/200/bids"));

            handler.afterConnectionEstablished(session1);
            handler.afterConnectionEstablished(session2);

            when(session1.isOpen()).thenReturn(true);
            when(session2.isOpen()).thenReturn(true);

            handler.broadcastBid(100, 750.0);

            String expectedJson = mapper.writeValueAsString(Map.of("auctionId", 100, "currentPrice", 750.0));
            verify(session1, times(1)).sendMessage(eq(new TextMessage(expectedJson)));

            verify(session2, never()).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("BVA-Broadcast-ExceptionHandlingDoesNotCrashSystem")
        void testBVA_BroadcastExceptionHandling() throws Exception {
            handler.afterConnectionEstablished(session1);
            when(session1.isOpen()).thenReturn(true);

            doThrow(new IOException("Network crash simulation")).when(session1).sendMessage(any(TextMessage.class));

            assertDoesNotThrow(() -> {
                handler.broadcastBid(100, 800.0);
            });

            verify(session1, times(1)).sendMessage(any(TextMessage.class));
        }
    }
}