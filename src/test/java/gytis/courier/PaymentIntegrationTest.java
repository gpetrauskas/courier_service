package gytis.courier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gytis.courier.adapter.in.rest.payment.dto.CreditCardRequest;
import gytis.courier.adapter.in.rest.payment.dto.PaymentMethodRequest;
import gytis.courier.adapter.in.rest.payment.dto.PaymentRequest;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.port.out.order.OrderCommandPort;
import gytis.courier.application.port.out.payment.PaymentCommandPort;
import gytis.courier.application.port.out.paymentmethod.PaymentMethodCommandPort;
import gytis.courier.application.port.out.person.UserCommandPort;
import gytis.courier.application.result.PaymentResult;
import gytis.courier.domain.address.AddressDetails;
import gytis.courier.domain.delivery.DeliveryOption;
import gytis.courier.domain.order.Order;
import gytis.courier.domain.order.OrderAddress;
import gytis.courier.domain.order.Parcel;
import gytis.courier.domain.payment.Payment;
import gytis.courier.domain.payment.PaymentAttempt;
import gytis.courier.domain.payment.PaymentStatus;
import gytis.courier.domain.payment.method.PaymentMethod;
import gytis.courier.domain.person.Email;
import gytis.courier.domain.person.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PaymentIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserCommandPort userCommandPort;
    @Autowired
    OrderCommandPort orderCommandPort;
    @Autowired
    PaymentCommandPort paymentCommandPort;
    @Autowired
    PaymentMethodCommandPort paymentMethodCommandPort;
    @Autowired
    ObjectMapper objectMapper;
    User user;
    Order order;

    @BeforeEach
    public void setUp() {
        user = createUser();
        order = createOrder(user);
    }

    @Test
    void successfullyPays() throws Exception {
        Payment payment = Payment.create(order.getId(), order.calculateShippingCost());
        paymentCommandPort.create(payment);

        PaymentRequest paymentRequest = creditCardPaymentRequest(false, "123");

        mockMvc.perform(post("/api/payment/{orderId}/pay", order.getId())
                .with(authentication(authenticateAs(user)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerType").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.transactionId").isNotEmpty());

        User user1 = userCommandPort.findWithPaymentMethodsById(user.getId()).orElseThrow();

        assertTrue(user1.getPaymentMethods().isEmpty());
    }

    @Test
    void successfullyPaysSaved() throws Exception {
        Payment payment = Payment.create(order.getId(), order.calculateShippingCost());
        paymentCommandPort.create(payment);

        PaymentRequest paymentRequest = creditCardPaymentRequest(true, "123");

        mockMvc.perform(post("/api/payment/{orderId}/pay", order.getId())
                .with(authentication(authenticateAs(user)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerType").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.transactionId").isNotEmpty());

        Payment saved = paymentCommandPort.findByOrderId(order.getId()).orElseThrow();
        User userWithMethod = userCommandPort.findWithPaymentMethodsById(user.getId()).orElseThrow();
        PaymentMethod method = userWithMethod.getPaymentMethods().getFirst();

        assertEquals(PaymentStatus.PAID, saved.getStatus());
        assertTrue(method.hasToken());
    }

    @Test
    void throwsOnBadCvc() throws Exception{
        Payment payment = Payment.create(order.getId(), order.calculateShippingCost());
        paymentCommandPort.create(payment);

        PaymentRequest paymentRequest = creditCardPaymentRequest(false, "000");

        mockMvc.perform(post("/api/payment/{orderId}/pay", order.getId())
                        .with(authentication(authenticateAs(user)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerType").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.failureReason").isNotEmpty())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.transactionId").isEmpty());
    }





    private User createUser() {
        User user = new User(2L, "user", new Email("user@example.com"), "encodedPass");
        userCommandPort.create(user);

        return user;
    }

    private Order createOrder(User user) {
        OrderAddress address = new OrderAddress(new AddressDetails("name", "street", "2", null, "Klaipeda", "91000", "123456789"));
        DeliveryOption weight = new DeliveryOption(1L, "medium", "up to 15kg", BigDecimal.valueOf(5), false);
        DeliveryOption method = new DeliveryOption(2L, "overnight", "next day delivery", BigDecimal.valueOf(12), false);
        DeliveryOption dimensions = new DeliveryOption(3L, "small", "dimensions up to 40x40x40", BigDecimal.valueOf(1), false);
        Parcel parcel = new Parcel(weight, dimensions, "books");

        return orderCommandPort.insert(Order.create(user.getId(), address, address, parcel, method));
    }

    private PaymentRequest creditCardPaymentRequest(boolean saved, String cvc) {
        PaymentMethodRequest newMethod = new CreditCardRequest("1111222233334444", "user", "12/28", saved);
        return new PaymentRequest(null, newMethod, cvc);
    }

    private Authentication authenticateAs(User user) {
        return new UsernamePasswordAuthenticationToken(
                new AuthenticatedPerson(user.getId(),
                        user.getEmail().email(),
                        user.getRole(),
                        user.getName()
                ),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
