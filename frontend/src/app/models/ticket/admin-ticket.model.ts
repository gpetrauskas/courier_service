import { Ticket } from './ticket.model';
import { Creator } from './creator.model';

export interface AdminTicket {
  ticket: Ticket;
  creator: Creator;
}
