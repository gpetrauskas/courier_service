import { PaginatedResponse } from '../paginated-response.model';
import { Notification } from './notification.model';

export interface PersonNotificationPageResponse {
  page: PaginatedResponse<Notification>;
  unreadCount: number;
}
