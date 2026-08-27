import { NotificationStatus } from "../../enums/notification-status.enum";

export interface AdminNotificationFilterModel {
  kind: 'adminNotification';
  keyword: string;
  status: NotificationStatus;
}
