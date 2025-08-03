package com.example.courier.deliverymethodservice;

import com.example.courier.common.DeliveryGroup;
import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.DeliveryMethod;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.ParcelDTO;
import com.example.courier.dto.mapper.DeliveryMethodMapper;
import com.example.courier.dto.request.deliverymethod.CreateDeliveryMethodDTO;
import com.example.courier.dto.request.deliverymethod.UpdateDeliveryMethodDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodAdminResponseDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodUserResponseDTO;
import com.example.courier.exception.DeliveryOptionNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.DeliveryOptionRepository;
import com.example.courier.service.deliveryoption.DeliveryMethodServiceImpl;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.validation.DeliveryOptionValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private DeliveryOptionRepository deliveryOptionRepository;
    private DeliveryMethodMapper deliveryMethodMapper;
    private CurrentPersonService currentPersonService;
    private DeliveryOptionValidator validator;

    private DeliveryMethodServiceImpl deliveryMethodService;

    private List<DeliveryMethod> allOptionsList = List.of(
            createTestDeliveryMethod("light size", "small package", BigDecimal.valueOf(20)),
            createTestDeliveryMethod("heavy weight", "heavy item", BigDecimal.valueOf(30)),
            createTestDeliveryMethod("overnight", "next day delivery", BigDecimal.valueOf(3))
    );

    @BeforeEach
    void setUp() {
        deliveryOptionRepository = mock(DeliveryOptionRepository.class);
        deliveryMethodMapper = mock(DeliveryMethodMapper.class);
        currentPersonService = mock(CurrentPersonService.class);
        validator = new DeliveryOptionValidator();

        deliveryMethodService = new DeliveryMethodServiceImpl(deliveryOptionRepository, deliveryMethodMapper, validator, currentPersonService);
    }

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

                Map<DeliveryGroup, List<DeliveryMethodDTO>> response = deliveryMethodService.getAllDeliveryOptions();

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

                Map<DeliveryGroup, List<DeliveryMethodDTO>> response = deliveryMethodService.getAllDeliveryOptions();

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

                Map<DeliveryGroup, List<DeliveryMethodDTO>> response = deliveryMethodService.getAllDeliveryOptions();

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
    @DisplayName("update delivery option tests")
    class UpdateTests {
        @Test
        @DisplayName("successfully updates delivery option")
        void successfullyUpdates() {
            UpdateDeliveryMethodDTO dto = new UpdateDeliveryMethodDTO(
                    1L, "medium size", "size up to 10kg", BigDecimal.valueOf(10));
            DeliveryMethod updatedOption = createTestDeliveryMethod("small size", "up to 5kg", BigDecimal.valueOf(5));

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
    @DisplayName("add new delivery option tests")
    class AddTests {
        @Test
        @DisplayName("successfully adds new delivery option")
        void addNew_success() {
            CreateDeliveryMethodDTO dto = new CreateDtoBuilder().build();
            DeliveryMethod deliveryMethod = createTestDeliveryMethod(dto.name(), dto.description(), dto.price());

            when(deliveryOptionRepository.existsByName(dto.name())).thenReturn(false);
            when(deliveryMethodMapper.toNewEntity(dto)).thenReturn(deliveryMethod);

            deliveryMethodService.addNewDeliveryOption(dto);

            verify(deliveryOptionRepository).existsByName(dto.name());
            verify(deliveryOptionRepository).save(deliveryMethod);
        }

        @Test
        @DisplayName("throws - option name already exists ")
        void addNew_deliveryNameAlreadyExists() {
            when(deliveryOptionRepository.existsByName("standard")).thenReturn(true);

            assertThrows(ValidationException.class, () ->
                    deliveryMethodService.addNewDeliveryOption(new CreateDeliveryMethodDTO("standard", "3-5 days delivery", BigDecimal.valueOf(5))));
        }

        @Test
        @DisplayName("fail - bad name format")
        void addNew_badNameFormat_fail() {
            CreateDeliveryMethodDTO dto =  new CreateDtoBuilder().withName("invalid_name$$$%").build();
            ValidationException ex = assertThrows(ValidationException.class, () -> deliveryMethodService.addNewDeliveryOption(dto));

            assertEquals("name can only contain letters", ex.getMessage());
        }

        @Test
        @DisplayName("throw - empty description")
        void addNew_fails_emptyDescription() {
            CreateDeliveryMethodDTO dto = new CreateDtoBuilder().withDescription("").build();
            ValidationException ex = assertThrows(ValidationException.class, () -> deliveryMethodService.addNewDeliveryOption(dto));

            assertEquals("description cannot be null or empty.", ex.getMessage());
        }

        @Test
        @DisplayName("negative price - throws")
        void addNew_fails_negativePrice() {
            CreateDeliveryMethodDTO dto = new CreateDtoBuilder().withPrice(BigDecimal.valueOf(-2)).build();
            ValidationException ex = assertThrows(ValidationException.class, () -> deliveryMethodService.addNewDeliveryOption(dto));

            assertEquals("Price must be positive value.", ex.getMessage());
        }

        @Test
        @DisplayName("name too long should throw")
        void nameTooLong_throws() {
            CreateDeliveryMethodDTO dto = new CreateDtoBuilder().withName("thisdeliverymethodnameiswaytoolong").build();
            ValidationException ex = assertThrows(ValidationException.class, () -> deliveryMethodService.addNewDeliveryOption(dto));

            assertEquals("name cannot exceed 20 characters", ex.getMessage());
        }

        static class CreateDtoBuilder {
            private String name = "standard";
            private String description = "up to 5 days delivery";
            private BigDecimal price = BigDecimal.valueOf(5);

            public CreateDtoBuilder withName(String name) {
                this.name = name;
                return this;
            }

            public CreateDtoBuilder withDescription(String description) {
                this.description = description;
                return this;
            }

            public CreateDtoBuilder withPrice(BigDecimal price) {
                this.price = price;
                return this;
            }

            public CreateDeliveryMethodDTO build() {
                return new CreateDeliveryMethodDTO(name, description, price);
            }
        }
    }

    @Nested
    @DisplayName("delete tests")
    class DeleteTests {
        @Test
        @DisplayName("delete success")
        void delete_success() {
            when(deliveryOptionRepository.existsById(1L)).thenReturn(true);

            deliveryMethodService.deleteDeliveryOption(1L);

            verify(deliveryOptionRepository).existsById(1L);
            verify(deliveryOptionRepository).deleteById(1L);
        }

        @Test
        @DisplayName("not found - thorws")
        void delete_notFound() {
            when(deliveryOptionRepository.existsById(1L)).thenReturn(false);

            assertThrows(DeliveryOptionNotFoundException.class, () -> deliveryMethodService.deleteDeliveryOption(1L));

            verify(deliveryOptionRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("calculate shipping cost")
    class CalculateShippingCost {

        private AddressDTO testAddress() {
            return new AddressDTO(1L, "city", "street", "housN", "flatN", "123456789", "123LT", "me");
        }

        private DeliveryMethod createDeliveryMethodWithPrice(BigDecimal price) {
            return new DeliveryMethod("name", "desc", price, false);
        }

        @Test
        @DisplayName("successfully - calculate shipping cost")
        void success() {
            ParcelDTO p = new ParcelDTO(2L, "1", "5", "books", null, ParcelStatus.WAITING_FOR_PAYMENT);
            OrderDTO dto = new OrderDTO(1L, testAddress(), testAddress(), p, "3", OrderStatus.PENDING, LocalDateTime.now());

            when(deliveryOptionRepository.findById(Long.parseLong(p.weight()))).thenReturn(Optional.of(createDeliveryMethodWithPrice(BigDecimal.valueOf(10))));
            when(deliveryOptionRepository.findById(Long.parseLong(p.dimensions()))).thenReturn(Optional.of(createDeliveryMethodWithPrice(BigDecimal.valueOf(30))));
            when(deliveryOptionRepository.findById(Long.parseLong(dto.deliveryMethod()))).thenReturn(Optional.of(createDeliveryMethodWithPrice(BigDecimal.valueOf(20))));

            BigDecimal price = deliveryMethodService.calculateShippingCost(dto);
            assertEquals(BigDecimal.valueOf(60), price);
        }

        @Test
        @DisplayName("delivery option not found - throw")
        void deliveryOptionNotFound_throws() {
            ParcelDTO parcelDTO = new ParcelDTO(1L, "1", "2", "xx", "xxx", ParcelStatus.WAITING_FOR_PAYMENT);
            OrderDTO dto = new OrderDTO(1L, testAddress(), testAddress(), parcelDTO, "3", OrderStatus.PENDING, LocalDateTime.now());

            when(deliveryOptionRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryMethodService.calculateShippingCost(dto))
                    .isInstanceOf(DeliveryOptionNotFoundException.class)
                    .hasMessageContaining("price by delivery options not found");
        }
    }

    @Nested
    @DisplayName("get description by by id")
    class GetDescriptionById {
        @Test
        void getDescriptionById_success() {
            DeliveryMethod xx = createTestDeliveryMethod("yes", "yes yes", BigDecimal.valueOf(50));
            when(deliveryOptionRepository.findById(1L)).thenReturn(Optional.of(xx));

            String description = deliveryMethodService.getDescriptionById(1L);

            assertEquals(xx.getDescription(), description);
            verify(deliveryOptionRepository).findById(1L);
        }

        @Test
        void getDescriptionById_failure() {
            when(deliveryOptionRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(DeliveryOptionNotFoundException.class, () ->
                    deliveryMethodService.getDescriptionById(1L));
        }
    }

    private void mockIsAdmin(boolean is) {
        when(currentPersonService.isAdmin()).thenReturn(is);
    }

    private void mockRepositoryFindAll(List<DeliveryMethod> options) {
        when(deliveryOptionRepository.findAll()).thenReturn(options);
    }

    private void verifyOptionList(Map<DeliveryGroup, List<DeliveryMethodDTO>> response) {
        assertEquals(3, response.size());
        assertThat(response.get(DeliveryGroup.SIZE)).hasSize(1);
        assertThat(response.get(DeliveryGroup.WEIGHT)).hasSize(1);
        assertThat(response.get(DeliveryGroup.PREFERENCE)).hasSize(1);
    }

    private DeliveryMethod createTestDeliveryMethod(String name, String description, BigDecimal price) {
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
