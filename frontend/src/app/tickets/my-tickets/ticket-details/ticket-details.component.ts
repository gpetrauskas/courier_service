import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule, Validators, FormBuilder, FormGroup, FormsModule } from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TicketService } from '../../../service/ticket.service';
import { AuthService } from '../../../auth/auth.service';
import { TicketStatus } from '../../../enums/ticket-status.enum';
import { TicketPriority } from '../../../enums/ticket-priority.enum';
import { TicketUpdateKind } from '../../../enums/ticket-update-kind.type';
import { TicketUpdateRequest } from '../../../models/ticket/ticket-update-request.dto';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Ticket } from "../../../models/ticket/ticket.model";
import { of } from "rxjs";
import { switchMap } from "rxjs/operators";
import { AddComment } from "../../../models/ticket/add-comment.model";
import { TicketComment } from "../../../models/ticket/ticket-comment.model";

@Component({
  selector: 'app-ticket-details',
  standalone: true,
  imports: [MatCardModule, MatChipsModule, MatDividerModule, MatInputModule, CommonModule,
    ReactiveFormsModule, MatFormFieldModule, MatSelectModule, FormsModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './ticket-details.component.html',
  styleUrl: './ticket-details.component.css'
})
export class TicketDetailsComponent implements OnInit{
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ticketService = inject(TicketService);
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);

  ticket: Ticket | null = null;
  comments: TicketComment[] = [];
  statusOptions = Object.values(TicketStatus).slice(1, 99);
  priorityOptions = Object.values(TicketPriority);
  newStatus: string = '';
  tempStatus: string = '';
  tempPriority: string = '';
  currentUsername = this.authService.getUserName();
  isAdmin = this.authService.isAdminSync();

  commentForm: FormGroup = this.fb.group({
    message: ['', Validators.required]
  });

  ngOnInit() {
    const ticketId = history.state.ticket?.id || Number(this.route.snapshot.paramMap.get('id'));

    of(history.state.ticket).pipe(
      switchMap(ticket => ticket ? of(ticket) : this.ticketService.getTicket(ticketId))
    ).subscribe(ticket => {
      this.ticket = ticket;
      this.tempStatus = ticket.status;
      this.tempPriority = ticket.priority;
      this.getTicketComments(ticketId);
    });
  }

  userDetails(personId: number) {
    void this.router.navigate(['../../../admin-dashboard/manage-user/user-details/', personId], {
      state: { personId: personId }
    });
  }

  getTicketComments(ticketId: number) {
    this.ticketService.getComments(ticketId, 0, 10).subscribe({
      next: (data) => {
        this.comments = data.data;
        console.log("veikia ???" + data);
      }
    });
  }

  update(ticketId: number, data: string, kind: string) {
    const payLoad: TicketUpdateRequest = {
      id: ticketId,
      partTarget: {
        kind: kind.toUpperCase() as TicketUpdateKind,
        [kind.toLowerCase()]: data
      }
    }

    this.ticketService.updateTicketProperty(payLoad).subscribe({
      next: () => {
       if (this.ticket) {
         if (kind.toLowerCase() === "status") {
           this.ticket.status = data as TicketStatus;
         } else {
           this.ticket.priority = data as TicketPriority;
         }
       }
      }
    });
  }

  addComment(ticket: Ticket) {
    if (this.commentForm.invalid) { return; }

    const request: AddComment = {
      message: this.commentForm.value.message
    }

    this.ticketService.addComment(ticket.id, request).subscribe({
      next: (data) => {
        this.ticket!.updatedAt = data.timestamp;
        history.replaceState({ ticket: this.ticket }, '');
        this.commentForm.reset();
        this.getTicketComments(ticket.id);
      }
    });
  }
}
