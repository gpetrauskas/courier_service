package com.example.courier.service.payment;

import com.example.courier.domain.CreditCard;
import com.example.courier.domain.User;
import com.example.courier.dto.CreditCardDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Service
public class CreditCardService {
    private static final Logger logger = LoggerFactory.getLogger(CreditCardService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/yy");

    @Transactional
    public CreditCard setupCreditCard(CreditCardDTO creditCardDTO, User user) {
        validateCardDetails(user, creditCardDTO);

        CreditCard newCard = createNewCreditCard(creditCardDTO, user);
        logger.info("Credit card set up successfully for user: {}", user.getName());
        return newCard;
    }

    private void validateCardDetails(User user, CreditCardDTO creditCardDTO) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (!user.getName().equals(creditCardDTO.cardHolderName())) {
            throw new IllegalArgumentException("Card holder name does not match the card user name");
        }
    }

    @Transactional
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

    public ResponseEntity<String> paymentTest(CreditCard card, String cvc) {
        if (card.getCardNumber().isEmpty() || card.getCardHolderName().isEmpty() || card.getExpiryDate().isEmpty() || cvc.isEmpty()) {
            logger.error("Payment test failed. Some fields are empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fields annot be empty");
        }
        if (isCardExpired(card)) {
            logger.error("Payment test failed. Card is expired;");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CARD EXPIRED");
        }
        if (card.getCardNumber().endsWith("00")) {
            logger.error("Payment test failed. Card declined");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DECLINED");
        }
        if (cvc.endsWith("3")) {
            logger.error("Payment test failed. Card rejected/");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("REJECTED");
        }

        logger.info("Payment test aprved for card: {}", card.maskCardNumber());
        return ResponseEntity.ok("APPROVED");
    }

    public boolean isCardExpired(CreditCard card) {
        YearMonth expiryDate = YearMonth.parse(card.getExpiryDate(), DATE_FORMATTER);
        YearMonth currentDate = YearMonth.now();
        boolean expired = expiryDate.isBefore(currentDate);

        if (expired) {
            logger.warn("Card {} is expired", card.maskCardNumber());
        }

        return expired;
    }

    @Transactional
    public CreditCard dontSaveCreditCard(CreditCard card) {
        int ccLength = card.getCardNumber().length();
        card.setCardNumber(card.getCardNumber().substring(ccLength - 4, ccLength));
        logger.info("Card number savd partially for card ending with {}", card.getCardNumber());

        return card;
    }
}
