import { TicketStatus } from "../../enums/ticket-status.enum";

export interface TicketFilterModel {
  kind: 'ticket';
  ticketStatus: TicketStatus;
  personId: number | null;
}
