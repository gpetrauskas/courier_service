package gytis.courier;

import gytis.courier.adapter.out.persistence.ticket.TicketJpaEntity;
import gytis.courier.adapter.out.persistence.ticket.TicketJpaRepository;
import gytis.courier.adapter.webscoket.WebSocketDestinations;
import gytis.courier.application.port.in.auth.AuthTokens;
import gytis.courier.application.port.in.auth.LoginCommand;
import gytis.courier.application.service.auth.LoginService;
import gytis.courier.domain.person.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SubscribeRuleIntegrationTest {
    @Autowired
    LoginService loginService;
    @LocalServerPort
    int port;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    TicketJpaRepository ticketJpaRepository;

    @Test
    void authenticatedUserConnects() throws ExecutionException, InterruptedException {
        AuthTokens authTokens = loginService.login(new LoginCommand("admin@example.com", "pass123"));

        StompSession stompSession = getStompSession(authTokens);

        assertThat(stompSession.isConnected()).isTrue();
    }

    @Test
    void connectionWithoutJwtIsRejected() {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);

        CompletableFuture<StompSession> sessionCF = stompClient.connectAsync("ws://localhost:" + port + "/portfolio", new StompSessionHandlerAdapter() {});

        assertThatThrownBy(sessionCF::get)
                .isInstanceOf(Exception.class);
    }

    @Test
    void userSubscribingToAdminTaskCardsRejected() throws ExecutionException, InterruptedException {
        AuthTokens authTokens = loginService.login(new LoginCommand("user@example.com", "pass123"));

        StompSession stompSession = getStompSession(authTokens);
        System.out.println(Thread.currentThread() + " whhich");

        LinkedBlockingDeque<Object> blockingDeque = subscribe(WebSocketDestinations.ADMIN_TASK_CARD, stompSession);

        assertThat(blockingDeque.poll(1, TimeUnit.SECONDS)).isNull();
        assertThat(stompSession.isConnected()).isTrue();
    }

    @Test
    void adminSubscribingToAdminTaskCards() throws ExecutionException, InterruptedException {
        AuthTokens authTokens = loginService.login(new LoginCommand("admin@example.com", "pass123"));

        StompSession stompSession = getStompSession(authTokens);
        System.out.println(Thread.currentThread() + " whhich");

        LinkedBlockingDeque<Object> blockingDeque = subscribe(WebSocketDestinations.ADMIN_TASK_CARD, stompSession);

        assertThat(blockingDeque.poll(1, TimeUnit.SECONDS)).isEqualTo("works");
        assertThat(stompSession.isConnected()).isTrue();
    }

    @Test
    void userSubscribesToUserNotifications() throws ExecutionException, InterruptedException {
        AuthTokens authTokens = loginService.login(new LoginCommand("user@example.com", "pass123"));

        StompSession stompSession = getStompSession(authTokens);

        LinkedBlockingDeque<Object> blockingDeque = subscribe(WebSocketDestinations.NOTIFICATIONS + Role.USER, stompSession);

        assertThat(blockingDeque.poll(1, TimeUnit.SECONDS)).isEqualTo("works");
    }

    @Test
    void userSubscribingOnAdminNotificationsRejected() throws ExecutionException, InterruptedException {
        AuthTokens authTokens = loginService.login(new LoginCommand("user@example.com", "pass123"));

        StompSession stompSession = getStompSession(authTokens);
        LinkedBlockingDeque<Object> blockingDeque = subscribe(WebSocketDestinations.NOTIFICATIONS + Role.ADMIN, stompSession);

        assertThat(blockingDeque.poll(1, TimeUnit.SECONDS)).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = { "user@example.com", "admin@example.com" })
    void adminOrTicketOwnerSubscribeToTicket(String email) throws ExecutionException, InterruptedException {
        AuthTokens authTokens = loginService.login(new LoginCommand(email, "pass123"));

        TicketJpaEntity ticketJpaEntity = new TicketJpaEntity();
        ticketJpaEntity.setCreatedById(3L);

        TicketJpaEntity savedTicket = ticketJpaRepository.save(ticketJpaEntity);

        StompSession stompSession = getStompSession(authTokens);

        LinkedBlockingDeque<Object> blockingQueue = subscribe(WebSocketDestinations.TICKET + savedTicket.getId(), stompSession);

        assertThat(blockingQueue.poll(1, TimeUnit.SECONDS)).isEqualTo("works");
    }

    @Test
    void userSubscribeToOtherPersonTicketRejected() throws ExecutionException, InterruptedException {
        AuthTokens authTokens = loginService.login(new LoginCommand("courier@example.com", "pass123"));

        TicketJpaEntity ticketJpaEntity = new TicketJpaEntity();
        ticketJpaEntity.setCreatedById(3L);

        TicketJpaEntity savedTicket = ticketJpaRepository.save(ticketJpaEntity);

        StompSession stompSession = getStompSession(authTokens);

        LinkedBlockingDeque<Object> blockingQueue = subscribe(WebSocketDestinations.TICKET + savedTicket.getId(), stompSession);

        assertThat(blockingQueue.poll(1, TimeUnit.SECONDS)).isNull();
    }



    private LinkedBlockingDeque<Object> subscribe(String destination, StompSession stompSession) throws InterruptedException {
        LinkedBlockingDeque<Object> blockingDeque = new LinkedBlockingDeque<>();

        StompFrameHandler frameHandler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingDeque.add(payload);
                System.out.println("which " + Thread.currentThread());
            }
        };

        stompSession.subscribe(destination, frameHandler);

        Thread.sleep(1000);

        simpMessagingTemplate.convertAndSend(destination, "works");

        return blockingDeque;
    }

    private StompSession getStompSession(AuthTokens authTokens) throws InterruptedException, ExecutionException {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);

        stompClient.setMessageConverter(new StringMessageConverter());

        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        httpHeaders.set("Cookie", "jwt=" + authTokens.jwt());

        StompSessionHandler handler = new StompSessionHandlerAdapter() {};

        CompletableFuture<StompSession> future = stompClient.connectAsync("ws://localhost:" + port + "/portfolio", httpHeaders, handler);
        return future.get();
    }
}
