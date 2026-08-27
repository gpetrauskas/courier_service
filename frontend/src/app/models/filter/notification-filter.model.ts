import { NotificationStatus } from "../../enums/notification-status.enum";

export interface NotificationFilterModel {
  kind: 'notification';
  status: NotificationStatus;
}
