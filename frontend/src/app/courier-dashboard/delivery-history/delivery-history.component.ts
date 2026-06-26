import { Component, OnInit } from '@angular/core';
import { CourierTaskService } from '../../service/task/courier-task.service';
import { CourierTask } from '../../models/task/courier-task.model';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { ErrorHandlerService } from "../../service/error-handler.service";

@Component({
  selector: 'app-delivery-history',
  standalone: true,
  imports: [ PaginationComponent, MatTableModule, CommonModule, MatIconModule, MatButtonModule ],
  templateUrl: './delivery-history.component.html',
  styleUrl: './delivery-history.component.css'
})
export class DeliveryHistoryComponent implements OnInit {
  tasksList: CourierTask[] = [];
  page: number = 0;
  size: number = 10;
  sortBy: string = 'createdAt';
  sortDirection: string = 'DESC';
  currentPage: number = 0;
  totalPages: number = 0;
  totalItems: number = 0;
  displayedHistoryTasksListColumn: string[] = ['taskId', 'taskType', 'deliveryStatus', 'completedAt'];
  columnsToDisplayWithExpand = [...this.displayedHistoryTasksListColumn, 'expand'];
  expandedTask: CourierTask | null = null;


  constructor(private courierService: CourierTaskService, private errorHandler: ErrorHandlerService) {}


  ngOnInit() {
    this.fetchHistoryTasks(this.page, this.size, this.sortBy, this.sortDirection);
  }

  private fetchHistoryTasks(page: number, size: number, sortBy: string, direction: string) {
    const params = { page: page, size: size, sortBy: sortBy, direction: direction }
    this.courierService.history(params).subscribe({
      next: data => {
        this.tasksList = data.data;
        this.currentPage = data.currentPage;
        this.totalItems = data.totalItems;
        this.totalPages = data.totalPages;
      },
      error: (err) => this.errorHandler.handleError(err)
    });
  }

  onPageChange(page: number) {
      this.page = page;
      this.fetchHistoryTasks(this.page, this.size, this.sortBy, this.sortDirection);
  }

  onPageSizeChange(size: number) {
    this.size = size;
    this.currentPage = 0;
    this.fetchHistoryTasks(this.page, this.size, this.sortBy, this.sortDirection);
  }

  isExpanded(task: CourierTask) {
    return this.expandedTask === task;
  }

  toggle(task: CourierTask) {
    this.expandedTask = this.isExpanded(task) ? null : task;
  }
}
