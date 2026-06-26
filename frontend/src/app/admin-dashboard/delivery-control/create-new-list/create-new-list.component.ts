import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ParcelService } from '../../../service/order/parcel.service';
import { OrderService } from '../../../service/order/order.service';
import { Router } from '@angular/router';
import { Courier } from "../../../models/person/courier.model";
import { CourierService } from "../../../service/person/courier.service";
import { TaskType } from "../../../enums/task-type.enum";
import { AdminTaskService } from "../../../service/task/admin-task.service";
import { CreateTask } from "../../../models/task/create-task.model";
import { AdminOrderTask } from "../../../models/order/admin-order-task.model";
import { ErrorHandlerService } from "../../../service/error-handler.service";

@Component({
  selector: 'app-create-new-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-new-list.component.html',
  styleUrl: './create-new-list.component.css'
})
export class CreateNewListComponent implements OnInit {
  isTaskTypeSelected: boolean = false;
  selectedTaskType: TaskType | null = null;
  taskType = TaskType;
  currentStep: number = 1;
  currentPage: number = 0;
  totalPages: number = 0;
  pageSize: number = 10;
  inputPageSize: number = this.pageSize;
  selectedCourierId: number | null = null;
  items: AdminOrderTask[] = [];
  couriers: Courier[] = [];
  selectedItems: AdminOrderTask[] = [];
  itemsToPickupCount: number = 0;
  itemsToDeliverCount: number = 0;
  itemsFailedCount: number = 0;

  constructor(
    private router: Router,
    private taskService: AdminTaskService,
    private orderService: OrderService,
    private courierService: CourierService,
    private parcelQueryService: ParcelService,
    private errorHandler: ErrorHandlerService
  ) {}

  ngOnInit() {
    this.loadItemsCount();
  }

  private loadItemsCount() {
    this.parcelQueryService.availableParcelsCount().subscribe({
      next: (data) => {
        console.log(data);
        this.itemsToPickupCount = data['pickingUpCount'];
        this.itemsToDeliverCount = data['deliveringCount'];
        this.itemsFailedCount = data['failedCount'];
      },
      error: (err) => this.errorHandler.handleError(err)
    });
  }

  wasFailed(item: AdminOrderTask): boolean {
    return item.failuresCount > 0;
  }

  private loadItemsByTaskType() {
    this.orderService.allTaskOrdersByTaskType(this.currentPage, this.pageSize, this.selectedTaskType!).subscribe({
      next: (data) => {
        this.totalPages = data.totalPages;
        this.currentPage = data.currentPage;
        this.items = data.data;
      },
      error: (err) => this.errorHandler.handleError(err)
    });
  }

  onTaskTypeChange(taskType: TaskType) {
    this.selectedTaskType = taskType;
    this.isTaskTypeSelected = !this.isTaskTypeSelected;
    this.currentPage = 0;
    this.currentStep = 2;

    this.loadItemsByTaskType();
  }

  onRowClick(item: AdminOrderTask) {
    if (this.selectedItems.some(i => i.parcelId === item.parcelId)) {
      this.selectedItems = this.selectedItems.filter(i => i.parcelId != item.parcelId);
    } else {
      this.selectedItems.push(item);
    }
  }

  isSelected(id: number): boolean {
    return this.selectedItems.some(i => i.parcelId === id);
  }

  back(): void {
    if (this.currentStep === 3) {
        this.currentStep = 2;
    } else if (this.currentStep === 2) {
      this.isTaskTypeSelected = !this.isTaskTypeSelected;
      this.selectedItems = [];
      this.currentStep = 1;
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
        this.currentPage -= 1;
        this.loadItemsByTaskType();
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage += 1;
      this.loadItemsByTaskType();
    }
  }

  onPageSizeChange() {
    const parsedSize = Number(this.inputPageSize);

    if (!isNaN(parsedSize) && parsedSize > 0 && parsedSize <= (this.itemsToPickupCount || this.itemsToDeliverCount)) {
      this.pageSize = parsedSize;
      this.currentPage = 0;
      this.loadItemsByTaskType();
    } else {
      this.inputPageSize = this.pageSize;
    }
  }

  validateKeyPress(event: KeyboardEvent): void {
    const charCode = event.key;
    if (!/^[0-9]+$/.test(charCode)) {
      event.preventDefault();
    }
  }

  assignCourier() {
    if (!this.selectedCourierId) {
      alert("Please select a courier before proceeding");
      return;
    }

    const task: CreateTask = {
      courierId: this.selectedCourierId,
      parcelIds: this.selectedItems.map((i) => i.parcelId),
      type: this.selectedTaskType!
    };

    this.errorHandler.handleRequest(this.taskService.create(task), "Successfully created task.",
      () => this.router.navigate(['/admin-dashboard/delivery-control'])
    );
  }

  goToReview() {
    this.currentStep = 3;
    this.availableCouriers();
  }

  private availableCouriers() {
    this.courierService.availableCouriers().subscribe({
      next: (data) => {
        this.couriers = data;
        console.log(data)
      },
      error: (err) => this.errorHandler.handleError(err)
    });
  }
}
