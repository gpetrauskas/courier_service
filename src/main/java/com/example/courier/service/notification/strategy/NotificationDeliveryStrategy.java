package com.example.courier.service.notification.strategy;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.service.notification.NotificationTarget;

/** Strategy interface for delivering notifications to a specific type of {@link NotificationTarget}.
 */
public interface NotificationDeliveryStrategy {
    /** Retrieves the {@link NotificationTarget} subtype this strategy supports
     *
     * @return the supported notification target type
     */
    Class<? extends NotificationTarget> getSupportedType();

    /** Delivers notification based on the given erquest.
     *
     * @param requestDTO the notification request details
     * @return the api response
     */
    ApiResponseDTO deliver(NotificationRequestDTO requestDTO);
}
