import {AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import { ActivityLogService } from "../service/activity-log.service";
import { ActivityModel } from "../models/activity.model";
import { ErrorHandlerService } from "../service/error-handler.service";
import { DatePipe } from "@angular/common";
import { MatTableDataSource, MatTableModule } from "@angular/material/table";
import { MatSort, MatSortModule, Sort } from "@angular/material/sort";
import { LiveAnnouncer } from "@angular/cdk/a11y";
import { PaginatedResponse } from "../models/paginated-response.model";
import { PaginationComponent } from "../shared/pagination/pagination.component";
import { PageEventModel } from "../models/page-event.model";
import { Observable } from "rxjs";
import { DateLabelPipe } from "../shared/pipes/date-label.pipe";
import { WebsocketService } from "../service/websocket.service";

  @Component({
    selector: 'app-activity-log',
    standalone: true,
    imports: [DatePipe, MatTableModule, MatSortModule, PaginationComponent, DateLabelPipe],
    templateUrl: './activity-log.component.html',
    styleUrl: './activity-log.component.css'
  })
  export class ActivityLogComponent implements AfterViewInit {
    logs: PaginatedResponse<ActivityModel> = {
      data: [],
      currentPage : 0,
      totalItems: 0,
      totalPages: 0
    };

    totalPages = 0;
    totalItems = 0;
    pageIndex = 0;
    sortBy = "createdAt";
    role = "";
    keyword = "";

    @Input() pageSize = 10;
    @Input() compact: boolean = false;
    @Input() scope: "all" | "task" = "all";


    displayedColumns: string[] = ['createdAt', 'role', 'userEmail', 'action', 'description'];
    dataSource = new  MatTableDataSource<ActivityModel>(this.logs.data);

    @ViewChild(MatSort) sort!: MatSort;

    constructor(
      private activityService: ActivityLogService,
      private errorHandler: ErrorHandlerService,
      private liveAnnouncer: LiveAnnouncer,
      private wsService: WebsocketService
      ) {

    }

    ngAfterViewInit() {
      this.dataSource.sort = this.sort;
    }

    ngOnInit() {
      this.getActivity();
      this.wsService.watchActivity("task").subscribe(() => this.getActivity());
    }

    getActivity() {
      let request: Observable<PaginatedResponse<ActivityModel>>;
      if (this.scope == "all") {
        request = this.activityService.getAll(this.pageIndex, this.pageSize, this.sortBy, this.role, this.keyword);
      } else {
        request = this.activityService.getForTasks(this.pageSize, this.pageIndex);
      }

      request.subscribe({
        next: data => {
          this.logs = data;
          this.totalPages = data.totalPages;
          this.pageIndex = data.currentPage;
          this.totalItems = data.totalItems;
          this.dataSource.data = data.data;
          this.dataSource.sort = this.sort;
        },
        error: (err) => this.errorHandler.showError(err)
      });
    }

    onPageChange(e: PageEventModel) {
      this.pageIndex = e.pageIndex;
      this.pageSize = e.pageSize;

      this.getActivity();
    }

  announceSortChange(sortState: Sort) {
    if (sortState.direction) {
      this.liveAnnouncer.announce(`sorted ${sortState.direction}ending`);
    } else {
      this.liveAnnouncer.announce('cleared sort');
    }
  }
}
