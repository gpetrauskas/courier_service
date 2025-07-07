package com.example.courier.service.person;

import com.example.courier.domain.Person;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.PaginatedResponseDTO;

import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.dto.request.person.UserEditDTO;
import com.example.courier.dto.response.BanHistoryDTO;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;
import com.example.courier.dto.response.person.PersonResponseDTO;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Optional<Person> findById(Long id);
    void updatePassword(Long personId, String newPassword);
    void save(Person person);
    boolean checkIfPersonAlreadyExistsByEmail(String email);
    <T extends Person> T fetchPersonByIdAndType(Long id, Class<T> personType);
    List<BanHistoryDTO> getBanHistory(Long personId);
    PaginatedResponseDTO<AdminPersonResponseDTO> findAllPaginated(int page, int size, String role, String keyword, String sortBy, String direction);
    void updateDetails(Long personId, PersonDetailsUpdateRequest updateRequest);
    void delete(Long peronId);
    String banUnban(Long personId);
    List<CourierDTO> getAvailableCouriers();
    Long availableCouriersCount();
    PersonResponseDTO myInfo();
    ApiResponseDTO updateMyInfo(UserEditDTO dto);
    ApiResponseDTO changePassword(PasswordChangeDTO dto);
    <T extends Person> List<T> fetchAllByType(Class<T> personType);
    <T extends Person> List<T> getAllActiveByType(Class<T> tClass);
    List<Long> findAllActiveIdsByType(Class<? extends Person> type);
}
