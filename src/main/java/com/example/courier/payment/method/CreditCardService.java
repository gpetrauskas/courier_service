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

@Service
public class CreditCardService {
    private static final Logger logger = LoggerFactory.getLogger(CreditCardService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/yy");
    private final PersonService personService;
    private final CurrentPersonService currentPersonService;

    public CreditCardService(PersonService personService, CurrentPersonService currentPersonService) {
        this.personService = personService;
        this.currentPersonService = currentPersonService;
    }

    /*
    * Setup credit card
    * Throws NullPointerException if CreditCardDTO is null
    * validates if users name match cardholder name - throws on failure
    * calls createNewCreditCard method
    * returns new CreditCard
    */
    @Transactional
    public CreditCard setupCreditCard(CreditCardDTO creditCardDTO) {
        Objects.requireNonNull(creditCardDTO, "Credit card details cannot be null");
        User user = personService.fetchPersonByIdAndType(currentPersonService.getCurrentPersonId(), User.class);

        validateCreditCardHolderNameMatchUser(user.getName(), creditCardDTO.cardHolderName());



        CreditCard newCard = createNewCreditCard(creditCardDTO, user);
        logger.info("Credit card set up successfully for user: {}", user.getName());

        return newCard;
    }

    private CreditCard createNewCreditCard(CreditCardDTO creditCardDTO, User user) {
        CreditCard card = new CreditCard();
        card.setUser(user);
        card.setCardNumber(creditCardDTO.cardNumber());
        card.setExpiryDate(creditCardDTO.expiryDate());
        card.setCardHolderName(creditCardDTO.cardHolderName());
        card.setSaved(creditCardDTO.saveCard());

        logger.debug("New credit card created: {}", card);
        return card;
    }

    /*
    * Simulate payment
    * checks specific hardcoded cvc and card number endings, card expiry to simulate failures
    * returns PaymentResultResponse
    */
    public PaymentResultResponse paymentTest(CreditCard card, String cvc) {
        if (hasEmptyFields(card, cvc)) fail("Fields cannot be empty");
        if (isCardExpired(card)) fail("CARD EXPIRED");
        if (card.getCardNumber().endsWith("00")) fail("DECLINED");
        if (cvc.endsWith("3")) fail("REJECTED");

        logger.info("Payment test approved for card: {}", card.maskCardNumber());



        String txId = "txId_" + UUID.randomUUID();

        return success("APPROVED", txId);
    }

    @Transactional
    public CreditCard dontSaveCreditCard(CreditCard card) {
        int ccLength = card.getCardNumber().length();
        card.setCardNumber(card.getCardNumber().substring(ccLength - 4, ccLength));
        logger.info("Card number savd partially for card ending with {}", card.getCardNumber());

        return card;
    }

    /*
    * Helper methods
    */

    /*
    * check if credit card has no empty fields
    * returns false if all typed
    */
    private boolean hasEmptyFields(CreditCard card, String cvc) {
        System.out.println(card + "      " + cvc);
        return Stream.of(card.getCardNumber(), card.getCardHolderName(), card.getExpiryDate(), cvc)
                .anyMatch(s -> s == null || s.isBlank());
    }

    /*
    * Validates if current username matches cardholder name
    * throws if no match
    */
    private void validateCreditCardHolderNameMatchUser(String userName, String ccHolderName) {
        if (!userName.equals(ccHolderName)) {
            fail("Current user name does not match the card holder name");
        }
    }

    /*
    * Checks if credit card is expired
    * return fals if not expired
    */
    private boolean isCardExpired(CreditCard card) {
        YearMonth expiryDate = YearMonth.parse(card.getExpiryDate(), DATE_FORMATTER);
        YearMonth currentDate = YearMonth.now();
        return expiryDate.isBefore(currentDate);
    }

    /*
    * Return PaymentResultResponse message with passed message
    * and 'failure' status as string
    */
    private void fail(String message) {
        logger.error("Payment test failed: {}", message);
        throw new PaymentFailedException(message, ProviderType.CREDIT_CARD, PaymentAttemptStatus.FAILED, "");
    }

    /*
    * Return PaymentResultResponse message with passed message
    * and 'success' status as string
    */
    private PaymentResultResponse success(String message, String txId) {
        return new PaymentResultResponse("success", message, ProviderType.CREDIT_CARD, txId);
    }
}
