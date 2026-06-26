import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog.component';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { PaginationComponent } from '../pagination/pagination.component';
import { NotificationService } from '../../service/notification.service';
import { Notification } from '../../models/person-notification/notification.model';
import { FilterComponent } from '../filter/filter.component';
import { ErrorHandlerService } from "../../service/error-handler.service";

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [
    CommonModule,
    PaginationComponent,
    MatCardModule,
    MatExpansionModule,
    MatIconModule,
    ConfirmationDialogComponent,
    FilterComponent,
  ],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css'
})
export class NotificationsComponent implements OnInit {
  status: string = '';
  selectedId: number | null = null;
  notifications: Notification[] = [];
  currentPage = 0;
  pageSize = 10;
  totalItems = 0;
  totalPages = 0;
  notificationsLoaded: boolean = false;
  hasUnread: boolean = false;

  showConfirmDialog = false;
  dialogConfig = {
    message: '',
    action: () => {}
  };

  constructor(
    private notificationService: NotificationService,
    private router: Router,
    private route: ActivatedRoute,
    private errorHandler: ErrorHandlerService
  ) {}

  ngOnInit() {
    this.notificationService.notifications$.subscribe(response => {
      this.notifications = response.page.data;
      this.totalPages = response.page.totalPages;
      this.totalItems = response.page.totalItems;
      this.currentPage = response.page.currentPage;
      this.hasUnread = !!response.unreadCount;

      if (this.selectedId && !this.notifications.some(n => n.id === this.selectedId)) {
        this.getPageContainingId(this.selectedId);
        this.selectedId = null;
      }
    });

    this.route.queryParams.subscribe(params => {
      this.selectedId = params['highlight'] ? +params['highlight'] : null;

      if (!this.notificationsLoaded) {
        this.notificationsLoaded = true;
        this.loadNotifications();
      } else if (this.selectedId && !this.notifications.some(n => n.id === this.selectedId)) {
        this.getPageContainingId(this.selectedId);
      }
    });
  }

  private getPageContainingId(selectedId: number) {
    return this.notificationService.getPageContainingId(selectedId, this.pageSize).subscribe();
  }

  private loadNotifications() {
    this.notificationService.loadNotificationsPage(this.pageSize, this.currentPage).subscribe();
  }

  onNotificationFilterChange(event: { status: string }): void {
    this.status = event.status || '';
    console.log("test if changed ", this.status);
  }

  hasUnreadNotifications(): boolean {
      return this.notifications.some(n => !n.isRead);
  }

  markAsRead(notificationId?: number) {
    notificationId
      ? this.notificationService.markAsRead(notificationId).subscribe()
      : this.notificationService.markAllAsRead();
  }

  deleteNotification(id?: number) {
    this.errorHandler.handleRequest(this.notificationService.delete(id), "Successfully deleted");
  }

  openConfirmDialog(notificationId?: number) {
    this.dialogConfig = {
      message: (
        notificationId
          ? `this notification: "${this.notifications.find(n => n.id === notificationId)?.title}"`
          : `all "${this.notifications.length} notifications"`
      ),
      action: () => this.deleteNotification(notificationId)
    }

    this.showConfirmDialog = true;
  }

  onDeleteAllConfirmed() {
    this.dialogConfig.action();
    this.showConfirmDialog = false;
  }

  onDeleteAllCanceled() {
    this.showConfirmDialog = false;
  }

  onPageChange(newPage: number) {
    this.currentPage = newPage;
    this.selectedId = null;
    this.loadNotifications();
    this.router.navigate([], {
      queryParams: { highlight: null }
    })
  }

  onPageSizeChange(newSize: number) {
    this.pageSize = newSize;
    this.currentPage = 0;
    this.loadNotifications();
  }
}
