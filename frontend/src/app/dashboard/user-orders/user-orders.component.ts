import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../service/order/order.service';
import { CommonModule } from '@angular/common';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ErrorHandlerService } from "../../service/error-handler.service";

@Component({
  selector: 'app-user-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, PaginationComponent],
  templateUrl: './user-orders.component.html',
  styleUrl: './user-orders.component.css'
})
export class UserOrdersComponent implements OnInit {
  orders: any[] = [];
  totalOrders: number = 0;
  totalPages: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;

  constructor(private orderService: OrderService, private router: Router, private errorHandler: ErrorHandlerService) {}

  ngOnInit(): void {
    this.fetchOrders(this.currentPage, this.pageSize);
  }

  fetchOrders(page: number, size: number): void {
    this.orderService.getMyOrders(page, size).subscribe({
      next: (response: any) => {
        this.orders = response.data;
        this.totalOrders = response.totalOrders;
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

  onPageChange(event: any) {
    this.currentPage = event;
    this.fetchOrders(this.currentPage, this.pageSize);
  }

  onPageSizeChange(event: any): void {
    let newSize = Number(event);
    this.pageSize = newSize;
    this.currentPage = 0;
    this.fetchOrders(this.currentPage, this.pageSize);
  }

}
