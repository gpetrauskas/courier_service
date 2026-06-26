import { Component, OnInit } from '@angular/core';
import { PersonService } from '../../../service/person/person.service';
import { ConfirmationDialogComponent } from '../../../confirmation-dialog/confirmation-dialog.component';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BanActionRequestDTO } from '../../../models/person/ban-action-request-dto.model';
import { PaginatedResponse } from '../../../models/paginated-response.model';
import { PersonResponseDTO } from '../../../models/person/person-basic.model';
import { FilterComponent } from '../../../shared/filter/filter.component';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import { BanService } from "../../../service/person/ban.service";
import { ErrorHandlerService } from "../../../service/error-handler.service";

type NoUserAction = {
  isUserActionPending: false;
};

type UserActionPending = {
  isUserActionPending: true;
  userId: number;
};

type UserActionState = NoUserAction | UserActionPending;

@Component({
  selector: 'app-all-users',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ConfirmationDialogComponent, FilterComponent, PaginationComponent
  ],
  templateUrl: './all-users.component.html',
  styleUrl: './all-users.component.css'
})
export class AllUsersComponent implements OnInit {
  users: PersonResponseDTO[] = [];
  action: 'DELETE' | 'BAN' | 'UNBAN' | '' = 'DELETE';
  userActionState: UserActionState = { isUserActionPending: false };
  isDialogVisible: boolean = false;
  showReasonInput: boolean = false;
  currentPage: number = 0;
  totalPages: number = 0;
  totalItems: number = 0;
  pageSize: number = 10;
  inputPageSize: number = this.pageSize;
  userRole: string = '';
  itemName: string = '';
  keyword: string = '';
  reason: string= '';

  constructor(
    private banService: BanService,
    private router: Router,
    private personServiceImpl: PersonService,
    private errorHandler: ErrorHandlerService
  ) {}

  ngOnInit() {
    this.loadUsers(this.currentPage, this.pageSize);
  }

  loadUsers(page: number, size: number, searchKeyword?: string) {
    this.personServiceImpl.fetchAllPersons(page, size, this.userRole, this.keyword).subscribe({
     next: (data: PaginatedResponse<PersonResponseDTO>) => {
       this.users = data.data;
       this.totalItems = data.totalItems;
       this.totalPages = data.totalPages;
     },
     error: (err) => {
       this.errorHandler.showError(err);
     }
    });
  }

  viewUser(id: number) {
    this.router.navigate(['/admin-dashboard/manage-user/user-details', id], {
      state: { personId: id }
    });
  }

  editUser(id: number) {
    this.router.navigate([`/admin-dashboard/manage-user/user-details`, id], {
      state: { personId: id },
      queryParams: {edit: true}
    });
  }

  setReason(reason: string) {
    this.reason = reason;
    this.onConfirmAction();
  }

  onFilterChange(filterData: any) {
    console.log('Filter changed to role:', this.userRole);
    this.userRole = filterData.userRole;
    this.keyword = filterData.keyword;
    this.currentPage = 0;
    this.loadUsers(this.currentPage, this.pageSize);
  }

  onPageSizeChange(pageSize: number) {
    this.pageSize = pageSize;
    this.currentPage = 0;
    this.loadUsers(this.currentPage, this.pageSize);
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadUsers(this.currentPage, this.pageSize);
  }

  openConfirmDialog(userId: number, actionType: 'DELETE' | 'BAN' | 'UNBAN') {
    this.action = actionType;
    this.setUserAction(userId);

    const user = this.users.find(usr => usr.id === userId);
    if (!user) {
      return;
    }

    this.itemName = this.formatUserName(user);
    this.showReasonInput = true;
    this.isDialogVisible = true;
  }

  onConfirmAction() {
    if (!this.userActionState.isUserActionPending || !this.action) return;

    const { userId } = this.userActionState;
    const actionHandlers: Record<string, (userId: number) => void> = {
      DELETE: () => this.handleDelete(userId),
      BAN: () => this.handleBan(userId, true),
      UNBAN: () => this.handleBan(userId, false),
    };

    const handler = actionHandlers[this.action];
    if (handler) {
      handler(userId);
    } else {
      console.error(`unknown action: ${this.action}`);
    }

    this.closeDialog();
  }

  private handleDelete(userId: number): void {
    this.errorHandler.handleRequest(this.personServiceImpl.deletePerson(userId), "successfully deleted",
      () => {
      this.users = this.users.filter(usr => usr.id !== userId);
      this.clearUserAction();
      },
      () => {
      this.clearUserAction();
      }
    );
  }

  private handleBan(userId: number, isBanned: boolean): void {
    const user = this.users.find(usr => usr.id === userId);
    if (!user) {
      this.clearUserAction();
      return;
    }

    const request: BanActionRequestDTO = {
      reason: this.reason,
    };

    this.errorHandler.handleRequest(this.banService.banUnban(userId, request),
      "Action was successful",
      () => user.isBlocked = isBanned,
      () => this.clearUserAction()
    );
  }

  private formatUserName(user: any): string {
    return `${user.name ?? 'Unknown Name'}\n(${user.email ?? 'Unknown email'}, ID: ${user.id})`;
  }

  closeDialog(): void {
    this.action = '';
    this.isDialogVisible = false;
  }

  private setUserAction(userId: number) {
    this.userActionState = { isUserActionPending: true, userId: userId };
  }

  private clearUserAction() {
    this.userActionState = { isUserActionPending: false }
  }
}
