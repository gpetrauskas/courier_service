import {Component, OnInit} from '@angular/core';
import {AdminTaskService} from '../../../service/task/admin-task.service';
import {CommonModule} from '@angular/common';
import {Router, RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {FilterComponent} from '../../../shared/filter/filter.component';
import {PaginationComponent} from '../../../shared/pagination/pagination.component';
import {AdminTaskSummary} from '../../../models/task/admin-task-summary.model';
import {PaginatedResponse} from '../../../models/paginated-response.model';
import {ErrorHandlerService} from "../../../service/error-handler.service";
import {PageEventModel} from "../../../models/page-event.model";
import {FilterEventModel} from "../../../models/filter/filter-event.model";
import {AdminTaskCardModel} from "../../../models/task/admin-task-card.model";
import {WebsocketService} from "../../../service/websocket.service";
import {ActivityModel} from "../../../models/activity.model";
import {DateLabelPipe} from "../../../shared/pipes/date-label.pipe";
import {PeriodEnum} from "../../../enums/period.enum";
import {PageStateModel} from "../../../models/page-state.model";
import {ActivityLogComponent} from "../../../activity-log/activity-log.component";
import {CreateNewListComponent} from "../create-new-list/create-new-list.component";
import {MatDialog} from "@angular/material/dialog";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {DurationLabelPipe} from "../../../shared/pipes/duration-label.pipe";
import {InfoCardsComponent} from "../../../info-cards/info-cards.component";

@Component({
  selector: 'app-all-lists',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, FilterComponent, PaginationComponent, DateLabelPipe, ActivityLogComponent,
  CreateNewListComponent, MatButtonToggleGroup, MatButtonToggle, DurationLabelPipe, InfoCardsComponent],
  templateUrl: './all-lists.component.html',
  styleUrl: './all-lists.component.css'
})
export class AllListsComponent implements OnInit {
  infoCards = [
    { label: 'Picked Up', value: 10, color: 'picked' },
    { label: 'Delivered', value: 20, color: 'delivered' },
    { label: 'Picking Up', value: 5, color: 'picking' },
    { label: 'Delivering', value: 7, color: 'delivering' },
    { label: 'Returning', value: 3, color: 'returning' },
    { label: 'Awaiting confirmation', value: 2, color: 'returned' }
  ];
  allLists: AdminTaskSummary[] = [];
  activityList: ActivityModel[] = [];
  cards: AdminTaskCardModel | null = null;
  stats:any = [];
  taskPageState: PageStateModel = {currentPage: 0, totalPages: 0, totalItems: 0,  pageSize: 5};
  sortBy: string = 'createdAt';
  sortDirection: string = 'DESC';
  inputPageSize: number = this.taskPageState.pageSize;
  tType: string = '';
  deliveryStatus: string = '';
  courierId: number | null = null;
  taskListId: number | null = null;
  taskRefreshAvailable = false;
  statsPeriod: PeriodEnum = PeriodEnum.DAY;
  PeriodEnum = PeriodEnum;

  constructor(
    private taskService: AdminTaskService,
    private router: Router,
    private errorHandler: ErrorHandlerService,
    private ws: WebsocketService,
    private matDialog: MatDialog
  ) {}

  ngOnInit() {
    this.loadAllLists(this.taskPageState.currentPage, this.taskPageState.pageSize);
    this.loadCards();
    this.loadStats();
    this.ws.watchTaskCards().subscribe(() => {
      this.taskRefreshAvailable = true;
      this.loadCards();
      this.loadStats();
    });
  }

  refresh() {
    this.loadAllLists(this.taskPageState.currentPage, this.taskPageState.pageSize);
  }

  loadStats() {
    this.taskService.getTaskStats(this.statsPeriod).subscribe({
      next: (data) => {
        this.stats = data;
      }
    });
  }

  openCreate() {
    this.matDialog
      .open(CreateNewListComponent, {width: "90%", maxWidth: "90vw", height: "auto", maxHeight: "90vh"})
      .componentInstance.createdAny.subscribe(() => {
        this.loadStats();
        this.loadCards();
        this.refresh();
    });
  }

  private loadAllLists(page: number, size: number) {
    this.taskRefreshAvailable = false;
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
        this.taskPageState = {
          currentPage: data.currentPage,
          totalPages: data.totalPages,
          totalItems: data.totalItems,
          pageSize: this.taskPageState.pageSize

        }
      },
      error: (err) => {
        this.errorHandler.handleError(err);
        this.taskRefreshAvailable = true;
      }
    });
  }

  private loadCards() {
    this.taskService.getCards().subscribe({
      next: (data: AdminTaskCardModel) => {
        this.cards = data;
        console.log(this.cards)
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

  onFilterChange(event: FilterEventModel) {
    if (event.kind !== 'task') return;
    this.tType = event.taskType;
    this.deliveryStatus = event.deliveryStatus;
    this.taskListId = event.taskId;
    this.courierId = event.courierId;
    this.taskPageState.currentPage = 0;
    this.loadAllLists(this.taskPageState.currentPage, this.taskPageState.pageSize);
  }

  onPageChange(event: PageEventModel) {
    this.taskPageState.currentPage = event.pageIndex;
    this.taskPageState.pageSize = event.pageSize;
    this.loadAllLists(this.taskPageState.currentPage, this.taskPageState.pageSize);
  }

  onStatsPeriodChange(event: PeriodEnum) {
    if (event === this.statsPeriod) {
      return;
    }

    this.statsPeriod = event;
    this.loadStats();
  }
}
