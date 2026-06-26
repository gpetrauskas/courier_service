import { TicketUpdateKind } from '../../enums/ticket-update-kind.type';
import { TicketStatus } from '../../enums/ticket-status.enum';
import { TicketPriority } from '../../enums/ticket-priority.enum';

export interface TicketUpdateRequest {
  id: number;
  partTarget: {
    kind: TicketUpdateKind;
    status?: TicketStatus;
    priority?: TicketPriority;
  };
}
