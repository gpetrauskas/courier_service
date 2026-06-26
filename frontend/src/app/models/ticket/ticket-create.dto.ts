import { TicketPriority } from '../../enums/ticket-priority.enum';

export interface TicketCreate {
  title: string;
  description: string;
  priority: TicketPriority;
}
