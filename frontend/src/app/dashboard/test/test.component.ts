import { Component, OnInit } from '@angular/core';
import { PaymentMethodService } from '../../service/person/payment-method.service';
import { CommonModule } from '@angular/common';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog.component';
import { ErrorHandlerService } from "../../service/error-handler.service";

@Component({
  selector: 'app-test',
  standalone: true,
  imports: [CommonModule, ConfirmationDialogComponent],
  templateUrl: './test.component.html',
  styleUrl: './test.component.css'
})
export class TestComponent implements OnInit {
  savedPaymentMethods: any[] = [];
  paymentsFound: boolean = false;
  paymentToDelete: any = null;
  showConfirmDialog: boolean = false;
  methodToDeleteName: string = '';
  methodToDeleteType: string = '';

  constructor(private paymentMethodService: PaymentMethodService, private errorHandler: ErrorHandlerService) {}

  ngOnInit(): void {
    this.fetchSavedPaymentMethods();
  }

  fetchSavedPaymentMethods(): any {
    this.paymentMethodService.fetchUserSavedPaymentMethods().subscribe({
      next: (response: any) => {
        this.savedPaymentMethods = response;
        this.paymentsFound = true;
      },
      error: (error: any) => {
        this.paymentsFound = false;
        this.errorHandler.handleError(error);
      }
    });
  }

  openConfirmDialog(payment: any): void {
    this.paymentToDelete = payment;
    this.showConfirmDialog = true;
    this.methodToDeleteName = payment.type === 'CREDIT_CARD' ? payment.last4 : payment.ppEmail;
    this.methodToDeleteType = payment.type;
  }

  onDeleteConfirmed(): void {
    if (this.paymentToDelete) {
      this.errorHandler.handleRequest(this.paymentMethodService.deletePaymentMethod(this.paymentToDelete.id), "Successfully deleted");
      this.showConfirmDialog = false;
    }
  }

  onDeleteCanceled(): void {
    this.showConfirmDialog = false;
  }
}
