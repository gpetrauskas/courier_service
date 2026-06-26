import { Component, OnInit } from '@angular/core';
import { AdminTaskService } from '../../../service/task/admin-task.service';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FilterComponent } from '../../../shared/filter/filter.component';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import { AdminTaskSummary } from '../../../models/task/admin-task-summary.model';
import { PaginatedResponse } from '../../../models/paginated-response.model';
import { ErrorHandlerService } from "../../../service/error-handler.service";

@Component({
  selector: 'app-all-lists',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, FilterComponent, PaginationComponent],
  templateUrl: './all-lists.component.html',
  styleUrl: './all-lists.component.css'
})
export class AllListsComponent implements OnInit {
  allLists: AdminTaskSummary[] = [];
  pageSize: number = 10;
  currentPage: number = 0;
  totalPages: number = 0;
  totalItems: number = 0;
  sortBy: string = 'createdAt';
  sortDirection: string = 'DESC';
  inputPageSize: number = this.pageSize;
  tType: string = '';
  deliveryStatus: string = '';
  courierId: number | undefined = undefined;
  taskListId: number | undefined = undefined;

  constructor(
    private taskService: AdminTaskService,
    private router: Router,
    private errorHandler: ErrorHandlerService
  ) {}

  ngOnInit() {
    this.loadAllLists(this.currentPage, this.pageSize);
  }

  private loadAllLists(page: number, size: number) {
    this.taskService.getAllDeliveryTaskLists({
      page,
      size,
      courierId: this.courierId || undefined,
      taskListId: this.taskListId || undefined,
      taskType: this.tType || undefined,
      deliveryStatus: this.deliveryStatus || undefined,
      sortBy: this.sortBy,
      direction: this.sortDirection
    }).subscribe({
      next: (data: PaginatedResponse<AdminTaskSummary>) => {
      this.allLists = data.data;
      this.currentPage = data.currentPage;
      this.totalItems = data.totalItems;
      this.totalPages = data.totalPages;
      },
      error: (err) => this.errorHandler.handleError(err)
    });
  }

  viewDetails(taskId: number) {
    this.router.navigate(['/admin-dashboard/delivery-control/list-details', taskId]);
  }

  cancelTask(taskId: number) {
    if (this.allLists.some(task => task.id === taskId)) {
      this.errorHandler.handleRequest(this.taskService.cancel(taskId), "Successfully canceled",
        () => {
        const taskToRemoveIndex = this.allLists.findIndex(task => task.id === taskId);
        this.allLists.splice(taskToRemoveIndex, 1);
        }
      );
    }
  }

  onFilterChange(event: any) {
   this.tType = event.taskType;
   this.deliveryStatus = event.deliveryStatus;
   this.taskListId = event.taskId;
   this.courierId = event.courierId;
   this.currentPage = 0;
   this.loadAllLists(this.currentPage, this.pageSize);
  }

  onPageChange(event: any) {
    this.currentPage = event;
    this.loadAllLists(this.currentPage, this.pageSize);
  }

  onPageSizeChange(event: number) {
    const parsedSize = Number(event);
    this.pageSize = parsedSize;
    this.currentPage = 0;
    this.loadAllLists(this.currentPage, this.pageSize);
  }
}
