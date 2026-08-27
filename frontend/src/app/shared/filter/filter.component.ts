import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NotificationStatus } from '../../enums/notification-status.enum';
import { UserRole } from "../../enums/user-role.enum";
import { OrderStatus } from "../../enums/order-status.enum";
import { TicketStatus } from '../../enums/ticket-status.enum';
import { DeliveryStatus } from "../../enums/delivery-status.enum";
import { TaskType } from "../../enums/task-type.enum";
import { FilterEventModel } from "../../models/filter/filter-event.model";
import { UserFilterModel } from "../../models/filter/user-filter.model";
import { TicketFilterModel } from "../../models/filter/ticket-filter.model";
import { OrderFilterModel } from "../../models/filter/order-filter.model";
import { TaskFilterModel } from "../../models/filter/task-filter.model";
import { NotificationFilterModel } from "../../models/filter/notification-filter.model";
import { AdminNotificationFilterModel } from "../../models/filter/admin-notification-filter.model";

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './filter.component.html',
  styleUrl: './filter.component.css'
})
export class FilterComponent {
  @Input() showNotificationFilter: boolean = false;
  @Input() showAdminNotificationFilter: boolean = false;
  @Input() showUserFilter: boolean = false;
  @Input() showOrderFilter: boolean = false;
  @Input() showTaskListFilter: boolean = false;
  @Input() showTicketFilter: boolean = false;
  @Input() isAdmin: boolean = false;


  @Output() filterChange = new EventEmitter<FilterEventModel>();
  // notificatiosn filter
  notificationStatus: NotificationStatus = NotificationStatus.ALL;

  notificationKeyword: string = '';


  // user filter
  userRole: UserRole = UserRole.All;
  keyword: string = '';

  // ticket filter
  ticketStatus: TicketStatus = TicketStatus.ALL;
  personIdTicket: number | null = null;

  // order filter
  orderStatus: OrderStatus = OrderStatus.All;
  userID: number | null = null;

  // delivery task list filter
  canceledTasks: boolean = false;
  taskID: number | null = null;
  courierID: number | null = null;
  taskType: TaskType = TaskType.All;
  deliveryStatus: DeliveryStatus = DeliveryStatus.All;

  // activity log

  notificationStatuses = Object.values(NotificationStatus);
  userRoles = Object.values(UserRole);
  orderStatuses = Object.values(OrderStatus);
  taskTypes = Object.values(TaskType);
  deliveryStatuses = Object.values(DeliveryStatus);
  ticketStatuses = Object.values(TicketStatus);

  emitUserFilter() {
    const uf: UserFilterModel = {
      kind: 'user',
      userRole: this.userRole,
      keyword: this.keyword
    }
    this.filterChange.emit(uf);
  }

  emitTicketFilter() {
    const tf: TicketFilterModel = {
      kind: 'ticket',
      ticketStatus: this.ticketStatus,
      personId: this.personIdTicket
    }
    this.filterChange.emit(tf);
  }

  emitOrderFilter() {
    const orderf: OrderFilterModel = {
      kind: 'order',
      userId: this.userID,
      orderStatus: this.orderStatus
    }
    this.filterChange.emit(orderf);
  }

  emitTaskFilter() {
    const tf: TaskFilterModel = {
      kind: 'task',
      taskId: this.taskID,
      courierId: this.courierID,
      taskType: this.taskType,
      deliveryStatus: this.deliveryStatus
    }
    this.filterChange.emit(tf);
  }

  toggleCanceledTasks() {
    this.canceledTasks = !this.canceledTasks;
    this.emitTaskFilter();
  }

  emitNotificationFilter() {
    const nf: NotificationFilterModel = {
      kind: 'notification',
      status: this.notificationStatus
    }
    this.filterChange.emit(nf);
  }

  emitAdminNotificationFilter() {
    const af: AdminNotificationFilterModel = {
      kind: 'adminNotification',
      keyword: this.notificationKeyword,
      status: this.notificationStatus
    }
    this.filterChange.emit(af);
  }
}
