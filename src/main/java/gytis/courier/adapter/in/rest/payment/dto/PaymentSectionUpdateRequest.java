package gytis.courier.adapter.in.rest.payment.dto;

import gytis.courier.adapter.in.rest.common.validation.NotNullOrEmpty;
import gytis.courier.domain.payment.PaymentStatus;


public record PaymentSectionUpdateRequest(
        @NotNullOrEmpty PaymentStatus status
) {
}
