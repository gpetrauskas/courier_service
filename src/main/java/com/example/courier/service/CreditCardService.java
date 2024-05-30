package com.example.courier.service;

import com.example.courier.domain.CreditCard;
import com.example.courier.domain.User;
import com.example.courier.dto.CreditCardDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Service
public class CreditCardService {
    private static final Logger logger = LoggerFactory.getLogger(CreditCardService.class);

    public CreditCard setupCreditCard(CreditCardDTO creditCardDTO, User user) {
        if (user == null) {
            throw new IllegalArgumentException("Error loading user. User is null.");
        }
        if (!user.getName().equals(creditCardDTO.cardHolderName())) {
            throw new IllegalArgumentException("Invalid payment method details");
        }

        CreditCard card = new CreditCard();
        card.setUser(user);
        card.setCardNumber(creditCardDTO.cardNumber());
        card.setExpiryDate(creditCardDTO.expiryDate());
        card.setCardHolderName(creditCardDTO.cardHolderName());
        card.setSaved(creditCardDTO.saveCard());

        return card;
    }

    public ResponseEntity<String> paymentTest(CreditCard card, String cvc) {
        if (isCardExpired(card)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CARD EXPIRED");
        }
        if (card.getCardNumber().endsWith("00")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DECLINED");
        }
        if (cvc.endsWith("3")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("REJECTED");
        }

        return ResponseEntity.ok("APPROVED");
    }

    public boolean isCardExpired(CreditCard card) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth expiryDate = YearMonth.parse(card.getExpiryDate(), formatter);
        YearMonth currentDate = YearMonth.now();

        return expiryDate.isBefore(currentDate);
    }

    public CreditCard dontSaveCreditCard(CreditCard card) {
        int ccLength = card.getCardNumber().length();
        card.setCardNumber(card.getCardNumber().substring(ccLength - 4, ccLength));

        return card;
    }
}
