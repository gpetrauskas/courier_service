package com.example.courier.payment.method;

import com.example.courier.common.PaymentAttemptStatus;
import com.example.courier.common.ProviderType;
import com.example.courier.domain.CreditCard;
import com.example.courier.domain.User;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.OneTimeCard;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.exception.PaymentFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Service for managing credit card setup and simulated payment operations.
 *
 * <p>This service provides functionality for:
 * <ul>
 *     <li>Setting up and saving a new credit card</li>
 *     <li>Charging saved cards and one-time cards</li>
 * </ul>
 *
 * <p>For simulation, certain card numbers, expiration dates,
 * and CVC suffixes trigger test failures.</p>
 */
@Service
public class CreditCardService {
    private static final Logger logger = LoggerFactory.getLogger(CreditCardService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/yy");
    private static final String TEST_REJECT_CVC_SUFFIX = "3";
    private static final String TEST_DECLINE_CARD_SUFFIX = "00";
    private final PaymentMethodService paymentMethodService;

    public CreditCardService(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    /**
     * Sets up and persists a new {@link CreditCard}
     *
     * <p>Performs validation with a mock provider:
     * <ul>
     *     <li>Fails if cardholder name does not match users name</li>
     *     <li>Fails if details are missing or card is expired</li>
     *     <li>Fails on specific hardcoded card/CVC values</li>
     * </ul>
     *
     * @param creditCardDTO {@link CreditCardDTO} details from request
     * @param cvc card security code
     * @param user current user
     * @return saved {@link CreditCard}
     * @throws NullPointerException if details are null
     * @throws PaymentFailedException if validation fails
     */
    @Transactional
    public CreditCard setupCreditCard(CreditCardDTO creditCardDTO, String cvc, User user) {
        Objects.requireNonNull(creditCardDTO, "Credit card details cannot be null");
        Objects.requireNonNull(cvc, "CVC cannot be null");
        Objects.requireNonNull(user, "User cannot be null");

        String token = validateWithProvider(creditCardDTO, cvc, user);

        CreditCard newCard = createNewCreditCard(creditCardDTO, user, token);
        paymentMethodService.saveMethod(newCard);

        logger.info("Credit card set up successfully for user: {}", user.getName());
        return newCard;
    }

    /**
     * Simulates charging a {@link CreditCard}.
     *
     * Checks specific hardcoded cvc and card number endings, card expiry to simulate failures
     * returns {@link PaymentResultResponse} or throws failure depending on test rule.
     *
     * @param card credit card used
     * @param cvc security code
     * @param amount payment amount
     * @return {@link PaymentResultResponse} result of simulated payment
     * @throws PaymentFailedException on validation failure
     */
    public PaymentResultResponse chargeSavedCard(CreditCard card, String cvc, BigDecimal amount) {
        validateCvc(cvc);
        simulateCharge(card, amount);

        logger.info("Payment test approved for card ending: {}", card.getLast4());
        return success("APPROVED", generateTxId());
    }

    /**
     * Simulates charging a one-time card.
     *
     * @param card   one-time card details
     * @param cvc    security code
     * @param amount payment amount
     * @return {@link PaymentResultResponse} representing success
     * @throws PaymentFailedException if validation fails
     */
    public PaymentResultResponse chargeOneTimeCard(OneTimeCard card, String cvc, BigDecimal amount) {
        validateCvc(cvc);
        simulateCharge(card, amount);

        logger.info("Payment test approved for card ending: {}", card.getLast4());
        return success("APPROVED", generateTxId());
    }

    /*
    * Helper methods
    */

    /**
     * Validates the CVC code.
     * <p>Fails if CVC ends with {@value #TEST_REJECT_CVC_SUFFIX}.</p>
     *
     * @param cvc provided CVC code
     * @throws PaymentFailedException if validation fails
     */
    private void validateCvc(String cvc) {
        if (cvc.endsWith(TEST_REJECT_CVC_SUFFIX)) {
            fail("REJECTED");
        }
    }

    /**
     * Simulates provider generated txId on successful card validation
     */
    private String generateTxId() {
        return "txId_" + UUID.randomUUID();
    }

    /**
     * Logs simulation of charging a one-time card.
     */
    private void simulateCharge(OneTimeCard card, BigDecimal amount) {
        logger.info("Charging {}€ with saved card ending {}", amount, card.getLast4());
    }

    /**
     * Logs simulation of charging a saved card.
     */
    private void simulateCharge(CreditCard card, BigDecimal amount) {
        logger.info("Charging {}€ with one-time card ending {}", amount, card.getLast4());    }

    /**
     * Creates card entity
     *
     * Create {@link CreditCard} entity and set {@link User},
     * last4 digits, expiry date, and cardholder name.
     * Uses token if available, otherwise stores raw card number (for one-time use)
     *
     * @param user current user which will be added to the credit card
     * @param creditCardDTO credit card request data
     * @param token tokenized card token ({@code null} is allowed)
     * @return created entity
     */
    private CreditCard createNewCreditCard(CreditCardDTO creditCardDTO, User user, String token) {
        CreditCard card = new CreditCard();
        card.setUser(user);
        card.setLast4(last4Digits(creditCardDTO.cardNumber()));
        card.setExpiryDate(creditCardDTO.expiryDate());
        card.setCardHolderName(creditCardDTO.cardHolderName());
        card.setSaved(creditCardDTO.saveCard());
        card.setToken(token);

        logger.debug("New credit card created: {}", card);
        return card;
    }

    /**
     * Extracts last 4 digits for the card number
     */
    private String last4Digits(String cardNumber) {
        String num = Objects.requireNonNull(cardNumber, "Invalid card number");
        return num.substring(cardNumber.length() - 4);
    }

    /**
     * Simulated token retrieval from a payment provider
     */
    private String requestTokenFromProvider(CreditCardDTO dto) {
        // just simulate
        if (dto == null) {
            fail("dto is null");
        }
        return "tok_" + UUID.randomUUID();
    }

    /**
     * Performs mock validation against some hardcoded rules
     */
    private String validateWithProvider(CreditCardDTO creditCardDTO, String cvc, User user) {
        if (!user.getName().equals(creditCardDTO.cardHolderName())) fail("Cardholder name mismatch");
        if (hasEmptyFields(creditCardDTO, cvc)) fail("Fields cannot be empty");
        if (isCardExpired(creditCardDTO)) fail("CARD EXPIRED");
        if (creditCardDTO.cardNumber().endsWith(TEST_DECLINE_CARD_SUFFIX)) fail("DECLINED");
        if (cvc.endsWith(TEST_REJECT_CVC_SUFFIX)) fail("REJECTED");

        logger.debug("Validation passed for card ending: {}", last4Digits(creditCardDTO.cardNumber()));
        return requestTokenFromProvider(creditCardDTO);
    }

    /**
     * Checks if {@link CreditCardDTO} and cvc has no empty fields
     *
     * @param card the card that is being checked for empty fields
     * @param cvc secure code cannot be empty
     * @return false if no null or empty fields found, false otherwise
     */
    private boolean hasEmptyFields(CreditCardDTO card, String cvc) {
        return Stream.of(card.cardNumber(), card.cardHolderName(), card.expiryDate(), cvc)
                .anyMatch(s -> s == null || s.isBlank());
    }

    /**
     * Checks if {@link CreditCardDTO} is expired
     *
     * @param card the expiry date to be checked from
     * @return true if card is expired, false otherwise
     */
    private boolean isCardExpired(CreditCardDTO card) {
        try {
            YearMonth expiryDate = YearMonth.parse(card.expiryDate(), DATE_FORMATTER);
            YearMonth currentDate = YearMonth.now();
            return expiryDate.isBefore(currentDate);
        } catch (DateTimeParseException ex) {
            throw new DateTimeParseException("Date error", ex.getParsedString(), ex.getErrorIndex());
        }
    }

    /**
     * Throws {@link PaymentFailedException} to indicate failed payment attempt.
     *
     * The exception includes:
     * the provided failure message,
     * provider set to {@code CREDIT_CARD},
     * status set to {@code FAILED},
     * empty transaction id
     *
     * @param message a message with explanation of failure
     * @throws PaymentFailedException always thrown to indicate a failed payment attempt
     */
    private void fail(String message) {
        logger.error("Payment test failed: {}", message);
        throw new PaymentFailedException(message, ProviderType.CREDIT_CARD, PaymentAttemptStatus.FAILED, "");
    }

    /**
     * Returns {@link PaymentResultResponse} indicating a successful payment attempt.
     *
     * The response includes:
     * status set to success,
     * the provided message,
     * provider set to {@code CREDIT_CARD},
     * the provided transaction ID
     *
     * @param message a message describing successful payment
     * @param txId the transaction ID associated with payment
     * @return PaymentResultResponse representing a successful payment
     */
    private PaymentResultResponse success(String message, String txId) {
        return new PaymentResultResponse("success", message, ProviderType.CREDIT_CARD, txId);
    }
}
