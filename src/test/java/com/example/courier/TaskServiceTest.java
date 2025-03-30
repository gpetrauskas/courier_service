package com.example.courier;

import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.CreateTaskDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.DeliveryTaskMapper;
import com.example.courier.dto.request.task.DeliveryTaskFilterDTO;
import com.example.courier.dto.response.task.AdminTaskDTO;
import com.example.courier.dto.response.task.TaskBase;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.TaskRepository;
import com.example.courier.service.order.OrderService;
import com.example.courier.service.parcel.ParcelService;
import com.example.courier.service.person.PersonService;
import com.example.courier.service.task.TaskItemService;
import com.example.courier.service.task.TaskService;
import com.example.courier.specification.task.TaskSpecificationBuilder;
import com.example.courier.util.AuthUtils;
import com.example.courier.validation.task.TaskValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock private PersonService personService;
    @Mock private ParcelService parcelService;
    @Mock private OrderService orderService;
    @Mock private final TaskValidator taskValidator = new TaskValidator();
    @Mock private TaskRepository taskRepository;
    @Mock private TaskSpecificationBuilder specificationBuilder;
    @Mock private TaskItemService taskItemService;
    @Mock private DeliveryTaskMapper deliveryTaskMapper;

    @InjectMocks private TaskService taskService;

    @Test
    void createNewDeliveryTask_Success() {
        Parcel parcel = spy(new Parcel());
        parcel.setStatus(ParcelStatus.PICKED_UP);

        Order order = new Order();
        order.setParcelDetails(parcel);

        Courier courier = new Courier();
        Admin admin = new Admin();
        CreateTaskDTO dto = new CreateTaskDTO(2L, null, List.of(1L), "DELIVERY");

        try (MockedStatic<AuthUtils> authUtilsMock = mockStatic(AuthUtils.class)) {
            authUtilsMock.when(() -> AuthUtils.getAuthenticated(Admin.class))
                    .thenReturn(admin);

            when(personService.fetchPersonByIdAndType(2L, Courier.class))
                    .thenReturn(courier);
            when(parcelService.fetchParcelsByIdBatch(any())).thenReturn(List.of(parcel));
            when(orderService.fetchAllByParcelDetails(any())).thenReturn(List.of(order));

            when(taskItemService.createTaskItems(any(), any(), any()))
                    .thenAnswer(invocation -> {
                        List<Parcel> parcels = invocation.getArgument(0);
                        List<Order> orders = invocation.getArgument(1);
                        Task task = invocation.getArgument(2);
                        return parcels.stream()
                                .map(p -> TaskItem.from(p, orders.get(0), task))
                                .toList();
                    });

            taskService.createNewDeliveryTask(dto);

            assertEquals(ParcelStatus.DELIVERING, parcel.getStatus());
            verify(parcel).assign();
            verify(taskRepository).save(any(Task.class));
            verify(personService).save(courier);
        }
    }

    @Test
    void createNewDeliveryTask_CourierNotFound_ThrowsException() {
        CreateTaskDTO dto = new CreateTaskDTO(999L, null, List.of(1L), "DELIVERY");
        when(personService.fetchPersonByIdAndType(999L, Courier.class))
                .thenThrow(new ResourceNotFoundException("Courier not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.createNewDeliveryTask(dto));
    }

    @Test
    void getAllTaskLists_AdminView_ReturnsPaginatedResults() {
        try (MockedStatic<AuthUtils> authUtils = mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::isAdmin).thenReturn(true);

            Specification<Task> mockSpec = (root, query, cb) -> null;
            when(specificationBuilder.buildTaskSpecification(any(DeliveryTaskFilterDTO.class)))
                    .thenReturn(mockSpec);

            Task task = new Task();
            Page<Task> mockPage = new PageImpl<>(List.of(task));
            when(taskRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(mockPage);

            when(deliveryTaskMapper.toDeliveryTaskDTO(task))
                    .thenReturn(new AdminTaskDTO(null, null, null, null, null, null, null, null, null));

            PaginatedResponseDTO<? extends TaskBase> result = taskService.getAllTaskLists(
                    new DeliveryTaskFilterDTO(0, 10, null, null, null, null, "createdAt", Sort.Direction.ASC)
            );

            assertEquals(1, result.data().size()); // or data() depending on your record
        }
    }

    @Test
    void validateCreation_validInput_passes() {
        CreateTaskDTO dto = new CreateTaskDTO(1L, null, List.of(101L), "DELIVERY");
        Courier courier = new Courier();
        Parcel parcel = new Parcel();
        parcel.setId(101L);

        assertDoesNotThrow(() -> taskValidator.validateCreation(dto, courier, List.of(parcel)));
    }

    @Test
    void validateCreation_ParcelCountMismatch_Throws() {
        CreateTaskDTO dto = new CreateTaskDTO(1L, null, List.of(101L), "DELIVERY");

        Courier courier = new Courier();

        List<Parcel> emptyList = List.of();

        assertThrows(IllegalArgumentException.class,
                () -> new TaskValidator().validateCreation(dto, courier, emptyList));
    }

    @Test
    void validateCreation_ParcelAlreadyAssigned_Throws() {
        CreateTaskDTO dto = new CreateTaskDTO(1L, null, List.of(101L), "DELIVERY");

        Courier courier = new Courier();

        Parcel parcel = new Parcel();
        parcel.assign();

        assertThrows(IllegalArgumentException.class,
                () -> new TaskValidator().validateCreation(dto, courier, List.of(parcel)));
    }

    @Test
    void validateCreation_CourierAlreadyHasActiveTask_Throws() {
        CreateTaskDTO dto = new CreateTaskDTO(1L, null, List.of(101L), "DELIVERY");

        Courier courier = new Courier();
        courier.setHasActiveTask(true);

        Parcel parcel = new Parcel();

        assertThrows(IllegalArgumentException.class,
                () -> new TaskValidator().validateCreation(dto, courier, List.of(parcel)));
    }
}