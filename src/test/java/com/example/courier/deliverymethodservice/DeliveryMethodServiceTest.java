package com.example.courier.deliverymethodservice;

import com.example.courier.domain.DeliveryMethod;
import com.example.courier.dto.mapper.DeliveryMethodMapper;
import com.example.courier.dto.request.deliverymethod.CreateDeliveryMethodDTO;
import com.example.courier.dto.request.deliverymethod.UpdateDeliveryMethodDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodAdminResponseDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodUserResponseDTO;
import com.example.courier.exception.DeliveryOptionNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.DeliveryOptionRepository;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.validation.DeliveryOptionValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryMethodServiceTest {
    @Mock
    private DeliveryOptionRepository deliveryOptionRepository;
    @Mock
    private DeliveryMethodMapper deliveryMethodMapper;
    @Mock
    private CurrentPersonService currentPersonService;
    @Mock
    DeliveryOptionValidator validator;

    @InjectMocks
    private DeliveryMethodService deliveryMethodService;

    private List<DeliveryMethod> allOptionsList = List.of(
            createTestDeliverymethod("light size", "small package", BigDecimal.valueOf(20)),
            createTestDeliverymethod("heavy weight", "heavy item", BigDecimal.valueOf(30)),
            createTestDeliverymethod("overnight", "next day delivery", BigDecimal.valueOf(3))
    );

    @Nested
    @DisplayName("get delivery options tests")
    class GetTests {
        @Nested
        @DisplayName("success tests")
        class SuccessTests {
            @Test
            @DisplayName("returns all delivery options grouped for admin")
            void returnAllDeliveryOptionsGroupedForAdmin() {
                mockIsAdmin(true);
                mockRepositoryFindAll(allOptionsList);
                when(deliveryMethodMapper.toAdminDeliveryOptionResponseDTO(any(DeliveryMethod.class)))
                        .thenAnswer(invocationOnMock -> newDeliveryMethodDTO.forAdmin(invocationOnMock.getArgument(0)));

                Map<String, List<DeliveryMethodDTO>> response = deliveryMethodService.getAllDeliveryOptions();

                verifyOptionList(response);
                verify(deliveryOptionRepository).findAll();
                verify(currentPersonService).isAdmin();
                verify(deliveryMethodMapper, times(3)).toAdminDeliveryOptionResponseDTO(any());
            }

            @Test
            @DisplayName("return all delivery options grouped for simple user")
            void returnAllDeliveryOptionsGroupedForUser() {
                mockIsAdmin(false);
                mockRepositoryFindAll(allOptionsList);
                when(deliveryMethodMapper.toUserDeliveryOptionResponseDTO(any(DeliveryMethod.class)))
                        .thenAnswer(invocationOnMock -> newDeliveryMethodDTO.forUser(invocationOnMock.getArgument(0)));

                Map<String, List<DeliveryMethodDTO>> response = deliveryMethodService.getAllDeliveryOptions();

                verifyOptionList(response);
                verify(deliveryOptionRepository).findAll();
                verify(currentPersonService).isAdmin();
                verify(deliveryMethodMapper, times(3)).toUserDeliveryOptionResponseDTO(any());
            }

            @Test
            @DisplayName("return empty list")
            void returnAllDeliveryOptions_empty() {
                mockIsAdmin(true);
                mockRepositoryFindAll(List.of());

                Map<String, List<DeliveryMethodDTO>> response = deliveryMethodService.getAllDeliveryOptions();

                assertTrue(response.isEmpty());
            }
        }


        @Nested
        @DisplayName("failure tests")
        class FailureTests {
            @Test
            @DisplayName("throws if isAdmin check fails")
            void isAdminThrows_unauthenticated() {
                when(currentPersonService.isAdmin()).thenThrow(new UnauthorizedAccessException("Not logged in."));
                assertThrows(UnauthorizedAccessException.class, () -> deliveryMethodService.getAllDeliveryOptions());
            }

            @Test
            @DisplayName("throws if admin ampping fails")
            void throwsIfAdminMappingFails() {
                DeliveryMethod invalidMethod = new DeliveryMethod();

                mockIsAdmin(true);
                mockRepositoryFindAll(List.of(invalidMethod));
                when(deliveryMethodMapper.toAdminDeliveryOptionResponseDTO(invalidMethod))
                        .thenAnswer(invocationOnMock -> newDeliveryMethodDTO.forAdmin(invalidMethod));

                assertThrows(IllegalArgumentException.class, () -> deliveryMethodService.getAllDeliveryOptions());
            }

            @Test
            @DisplayName("throws when mapper returns invalid dto")
            void throwsWhenMapperReturnsInvalidDto() {
                mockIsAdmin(false);
                mockRepositoryFindAll(allOptionsList);
                when(deliveryMethodMapper.toUserDeliveryOptionResponseDTO(any(DeliveryMethod.class)))
                        .thenReturn(new DeliveryMethodUserResponseDTO(null, null, null, null));

                assertThatThrownBy(() -> deliveryMethodService.getAllDeliveryOptions())
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("cannot be null");
            }
        }
    }

    @Nested
    @DisplayName("update delivery option")
    class UpdateTests {
        @Test
        @DisplayName("successfully updates delivery option")
        void successfullyUpdates() {
            UpdateDeliveryMethodDTO dto = new UpdateDeliveryMethodDTO(
                    1L, "medium size", "size up to 10kg", BigDecimal.valueOf(10));
            DeliveryMethod updatedOption = createTestDeliverymethod("small size", "up to 5kg", BigDecimal.valueOf(5));

            when(deliveryOptionRepository.findById(1L)).thenReturn(Optional.of(updatedOption));
            doAnswer(invocationOnMock -> {
                UpdateDeliveryMethodDTO deliveryMethodDTO = invocationOnMock.getArgument(0);
                DeliveryMethod method = invocationOnMock.getArgument(1);
                method.setName(deliveryMethodDTO.name());
                method.setDescription(deliveryMethodDTO.description());
                method.setPrice(deliveryMethodDTO.price());
                return null;
            }).when(deliveryMethodMapper).updateDeliveryOptionFromDTO(any(), any());

            deliveryMethodService.updateDeliveryOption(1L, dto);

            verify(deliveryOptionRepository).save(updatedOption);
            verify(deliveryMethodMapper).updateDeliveryOptionFromDTO(dto, updatedOption);

            assertEquals(dto.name(), updatedOption.getName());
            assertEquals(dto.description(), updatedOption.getDescription());
            assertEquals(dto.price(), updatedOption.getPrice());
        }
        @Test
        @DisplayName("throws when option not found")
        void throws_notFound() {
            when(deliveryOptionRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(DeliveryOptionNotFoundException.class, () ->
                    deliveryMethodService.updateDeliveryOption(1L , any(UpdateDeliveryMethodDTO.class)));
        }
    }

    @Nested
    @DisplayName("add new delivery option")
    class AddTests {
        @Test
        @DisplayName("successfully adds new delivery option")
        void addNew_success() {
            CreateDeliveryMethodDTO dto = new CreateDeliveryMethodDTO("standard", "3 - 5 days delivery", BigDecimal.valueOf(3));
            DeliveryMethod deliveryMethod = new DeliveryMethod(dto.name(), dto.description(), dto.price(), false);
            when(deliveryOptionRepository.existsByName(dto.name())).thenReturn(false);
            when(deliveryMethodMapper.toNewEntity(dto)).thenReturn(deliveryMethod);

            deliveryMethodService.addNewDeliveryOption(dto);

            verify(deliveryOptionRepository).existsByName(dto.name());
            verify(validator).validateDeliveryOptionForCreation(dto);
            verify(deliveryOptionRepository).save(deliveryMethod);
        }
    }

    private void mockIsAdmin(boolean is) {
        when(currentPersonService.isAdmin()).thenReturn(is);
    }

    private void mockRepositoryFindAll(List<DeliveryMethod> options) {
        when(deliveryOptionRepository.findAll()).thenReturn(options);
    }

    private void verifyOptionList(Map<String, List<DeliveryMethodDTO>> response) {
        assertEquals(3, response.size());
        assertThat(response.get("size")).hasSize(1);
        assertThat(response.get("weight")).hasSize(1);
        assertThat(response.get("preference")).hasSize(1);
    }

    private DeliveryMethod createTestDeliverymethod(String name, String description, BigDecimal price) {
        return new DeliveryMethod(name, description, price, false);
    }

    static class newDeliveryMethodDTO {
        static DeliveryMethodAdminResponseDTO forAdmin(DeliveryMethod deliveryMethod) {
            return new DeliveryMethodAdminResponseDTO(
                    deliveryMethod.getId(), deliveryMethod.getName(), deliveryMethod.getDescription(),
                    deliveryMethod.getPrice(), deliveryMethod.isDisabled()
            );
        }
        static DeliveryMethodUserResponseDTO forUser(DeliveryMethod deliveryMethod) {
            return new DeliveryMethodUserResponseDTO(
                    deliveryMethod.getId(), deliveryMethod.getName(), deliveryMethod.getDescription(),
                    deliveryMethod.getPrice()
            );
        }
    }
}
