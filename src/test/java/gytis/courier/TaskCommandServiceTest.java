/*
package com.example.courier;

import person.domain.gytis.courier.Admin;
import person.domain.gytis.courier.Courier;
import order.domain.gytis.courier.ParcelStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.CreateTaskDTO;
import com.example.courier.dto.mapper.DeliveryTaskMapper;
import com.example.courier.repository.TaskRepository;
import com.example.courier.service.order.query.OrderQueryService;
import com.example.courier.service.person.query.PersonLookupService;
import com.example.courier.adapter.in.security.CurrentPersonUseCase;
import com.example.courier.service.task.TaskItemService;
import com.example.courier.service.task.TaskCommandService;
import com.example.courier.service.task.TaskQueryService;
import com.example.courier.specification.task.TaskSpecificationBuilder;
import com.example.courier.testutil.OrderTestBuilder;
import com.example.courier.testutil.ParcelTestBuilder;
import com.example.courier.testutil.TaskItemTestBuilder;
import com.example.courier.validation.task.TaskValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskCommandServiceTest {

    @Mock private PersonLookupService personLookupService;
    @Mock private final TaskValidator taskValidator = new TaskValidator();
    @Mock private TaskRepository taskRepository;
    @Mock private TaskSpecificationBuilder specificationBuilder;
    @Mock private TaskItemService taskItemService;
    @Mock private DeliveryTaskMapper deliveryTaskMapper;
    @Mock private CurrentPersonUseCase currentPersonService;
    @Mock private OrderQueryService orderQueryService;
    @Mock private TaskQueryService taskQueryService;

    @InjectMocks private TaskCommandService taskCommandService;


    private void mockCommonServices(Courier courier, Admin admin, Order order, CreateTaskDTO dto, TaskItem item) {
        when(personLookupService.fetchPersonByIdAndType(dto.courierId(), Courier.class)).thenReturn(courier);
        when(currentPersonService.getCurrentPersonAs(Admin.class)).thenReturn(admin);
        when(orderQueryService.getAllOrdersWithParcelByParcelIds(dto.parcelsIds())).thenReturn(List.of(order));
        when(taskItemService.createTaskItems(anyList(), any(Task.class))).thenReturn(List.of(item));
    }


    @Test
    void createNewDeliveryTask_pickupTask_Success() {
        CreateTaskDTO dto = new CreateTaskDTO(1L, 2L, List.of(99L), "PICKUP");
        Courier courier = new Courier();
        Admin admin = new Admin();

        Parcel parcel = ParcelTestBuilder.parcelTestBuilder()
                .withStatus(ParcelStatus.PICKING_UP)
                .build();

        Order order = OrderTestBuilder.orderTestBuilder()
                .withParcel(parcel)
                .build();

        TaskItem item = TaskItemTestBuilder.taskItemTestBuilder()
                .withOrder(order)
                .build();

        mockCommonServices(courier, admin, order, dto, item);

        taskCommandService.initiateNewTask(dto);

        assertTrue(courier.hasActiveTask());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createNewDeliveryTask_deliveryTask_SuccessWithParcelStatusTransition() {
        CreateTaskDTO dto = new CreateTaskDTO(1L, 2L, List.of(99L), "DELIVERY");
        Courier courier = new Courier();
        Admin admin = new Admin();

        Parcel parcel = ParcelTestBuilder.parcelTestBuilder()
                .withStatus(ParcelStatus.PICKED_UP)
                .build();
        Order order = new OrderTestBuilder()
                .withParcel(parcel)
                .build();
        TaskItem item = new TaskItemTestBuilder()
                .withOrder(order)
                .withTask(Task.create("DELIVERY", courier, admin))
                .build();

        mockCommonServices(courier, admin, order, dto, item);

        taskCommandService.initiateNewTask(dto);

        assertTrue(courier.hasActiveTask());
        assertEquals(ParcelStatus.DELIVERING, parcel.getStatus());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getAllTaskLists_AdminView_ReturnsPaginatedResults() {

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
        courier.activateTask();

        Parcel parcel = new Parcel();

        assertThrows(IllegalArgumentException.class,
                () -> new TaskValidator().validateCreation(dto, courier, List.of(parcel)));
    }
}*/
