import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { ActivityLogService } from "../service/activity-log.service";
import { ActivityModel } from "../models/activity.model";
import { ErrorHandlerService } from "../service/error-handler.service";
import { DatePipe } from "@angular/common";
import { MatTableDataSource, MatTableModule } from "@angular/material/table";
import { MatSort, MatSortModule, Sort } from "@angular/material/sort";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {PaginatedResponse} from "../models/paginated-response.model";
import {PaginationComponent} from "../shared/pagination/pagination.component";
import {PageEventModel} from "../models/page-event.model";

  @Component({
    selector: 'app-activity-log',
    standalone: true,
    imports: [DatePipe, MatTableModule, MatSortModule, PaginationComponent],
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
    pageSize = 10;
    sortBy = "createdAt";
    role = "";
    keyword = "";


    displayedColumns: string[] = ['createdAt', 'role', 'userEmail', 'action', 'description'];
    dataSource = new  MatTableDataSource<ActivityModel>(this.logs.data);

    @ViewChild(MatSort) sort!: MatSort;

    constructor(
      private activityService: ActivityLogService,
      private errorHandler: ErrorHandlerService,
      private liveAnnouncer: LiveAnnouncer) {

    }

    ngAfterViewInit() {
      this.dataSource.sort = this.sort;
    }

    ngOnInit() {
      this.getAll();
    }

    getAll() {
      this.activityService.getAll(this.pageIndex, this.pageSize, this.sortBy, this.role, this.keyword).subscribe({
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

      this.getAll();
    }

  announceSortChange(sortState: Sort) {
    if (sortState.direction) {
      this.liveAnnouncer.announce(`sorted ${sortState.direction}ending`);
    } else {
      this.liveAnnouncer.announce('cleared sort');
    }
  }
}
