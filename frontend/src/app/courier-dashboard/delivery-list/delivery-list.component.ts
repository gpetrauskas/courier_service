import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatTableModule} from '@angular/material/table';
import { FormsModule } from '@angular/forms';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { CourierTaskItem } from '../../models/task/courier-task-item.model';
import { CourierTask } from '../../models/task/courier-task.model';
import { DeliveryTaskItemStatus, StatusesByTaskType } from '../../models/delivery-task-item-status.model';
import { PaginatedResponse } from "../../models/paginated-response.model";
import { CourierTaskService } from "../../service/task/courier-task.service";
import { MatInputModule } from "@angular/material/input";
import { ErrorHandlerService } from "../../service/error-handler.service";

@Component({
  selector: 'app-delivery-list',
  standalone: true,
  imports: [RouterModule, CommonModule, MatTableModule, FormsModule,
    MatPaginatorModule, MatCardModule, MatExpansionModule, MatTabsModule,
    MatFormFieldModule, MatSelectModule, MatButtonModule, MatInputModule],
  templateUrl: './delivery-list.component.html',
  styleUrl: './delivery-list.component.css'
})
export class DeliveryListComponent implements OnInit {
  deliveryTaskList: CourierTask[] = [];
  selectedTask: CourierTask | null = null;
  displayedTaskListColumn: string[] = ['taskId', 'taskType', 'createdAt', 'checkIn'];
  view: string = 'taskList';
  filterStatus: string = '';
  newNote: string = '';
  addingNoteForItemId: number | null = null;
  availableStatuses: DeliveryTaskItemStatus[] = [];
  paginatedItems: CourierTaskItem[] = [];

  itemPageIndex: number = 0;
  itemPageSize: number = 5;

  pageSize = 1;
  pageIndex = 0;
  pageSizeOptions = [5, 10, 20];

  constructor(
    private taskService: CourierTaskService, private errorHandler: ErrorHandlerService
  ) {}

  ngOnInit() {
    this.loadDeliveryList();
  }

  private loadDeliveryList() {
    this.taskService.assigned().subscribe({
      next: (data: PaginatedResponse<CourierTask>) => {
        this.deliveryTaskList = data.data;
        console.log(this.deliveryTaskList);
      },
      error: (err) => this.errorHandler.handleError(err)
    });
  }

  checkIn(taskId: number): void {
    this.errorHandler.handleRequest(this.taskService.checkIn(taskId), "Checked in successfully");
  }

  isReturningToStation(task: CourierTask): boolean {
      return task.deliveryStatus === 'RETURNING_TO_STATION';
  }

  showTaskDetails(taskId: number) {
    this.view = 'taskDetails';
    const foundTask = this.deliveryTaskList.find((t: CourierTask) => t.taskId === taskId);
    console.log('founded ' + foundTask?.taskId);

    if (!foundTask) return;

    this.taskService.items(taskId).subscribe({
      next: (data) => {
        this.selectedTask = foundTask;
        this.selectedTask.items = data.items;
        this.updatePaginatedItems();
        this.updateAvailableStatuses(data.taskType);
      }
    });
  }

  toggleAddNote(taskId?: number) {
    this.newNote = '';
    taskId
      ? this.addingNoteForItemId = taskId
      : this.addingNoteForItemId = null;
  }

  showTaskList() {
    this.selectedTask = null;
    this.view = 'taskList';
  }

  onPageChange(event: PageEvent) {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
  }

  onItemPageChange(event: PageEvent) {
    this.itemPageSize = event.pageSize;
    this.itemPageIndex = event.pageIndex;
    this.updatePaginatedItems();
  }

  private updatePaginatedItems() {
    const start = this.itemPageIndex * this.itemPageSize;
    const end = start + this.itemPageSize;
    this.paginatedItems = this.selectedTask?.items.slice(start, end) || [];
  }

  addNewNote(item: CourierTaskItem) {
    if (this.selectedTask) {
      this.errorHandler.handleRequest(this.taskService.addNote(this.selectedTask.taskId, item.id, this.newNote), "Added successfully",
        () => this.toggleAddNote()
      );
    }
  }

  updateAvailableStatuses(taskType: string) {
    this.availableStatuses = StatusesByTaskType[taskType];
  }

  updateItemStatus(item: CourierTaskItem) {
    if (!this.selectedTask) {
      return;
    }

    if (item.tempStatus === item.status || !item.tempStatus) {
      return;
    }

    this.errorHandler.handleRequest(this.taskService.updateItemStatus(this.selectedTask.taskId, item.id, item.tempStatus as DeliveryTaskItemStatus), "Successfully updated",
      () => item.status = item.tempStatus!);
  }

  isStatusDisabled(status: string): boolean {
    if (this.selectedTask?.taskType === "PICKING_UP") {
      return status === 'PICKED_UP' || status === 'CANCELED' || status === 'DELIVERED';
    } else if (this.selectedTask?.taskType === "DELIVERY") {
      return status === 'CANCELED' || status === 'DELIVERED';
    }
    return false;
  }
}
