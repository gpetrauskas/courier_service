import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router'
import { OrderService } from '../../../service/order/order.service';
import { FilterComponent } from '../../../shared/filter/filter.component';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import { FormsModule } from '@angular/forms';
import { AdminOrderList } from "../../../models/order/admin-order-list.model";
import { PageEventModel } from "../../../models/page-event.model";
import { FilterEventModel } from "../../../models/filter/filter-event.model";


@Component({
  selector: 'app-all-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, FilterComponent, PaginationComponent],
  templateUrl: './all-orders.component.html',
  styleUrl: './all-orders.component.css'
})
export class AllOrdersComponent implements OnInit {
  orders: AdminOrderList[] = [];
  currentPage: number = 0;
  totalPages: number = 0;
  totalItems: number = 0;
  pageSize: number = 10;
  status: string = '';
  userId: number | null = null;

  constructor(private router: Router, private orderService: OrderService) {}

  ngOnInit() {
    this.loadOrders(this.currentPage, this.pageSize);
  }

  loadOrders(page: number, size: number) {
    const userIdToSend = this.userId !== null ? this.userId : undefined;

    this.orderService.getAllOrdersForAdmin(page, size, this.status, userIdToSend).subscribe({
      next: (data) => {
        this.orders = data.data;
        this.currentPage = data.currentPage;
        this.totalPages = data.totalPages;
        this.totalItems = data.totalItems;
      }
    });
  }

  viewOrder(order: any) {
    this.router.navigate(['/admin-dashboard/manage-order/order-details', order.id]);
  }

  onPageChange(event: PageEventModel) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadOrders(this.currentPage, this.pageSize);
  }

  onFilterChange(filterData: FilterEventModel) {
    if (filterData.kind !== 'order') return;
    this.status = filterData.orderStatus;
    this.userId = filterData.userId;
    this.currentPage = 0;
    this.loadOrders(this.currentPage, this.pageSize);
  }
}
