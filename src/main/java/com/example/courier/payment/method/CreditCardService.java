package com.example.courier.payment.method;

import com.example.courier.common.PaymentAttemptStatus;
import com.example.courier.common.ProviderType;
import com.example.courier.domain.CreditCard;
import com.example.courier.domain.User;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.exception.PaymentFailedException;
import com.example.courier.service.person.PersonService;
import com.example.courier.service.security.CurrentPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Service for managing credit card setup and payment operations
 */
@Service
public class CreditCardService {
    private static final Logger logger = LoggerFactory.getLogger(CreditCardService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/yy");
    private final PersonService personService;
    private final CurrentPersonService currentPersonService;
    private final PaymentMethodService paymentMethodService;

    public CreditCardService(PersonService personService, CurrentPersonService currentPersonService,
                             PaymentMethodService paymentMethodService) {
        this.personService = personService;
        this.currentPersonService = currentPersonService;
        this.paymentMethodService = paymentMethodService;
    }

    /**
     * Sets up and persists a new {@link CreditCard}
     *
     * Validates if users name match cardholder name
     * throws on mismatch. On success, creates and stores the card,
     * returning the saved entity
     *
     * @param creditCardDTO {@link CreditCardDTO} details from request
     * @return saved {@link CreditCard}
     * @throws NullPointerException if details are null
     * @throws PaymentFailedException if validation fails
     */
    @Transactional
    public CreditCard setupCreditCard(CreditCardDTO creditCardDTO) {
        Objects.requireNonNull(creditCardDTO, "Credit card details cannot be null");

        System.out.println("details: " + creditCardDTO.saveCard() + " " + creditCardDTO.cardNumber() + " " +
                " " + creditCardDTO.expiryDate() + " " + creditCardDTO.cardHolderName());
        User user = personService.fetchPersonByIdAndType(currentPersonService.getCurrentPersonId(), User.class);

        System.out.println("name: " + user.getName() + " and: " + creditCardDTO.cardHolderName());

        validateCreditCardHolderNameMatchUser(user.getName(), creditCardDTO.cardHolderName());

        CreditCard newCard = createNewCreditCard(creditCardDTO, user);
        logger.info("Credit card set up successfully for user: {}", user.getName());

        return newCard;
    }

    /**
     * Simulate {@link CreditCard} payment
     *
     * checks specific hardcoded cvc and card number endings, card expiry to simulate failures
     * returns {@link PaymentResultResponse} or throws failure depending on test rule
     *
     * @param card credit card used
     * @param cvc security code
     * @return result of simulated payment
     * @throws PaymentFailedException on validation failure
     */
    public PaymentResultResponse paymentTest(CreditCard card, String cvc) {
        if (hasEmptyFields(card, cvc)) fail("Fields cannot be empty");
        if (isCardExpired(card)) fail("CARD EXPIRED");
        if (card.getLast4().endsWith("00")) fail("DECLINED");
        if (cvc.endsWith("3")) fail("REJECTED");

        logger.info("Payment test approved for card ending: {}", card.getLast4());
        String txId = "txId_" + UUID.randomUUID();

        return success("APPROVED", txId);
    }

    /*
    * Helper methods
    */

    /**
     * Creates and saves card entity
     *
     * Create {@link CreditCard} entity set and save {@link User}, last4 digits, expiry date, and cardholder name.
     * On user save card selection, create a tokenized simulated credit card token or
     * leave empty if save is se to false
     *
     * @param user current user which will be added to the credit card
     * @param creditCardDTO credit card request data
     * @return saved entity
     */
    private CreditCard createNewCreditCard(CreditCardDTO creditCardDTO, User user) {
        String last4 = creditCardDTO.cardNumber().substring(creditCardDTO.cardNumber().length() - 4);
        String tokenizedCCNumber = "tok_";
        if (creditCardDTO.saveCard()) {
            tokenizedCCNumber = tokenizedCCNumber + UUID.randomUUID();
        }

        CreditCard card = new CreditCard();
        card.setUser(user);
        card.setToken(tokenizedCCNumber);
        card.setLast4(last4);
        card.setExpiryDate(creditCardDTO.expiryDate());
        card.setCardHolderName(creditCardDTO.cardHolderName());
        card.setSaved(creditCardDTO.saveCard());

        logger.debug("New credit card created: {}", card);
        paymentMethodService.saveMethod(card);

        return card;
    }

    /**
     * check if {@link CreditCard} has no empty fields
     *
     * @param card the card that is being checked for empty fields
     * @param cvc secure code cannot be empty
     * @return tru if no null or empty fields found, false otherwise
     */
    private boolean hasEmptyFields(CreditCard card, String cvc) {
        return Stream.of(card.getLast4(), card.getCardHolderName(), card.getExpiryDate(), cvc)
                .anyMatch(s -> s == null || s.isBlank());
    }

    /**
     * Validates if current username matches cardholder name
     *
     * @param userName users name fetched from user entity
     * @param ccHolderName cardholder name
     *
     * @throws PaymentFailedException if users name do not match the cardholder name
     */
    private void validateCreditCardHolderNameMatchUser(String userName, String ccHolderName) {
        if (!userName.equals(ccHolderName)) {
            fail("Current user name does not match the card holder name");
        }
    }

    /**
     * Checks if {@link CreditCard} is expired
     *
     * @param card the expiry date to be checked from
     * @return true if card is expired, false otherwise
     */
    private boolean isCardExpired(CreditCard card) {
        YearMonth expiryDate = YearMonth.parse(card.getExpiryDate(), DATE_FORMATTER);
        YearMonth currentDate = YearMonth.now();
        return expiryDate.isBefore(currentDate);
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
     * The response include:
     * status set to success,
     * the provided message,
     * provider set to {@code CREDIT_CARD},
     * the provided transaction ID
     *
     * @param message a message describing successful payment
     * @param txId the transaction ID associated with oayment
     * @return PaymentResultResponse representing a successful payment
     */
    private PaymentResultResponse success(String message, String txId) {
        return new PaymentResultResponse("success", message, ProviderType.CREDIT_CARD, txId);
    }
}
