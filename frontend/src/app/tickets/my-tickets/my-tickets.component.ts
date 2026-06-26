import { Component, OnInit } from '@angular/core';
import { TicketStatus } from '../../enums/ticket-status.enum';
import { TicketService } from '../../service/ticket.service';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { Ticket } from '../../models/ticket/ticket.model';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog.component';
import { FilterComponent } from '../../shared/filter/filter.component';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';
import { ErrorHandlerService } from "../../service/error-handler.service";

@Component({
  selector: 'app-my-tickets',
  standalone: true,
  imports: [MatCardModule, CommonModule, MatIconModule, MatButtonModule, PaginationComponent,
    ConfirmationDialogComponent, FilterComponent],
  templateUrl: './my-tickets.component.html',
  styleUrl: './my-tickets.component.css'
})
export class MyTicketsComponent implements OnInit {
  constructor(
    private ticketService: TicketService,
    private router: Router,
    private authService: AuthService,
    private errorHandler: ErrorHandlerService) {
  }

  tickets: Ticket[] = [];
  statuses = Object.values(TicketStatus);
  ticketToClose: Ticket | null = null;
  isAdmin = false;
  isDialogVisible = false;
  itemName = '';
  itemType = '';
  actionType = '';
  currentPage = 0;
  pageSize = 5;
  totalItems = 0;
  totalPages = 0;


  ngOnInit() {
    this.isAdmin = this.authService.isAdminSync();
    this.loadAllTickets(this.currentPage, this.pageSize);
  }

  loadAllTickets(currentPage: number, pageSize: number, status?: string, personId?: number) {
    const tickets$ = this.isAdmin
        ? this.ticketService.getAllForAdmin(currentPage, pageSize, status, personId)
        : this.ticketService.getAllForUser(currentPage, pageSize, status);

    tickets$.subscribe({
      next: (data) => {
        this.tickets = data.data;
        this.currentPage = data.currentPage;
        this.totalItems = data.totalItems;
        this.totalPages = data.totalPages;
      }
    });
  }

  choseTicket(ticket: any) {
    const isAdminRoute = this.router.url.includes('admin-dashboard');
    const baseRoute = isAdminRoute ? 'admin-dashboard/tickets/all' : 'dashboard/ticket/my-tickets';
    this.router.navigate([baseRoute, ticket.id], {
      state: { ticket }
    });
  }

  pageChange(event: any) {
    this.currentPage = event;
    this.loadAllTickets(this.currentPage, this.pageSize);
  }

  onPageSizeChange(event: any) {
    this.pageSize = event;
    this.currentPage = 0;
    this.loadAllTickets(this.currentPage, this.pageSize);
  }

  openDialog(ticket: Ticket) {
    this.ticketToClose = ticket;
    this.itemType = 'this'
    this.actionType = 'close'
    this.itemName = 'ticket: ' + ' "' + ticket.title + '"';
    this.isDialogVisible = true;
  }

  onDialogConfirmed() {
    this.closeTicket(this.ticketToClose);
    this.resetDialog();
  }

  onDialogCanceled() {
    this.resetDialog();
  }

  resetDialog() {
    this.isDialogVisible = false;
    this.ticketToClose = null;
    this.actionType = '';
    this.itemType = '';
    this.itemName = '';
  }

  onFilterChange(filter: any) {
    const filterStatus = filter.ticketStatus;
    const personId = Number(filter.personId);
    this.currentPage = 0;
    this.loadAllTickets(this.currentPage, this.pageSize, filterStatus, personId);
  }

  closeTicket(ticket: any) {
    if (!ticket || !this.tickets.some(t => t.id === ticket.id)) {
      return;
    }

    this.errorHandler.handleRequest(
      this.ticketService.close(ticket.id),
      "Successfully closed",
      () => ticket.status = 'CLOSED'
    );
  }
}
