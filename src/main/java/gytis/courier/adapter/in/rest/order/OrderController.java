package gytis.courier.adapter.in.rest.order;

import gytis.courier.adapter.in.pagination.PageQueryAssembler;
import gytis.courier.adapter.in.rest.order.dto.OrderAddressSectionUpdateRequest;
import gytis.courier.adapter.in.rest.order.dto.OrderSectionUpdateRequest;
import gytis.courier.adapter.in.rest.order.dto.ParcelSectionUpdateRequest;
import gytis.courier.adapter.in.rest.order.dto.PlaceOrderRequest;
import gytis.courier.adapter.in.rest.order.dto.request.OrderAdminSearchRequest;
import gytis.courier.adapter.in.rest.order.dto.request.OrderUserSearchRequest;
import gytis.courier.adapter.in.rest.order.pagination.OrderAdminPagingPolicy;
import gytis.courier.adapter.in.rest.order.pagination.OrderUserPagingPolicy;
import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.order.AdminOrderUpdateUseCase;
import gytis.courier.application.port.in.order.CancelOrderUseCase;
import gytis.courier.application.port.in.order.OrderQueryUseCase;
import gytis.courier.application.port.in.order.PlaceOrderUseCase;
import gytis.courier.application.query.filter.OrderQuery;
import gytis.courier.application.readmodel.order.AdminOrderListReadModel;
import gytis.courier.application.readmodel.order.OrderUserDetailReadModel;
import gytis.courier.application.readmodel.order.UserOrderListReadModel;
import gytis.courier.application.readmodel.order.OrderAdminDetailReadModel;
import gytis.courier.application.readmodel.order.OrderForTaskReadModel;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import gytis.courier.domain.task.TaskType;
import gytis.courier.domain.person.Person;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderQueryUseCase queryUseCase;
    private final PlaceOrderUseCase placeOrderUseCase;
    private final CancelOrderUseCase cancelUseCase;
    private final AdminOrderUpdateUseCase adminUseCase;
    private final OrderCommandMapper mapper;

    public OrderController(OrderQueryUseCase queryUseCase, PlaceOrderUseCase placeOrderUseCase, CancelOrderUseCase cancelUseCase, AdminOrderUpdateUseCase adminUseCase, OrderCommandMapper mapper) {
        this.queryUseCase = queryUseCase;
        this.placeOrderUseCase = placeOrderUseCase;
        this.cancelUseCase = cancelUseCase;
        this.adminUseCase = adminUseCase;
        this.mapper = mapper;
    }

    /** command */

    // user
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> addOrder(@RequestBody @Valid PlaceOrderRequest request, @AuthenticationPrincipal AuthenticatedPerson person) {
        var command = mapper.toPlaceOrderCommand(request, person.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(placeOrderUseCase.placeOrder(command));
    }

    @PostMapping("/cancelOrder/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long orderId, @AuthenticationPrincipal Person person) {
        cancelUseCase.cancel(orderId, person.getId());
        return ResponseEntity.ok(new ApiResponse("success", "Order was canceled successfully"));
    }

    // admin
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateOrderSection(@PathVariable Long id, @Valid @RequestBody OrderSectionUpdateRequest request) {
        var command = mapper.toOrderSectionCommand(request);
        adminUseCase.updateOrderSection(id, command);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/parcelSection")
    public ResponseEntity<ApiResponse> updateParcelSection(@PathVariable Long id, @RequestBody ParcelSectionUpdateRequest request) {
        var command = mapper.toParcelSectionCommand(request);
        adminUseCase.parcelSectionUpdate(id, command);
        return ResponseEntity.ok(new ApiResponse("success", "Parcel section successfully updated"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/addressSection")
    public ResponseEntity<ApiResponse> updateAddressSection(@PathVariable Long id, @RequestBody OrderAddressSectionUpdateRequest request) {
        var command = mapper.toAddressSectionCommand(request);
        adminUseCase.orderAddressSectionUpdate(id, command);
        return ResponseEntity.ok(new ApiResponse("success", "Order address successfully updated"));
    }

    /** query */

    //user
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public PageResult<UserOrderListReadModel> getUserOrders(OrderUserSearchRequest request, @AuthenticationPrincipal AuthenticatedPerson person) {
        PageQuery pageQuery = PageQueryAssembler.from(request.page(), request.size(), request.sortField(), request.direction(), OrderUserPagingPolicy.INSTANCE);
        return queryUseCase.getUserOrderList(pageQuery, person.id());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{id}")
    public OrderUserDetailReadModel getDetailedForUser(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedPerson person) {
        return queryUseCase.getUserOrderDetail(id, person.id());
    }

    //admin
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public PageResult<AdminOrderListReadModel> getAdminOrders(@ModelAttribute OrderAdminSearchRequest request) {
        OrderQuery orderQuery = new OrderQuery(request.status(), request.id());
        PageQuery pageQuery = PageQueryAssembler.from(request.page(), request.size(), request.sortField(), request.direction(), OrderAdminPagingPolicy.INSTANCE);
        return queryUseCase.getAdminOrderList(pageQuery, orderQuery);
    }

    @GetMapping("/getOrdersForTaskAssignment")
    @PreAuthorize("hasRole('ADMIN')")
    public PageResult<OrderForTaskReadModel> getAllByTaskType(
            Pageable pageable,
            @RequestParam TaskType taskType
    ) {
        System.out.println("yep " + pageable.getPageSize() + " " + taskType);
        PageQuery pageQuery = PageQueryAssembler.fromPageable(pageable, OrderAdminPagingPolicy.INSTANCE);
        return queryUseCase.getAllByTaskType(pageQuery, taskType);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public OrderAdminDetailReadModel getDetailed(@PathVariable Long id) {
        return queryUseCase.getOrderDetail(id);
    }
}