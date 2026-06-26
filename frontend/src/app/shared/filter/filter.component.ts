import { Component, EventEmitter, Output, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NotificationStatus } from '../../enums/notification-status.enum';
import { UserRole } from "../../enums/user-role.enum";
import { OrderStatus } from "../../enums/order-status.enum";
import { TicketStatus } from '../../enums/ticket-status.enum';
import { DeliveryStatus } from "../../enums/delivery-status.enum";
import { TaskType } from "../../enums/task-type.enum";

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


  @Output() filterChange = new EventEmitter<any>();
  // notificatiosn filter
  notificationStatus: string = '';

  notificationKeyword: string = '';


  // user filter
  userRole: string = '';
  keyword: string = '';

  // ticket filter
  ticketStatus: string = '';
  personIdTicket: number | null = null;

  // order filter
  orderStatus: string = '';
  userID: number | null = null;

  // delivery task list filter
  canceledTasks: boolean = false;
  taskID: number | null = null;
  courierID: number | null = null;
  taskType: string = '';
  deliveryStatus: string = '';

  notificationStatuses = Object.values(NotificationStatus);
  userRoles = Object.values(UserRole);
  orderStatuses = Object.values(OrderStatus);
  taskTypes = Object.values(TaskType);
  deliveryStatuses = Object.values(DeliveryStatus);
  ticketStatuses = Object.values(TicketStatus);

  emitUserFilter() {
    this.filterChange.emit({ userRole: this.userRole, keyword: this.keyword });
  }

  emitTicketFilter() {
    this.filterChange.emit({ ticketStatus: this.ticketStatus, personId: this.personIdTicket });
  }

  emitOrderFilter() {
    this.filterChange.emit({ userId: this.userID, orderStatus: this.orderStatus });
  }

  emitTaskFilter() {
    this.filterChange.emit({ taskId: this.taskID, courierId: this.courierID, taskType: this.taskType,
      deliveryStatus: this.deliveryStatus });
  }

  toggleCanceledTasks() {
    this.canceledTasks = this.canceledTasks != true;
    this.emitTaskFilter();
  }

  emitNotificationFilter() {
    this.filterChange.emit({ status: this.notificationStatus });
  }

  emitAdminNotificationFilter() {
    this.filterChange.emit({ keyword: this.notificationKeyword });
  }

}
