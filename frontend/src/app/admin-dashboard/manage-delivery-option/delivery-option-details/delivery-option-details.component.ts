import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DeliveryOption } from '../../../models/delivery-option/delivery-option.model';
import { DeliveryOptionsService } from '../../../service/delivery-options.service';
import { ErrorHandlerService } from "../../../service/error-handler.service";

@Component({
  selector: 'app-delivery-option-details',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './delivery-option-details.component.html',
  styleUrl: './delivery-option-details.component.css'
})
export class DeliveryOptionDetailsComponent implements OnInit {
  preference: DeliveryOption | null = null;
  backupDeliveryOption: DeliveryOption | null = null;

  constructor(
    private router: Router,
    private deliveryOptionsService: DeliveryOptionsService,
    private handlerService: ErrorHandlerService) {
  }

  ngOnInit() {
    this.preference = history.state.preference;
    if (this.preference) {
      this.backupDeliveryOption = structuredClone(this.preference);
    }
  }

  saveOption() {
    if (!this.preference || !this.backupDeliveryOption) return;

    const updateData: Partial<DeliveryOption> = {};

    updateData.id = this.preference.id;

    if (this.preference.name !== this.backupDeliveryOption.name) {
      updateData.name = this.preference.name;
    }

    if (this.preference.description !== this.backupDeliveryOption.description) {
      updateData.description = this.preference.description;
    }

    if (this.preference.price !== this.backupDeliveryOption.price) {
      updateData.price = this.preference.price;
    }

    this.handlerService.handleRequest(this.deliveryOptionsService.update(this.preference.id, updateData), "successfully updated",
      () => this.cancel()
    );
  }

  cancel() {
    this.preference = null;
    this.backupDeliveryOption = null;
    this.router.navigate(['/admin-dashboard/manage-delivery-option/all-delivery-options']);
  }
}
