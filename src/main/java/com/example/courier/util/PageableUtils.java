package com.example.courier.util;

import com.example.courier.dto.request.task.DeliveryTaskFilterDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {
    public static Pageable toPageable(DeliveryTaskFilterDTO dto) {
        return PageRequest.of(dto.page(), dto.size(), Sort.by(dto.direction(), dto.sortBy()));
    }
}
