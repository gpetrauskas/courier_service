import {Component, OnInit} from '@angular/core';
import {OrderService} from '../../service/order/order.service';
import {CommonModule} from '@angular/common';
import {PaginationComponent} from '../../shared/pagination/pagination.component';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {ErrorHandlerService} from "../../service/error-handler.service";
import {PageEventModel} from "../../models/page-event.model";
import {UserOrderList} from "../../models/order/user-order-list.model";
import {PaginatedResponse} from "../../models/paginated-response.model";
import {FilterComponent} from "../../shared/filter/filter.component";
import {OrderStatus} from "../../enums/order-status.enum";
import {FilterEventModel} from "../../models/filter/filter-event.model";
import {OrderFilterModel} from "../../models/filter/order-filter.model";

@Component({
  selector: 'app-user-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, PaginationComponent, FilterComponent],
  templateUrl: './user-orders.component.html',
  styleUrl: './user-orders.component.css'
})
export class UserOrdersComponent implements OnInit {
  statusFilter: OrderStatus = OrderStatus.All;
  orders: UserOrderList[] = [];
  totalOrders: number = 0;
  totalPages: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;

  constructor(private orderService: OrderService, private router: Router, private errorHandler: ErrorHandlerService) {}

  ngOnInit(): void {
    this.fetchOrders(this.currentPage, this.pageSize);
  }

  fetchOrders(page: number, size: number, statusFilter?: OrderStatus): void {
    this.orderService.getMyOrders(page, size, statusFilter).subscribe({
      next: (response: PaginatedResponse<UserOrderList>) => {
        this.orders = response.data;
        this.totalOrders = response.totalItems
        this.totalPages = response.totalPages;
        this.currentPage = response.currentPage;
      },
      error: (error) => {
        this.errorHandler.handleError(error)
      }
    });
  }

  viewOrderDetails(orderId: number): void {
    const selectedOrder = this.orders.find(o => o.id === orderId);
    if (selectedOrder) {
      this.router.navigate(['/dashboard/user-orders', orderId], {
        state: { order: selectedOrder }
      });
    }
  }

  onPageChange(event: PageEventModel) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.fetchOrders(this.currentPage, this.pageSize);
  }

  onFilterChange(event: FilterEventModel) {
    event = event as OrderFilterModel;
    this.statusFilter = event.orderStatus;

    this.fetchOrders(this.currentPage, this.pageSize, this.statusFilter);
  }

}
