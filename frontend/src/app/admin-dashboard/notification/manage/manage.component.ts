import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../service/notification.service';
import { FilterComponent } from '../../../shared/filter/filter.component';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import { Notification } from '../../../models/person-notification/notification.model';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { ErrorHandlerService } from "../../../service/error-handler.service";

@Component({
  selector: 'app-manage',
  standalone: true,
  imports: [FilterComponent, PaginationComponent, MatCardModule, CommonModule, MatIconModule],
  templateUrl: './manage.component.html',
  styleUrl: './manage.component.css'
})
export class ManageComponent implements OnInit {
  notifications: Notification[] = [];
  page: number = 0;
  size: number = 10;
  totalPages: number = 0;
  totalItems: number = 0;
  sortBy: string = "createdAt";
  sortDirection: string = "DESC";
  selectedId: number | null = null;
  keyword: string = '';


  constructor(private notificationService: NotificationService, private cdr: ChangeDetectorRef, private errorHandler: ErrorHandlerService) {
  }

  ngOnInit() {
      this.fetchNotifications(this.size, this.page);
  }

  selectNotification(id: number) {
    if (id == this.selectedId) {
      this.selectedId = null;
    } else {
      this.selectedId = id;
    }
  }

  delete(notificationId: number, event: Event) {
    event.stopPropagation();
    if (!notificationId) {
        return;
    }

    this.errorHandler.handleRequest(this.notificationService.adminDelete(notificationId), "Successfully deleted", () => {
      this.notifications = [ ...this.notifications.filter(n => n.id !== notificationId)];
      this.cdr.detectChanges();
      }
    );
  }

   fetchNotifications(size: number, page: number) {
    this.notificationService.getAllForAdmin(size, page, this.keyword, this.sortBy, this.sortDirection).subscribe({
      next: (data) => {
        this.notifications = data.data;
        this.page = data.currentPage;
        this.totalItems = data.totalItems;
        this.totalPages = data.totalPages;
        this.cdr.detectChanges();
      },
      error: (err) =>  {
        this.errorHandler.showError(err);
      }
    });
  }

  onPageChange(page: number) {
    this.page = page;
    this.fetchNotifications(this.size, this.page);
  }

  onPageSizeChange(size: number) {
    this.size = size;
    this.page = 0;
    this.fetchNotifications(this.size, this.page);
  }

  onFilterChange(filter: any) {
    this.keyword = filter.keyword ?? '';
    this.page = 0;
    this.fetchNotifications(this.size, this.page);
  }

}
