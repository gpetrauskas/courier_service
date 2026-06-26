import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EnumLabelPipe } from '../../../shared/pipes/enum-label.pipe';
import { ErrorHandlerService } from "../../../service/error-handler.service";
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminTaskService } from '../../../service/task/admin-task.service';
import { CourierService } from '../../../service/person/courier.service';
import { TaskStatus } from '../../../enums/task-status.enum';
import { AdminTaskDetailed } from '../../../models/task/admin-task-detailed.model';
import { Courier } from '../../../models/person/courier.model';
import { DeliveryTaskItem } from '../../../models/task/delivery-task-item.model';

@Component({
  selector: 'app-list-details',
  standalone: true,
  imports: [CommonModule, FormsModule, EnumLabelPipe],
  providers: [DatePipe],
  templateUrl: './list-details.component.html',
  styleUrl: './list-details.component.css'
})
export class ListDetailsComponent implements OnInit {
  task: AdminTaskDetailed | null = null;
  couriers: Courier[] = [];
  selectedCourier: number | null = null;
  tempDeliveryStatus: TaskStatus | null = null;
  taskStatuses: TaskStatus[] = Object.values(TaskStatus);
  isChangingCourier: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private courierService: CourierService,
    private taskService: AdminTaskService,
    private datePipe: DatePipe,
    private errorHandler: ErrorHandlerService
  ) {}

  ngOnInit() {
    const taskId = Number(this.route.snapshot.paramMap.get("id"));
      this.taskService.getDetailedTask(taskId).subscribe({
        next: (data) => {
          this.task = data;
          this.tempDeliveryStatus = data.deliveryStatus;
        },
        error: (err) => this.errorHandler.handleError(err)
      });
  }

  changeCourierToggle() {
    this.isChangingCourier = !this.isChangingCourier;
    if (this.isChangingCourier) {
      this.fetchAvailableCouriers();
    }
  }

  fetchAvailableCouriers() {
    if (this.couriers.length === 0) {
      this.courierService.availableCouriers().subscribe({
        next: (data: Courier[]) => {
          this.couriers = data;
          this.selectedCourier = null;
        },
        error: (err) => {
          this.errorHandler.showError(err);
          this.isChangingCourier = false;
        }
      });
    }
  }

  changeCourier() {
    if (!(this.selectedCourier && this.task)) return;
    this.errorHandler.handleRequest(this.taskService.changeCourier(this.task!.id, this.selectedCourier), "Courier changed successfully",
      () => {
      this.couriers = this.couriers.filter(c => c.id != this.selectedCourier);
      this.selectedCourier = null;
      }
    );
  }

  changeTaskStatus() {
    if (this.tempDeliveryStatus === TaskStatus.COMPLETED) {
      this.markCompleted();
    }
  }

  deleteItem(itemToDelete: DeliveryTaskItem) {
    this.errorHandler.handleRequest(this.taskService.removeItem(this.task!.id, itemToDelete.id), "Successfully deleted",
      () => {
      this.task?.items.filter(i => i.id !== itemToDelete.id);
      }
    );
  }

  get isCanceled() {
    return this.task?.deliveryStatus === TaskStatus.CANCELED;
  }

  get isInProgress() {
    return this.task?.deliveryStatus === TaskStatus.IN_PROGRESS;
  }

  get isAtCheckpoint() {
    return this.task?.deliveryStatus === TaskStatus.AT_CHECKPOINT;
  }

  get statusBadgeClass() {
    if (!this.task) return '';
    return this.task.deliveryStatus.toLowerCase();
  }

  get completedAtLabel() {
     if (!this.task) return '';
     return this.task.deliveryStatus === TaskStatus.CANCELED
     ? 'Canceled'
     : this.datePipe.transform(this.task.completedAt, 'short') ?? 'Pending';
  }

  markCompleted() {
    if (!this.task) return;
    this.errorHandler.handleRequest(this.taskService.complete(this.task.id), "Successfully marked as completed",
      () => this.router.navigate(['/admin-dashboard/delivery-control/all-lists'])
    );
  }
}
