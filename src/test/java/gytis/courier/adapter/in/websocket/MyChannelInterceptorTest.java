package gytis.courier.adapter.in.websocket;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.adapter.webscoket.WebSocketDestinations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MyChannelInterceptorTest {
    @Mock
    MessageChannel messageChannel;

    @Mock
    SubscriptionRule rule;

    MyChannelInterceptor interceptor;

    @BeforeEach
    void set() {
        interceptor = new MyChannelInterceptor(List.of(rule));
    }

    private Message<?> message(StompCommand command, String destination, Authentication authentication) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        accessor.setDestination(destination);

        if (authentication != null) {
            accessor.setUser(authentication);
        }

        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private Authentication person(String role) {
        return new UsernamePasswordAuthenticationToken(
                new AuthenticatedPerson(1L, "person@example.com", role, "person"),
                null
        );
    }

    @Test
    public void nonSubscribeMessageIsPassedThrough() {
        Message<?> message = message(StompCommand.CONNECT, null, null);

        Message<?> result = interceptor.preSend(message, messageChannel);

        assertThat(result).isSameAs(message);
    }

    @ParameterizedTest
    @ValueSource(strings = {" "})
    @NullAndEmptySource
    public void subscribeWithMissingOrBlankDestinationIsRejected(String destination) {
        Message<?> message = message(StompCommand.SUBSCRIBE, destination, null);

        Message<?> result = interceptor.preSend(message, messageChannel);

        assertThat(result).isNull();
    }

    @Test
    public void subscribeWithoutUserIsRejected() {
        Message<?> message = message(StompCommand.SUBSCRIBE, WebSocketDestinations.ADMIN_TASK_CARD, null);

        Message<?> result = interceptor.preSend(message, messageChannel);

        assertThat(result).isNull();
    }

    @Test
    public void subscribeWithoutUserIsRejectedEvenRuleAllows() {
        Message<?> message = message(StompCommand.SUBSCRIBE, WebSocketDestinations.ADMIN_TASK_CARD, null);

        Mockito.lenient().when(rule.authorize(any(String.class), any())).thenReturn(true);
        Mockito.lenient().when(rule.matches(any(String.class))).thenReturn(true);

        Message<?> result = interceptor.preSend(message, messageChannel);

        assertThat(result).isNull();
        verifyNoInteractions(rule);
    }

    @Test
    public void subscribeWithUserAndNoMatchingRuleIsRejected() {
        Authentication authentication = person("ADMIN");

        Message<?> message = message(StompCommand.SUBSCRIBE, "xx", authentication);

        when(rule.matches(any())).thenReturn(false);
        Mockito.lenient().when(rule.authorize(any(), any())).thenReturn(true);

        Message<?> result = interceptor.preSend(message, messageChannel);

        assertThat(result).isNull();
/*        assertThatThrownBy(() -> interceptor.preSend(message, messageChannel))
                .isInstanceOf(MessageDeliveryException.class);*/
    }

    @Test
    public void subscribeWithUserAndAllowingRulesIsPassed() {
        Authentication authentication = person("USER");

        Message<?> message = message(StompCommand.SUBSCRIBE, WebSocketDestinations.NOTIFICATIONS, authentication);

        when(rule.matches(any())).thenReturn(true);
        when(rule.authorize(any(), any())).thenReturn(true);

        Message<?> resp = interceptor.preSend(message, messageChannel);

        assertThat(message).isSameAs(resp);
    }

    @Test
    public void subscribeWithUserAndDenyingRuleIsRejected() {
        Authentication authentication = person("USER");

        Message<?> message = message(StompCommand.SUBSCRIBE, WebSocketDestinations.NOTIFICATIONS, authentication);

        when(rule.matches(any())).thenReturn(true);
        when(rule.authorize(any(), any())).thenReturn(false);

        Message<?> result = interceptor.preSend(message, messageChannel);

        assertThat(result).isNull();
/*        assertThatThrownBy(() ->  interceptor.preSend(message, messageChannel))
                .isInstanceOf(MessageDeliveryException.class);*/
    }
}
