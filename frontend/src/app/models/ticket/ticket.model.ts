import { TicketStatus } from '../../enums/ticket-status.enum';
import { TicketPriority } from '../../enums/ticket-priority.enum';
import { Creator } from './creator.model';

export interface Ticket {
  id: number;
  title: string;
  description: string;
  status: TicketStatus;
  priority: TicketPriority;
  createdAt: string;
  updatedAt: string;
  creator?: Creator;
}
