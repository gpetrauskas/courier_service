import { AdminNotificationFilterModel } from "./admin-notification-filter.model";
import { NotificationFilterModel } from "./notification-filter.model";
import { OrderFilterModel } from "./order-filter.model";
import { TaskFilterModel } from "./task-filter.model";
import { TicketFilterModel } from "./ticket-filter.model";
import { UserFilterModel } from "./user-filter.model";

export type FilterEventModel = AdminNotificationFilterModel | NotificationFilterModel | OrderFilterModel | TaskFilterModel | TicketFilterModel | UserFilterModel;
