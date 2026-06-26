import { NotificationTargetType } from '../../enums/notification-target-type.enum';

export interface BroadcastTarget {
  type: NotificationTargetType;
}

export interface IndividualTarget {
  personId: number;
}

export interface NotificationRequest {
  title: string;
  message: string;
  target: BroadcastTarget | IndividualTarget;
}
