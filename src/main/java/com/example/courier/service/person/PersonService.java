package com.example.courier.service.person;

import com.example.courier.domain.Person;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.PersonResponseDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.dto.response.BanHistoryDTO;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Optional<Person> findById(Long id);
    void updatePassword(Long personId, String newPassword);
    void save(Person person);
    boolean checkIfPersonAlreadyExistsByEmail(String email);
    <T extends Person> T fetchPersonByIdAndType(Long id, Class<T> personType);
    List<BanHistoryDTO> getBanHistory(Long personId);
    PaginatedResponseDTO<PersonResponseDTO> findAllPaginated(int page, int size, String role, String keyword);
    void updateDetails(Long personId, PersonDetailsUpdateRequest updateRequest);
    void delete(Long peronId);
    String banUnban(Long personId);
    List<CourierDTO> getAvailableCouriers();
    Long availableCouriersCount();
}
