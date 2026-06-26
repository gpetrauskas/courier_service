import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DeliveryOptionsService } from '../../../service/delivery-options.service';
import { ConfirmationDialogComponent } from '../../../confirmation-dialog/confirmation-dialog.component';
import { DeliveryOption } from '../../../models/delivery-option/delivery-option.model';
import { ErrorHandlerService } from "../../../service/error-handler.service";

@Component({
  selector: 'app-all-delivery-options',
  standalone: true,
  imports: [CommonModule, ConfirmationDialogComponent],
  templateUrl: './all-delivery-options.component.html',
  styleUrl: './all-delivery-options.component.css'
})
export class AllDeliveryOptionsComponent implements OnInit {
  deliveryOptions: DeliveryOption[] = [];
  selectedOption: DeliveryOption | null = null;
  isDialogVisible: boolean = false;

  constructor(
    private deliveryOptionsService: DeliveryOptionsService,
    private router: Router,
    private errorHandler: ErrorHandlerService
  ) {}

  ngOnInit() {
    this.loadDeliveryOptions();
  }

  loadDeliveryOptions() {
    this.deliveryOptionsService.getAllNotCategorized().subscribe(
      (response) => {
        this.deliveryOptions = response;
      }
    )
  }

  editDeliveryOption(preference: DeliveryOption) {
    this.router.navigate(['/admin-dashboard/manage-delivery-option/delivery-option-details', preference.id], {
      state: { preference }
    });
  }

  onConfirmDelete() {
    if (this.selectedOption) {
      this.errorHandler.handleRequest(this.deliveryOptionsService.delete(this.selectedOption.id), "Deleted successfully",
        () => this.deliveryOptions = this.deliveryOptions.filter(opt => opt.id !== this.selectedOption?.id)
      );
    }
    this.isDialogVisible = false;
  }

  openConfirmDialog(preference: any) {
    this.selectedOption = { ...preference};
    this.isDialogVisible = true;
  }

  onCancel() {
    this.selectedOption = null;
    this.isDialogVisible = false;
  }
}
