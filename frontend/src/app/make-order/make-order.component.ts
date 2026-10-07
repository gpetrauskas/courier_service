import { Component, ViewChild } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { DeliveryOptionsComponent } from './delivery-options/delivery-options.component';
import { SenderAddressComponent } from './sender-address/sender-address.component';
import { RecipientAddressComponent } from './recipient-address/recipient-address.component';
import { OrderReviewComponent } from './order-review/order-review.component';
import { OrderService } from '../service/order/order.service';
import { PackageDetails } from '../models/order/package-details.model';
import { Address } from '../models/address/address.model';
import { DeliveryOption } from '../models/delivery-option/delivery-option.model';
import { ErrorHandlerService } from "../service/error-handler.service";
import { MatStepper, MatStepperModule } from "@angular/material/stepper";
import { MatFormField } from "@angular/material/input";
import { ReactiveFormsModule } from "@angular/forms";
import { MediaServiceService } from "../service/media-service.service";


@Component({
  selector: 'app-make-order',
  standalone: true,
  imports: [RouterModule, CommonModule, DeliveryOptionsComponent,
    SenderAddressComponent, RecipientAddressComponent, OrderReviewComponent, MatStepperModule, MatFormField, ReactiveFormsModule],
  templateUrl: './make-order.component.html',
  styleUrl: './make-order.component.css'
})
export class MakeOrderComponent {
  @ViewChild(MatStepper) stepper!: MatStepper;

  orderData = {
    parcelDetails: { weightId: null, dimensionsId: null, contents: '' } as PackageDetails,
    preferenceId: null as number | null,
    senderAddress: {} as Address,
    recipientAddress: {} as Address
  };

  selectedWeightOption?: DeliveryOption;
  selectedSizeOption?: DeliveryOption;
  selectedPreferenceOption?: DeliveryOption;
  savedAddresses: Address[] = [];

  constructor (
    private orderService: OrderService,
    private router: Router,
    private errorHandler: ErrorHandlerService,
    public screen: MediaServiceService
  ) {}

  handleSenderAddressButtonClick(event: {
    parcelDetails: PackageDetails;
    preferenceId: number | null;
    selectedWeightOption?: DeliveryOption;
    selectedSizeOption?: DeliveryOption;
    selectedPreferenceOption?: DeliveryOption
  }) {
    this.orderData.parcelDetails = event.parcelDetails;
    this.orderData.preferenceId = event.preferenceId;
    this.selectedWeightOption = event.selectedWeightOption;
    this.selectedSizeOption = event.selectedSizeOption;
    this.selectedPreferenceOption = event.selectedPreferenceOption;

    this.stepper.next();
  }

  handleRecipientAddressButtonClick(senderAddress: Address, savedAddresses: Address[]) {
    this.orderData.senderAddress = senderAddress;
    this.savedAddresses = savedAddresses;

    this.stepper.next();
  }

  handleOrderReviewButtonClick(recipientAddress: Address) {
    this.orderData.recipientAddress = recipientAddress;

    this.stepper.next();
  }

  handleConfirmOrderButtonClick() {
    this.orderService.submitOrder(this.orderData).subscribe({
      next: value => this.router.navigate([`/dashboard/user-orders`, value]),
      error: err => this.errorHandler.handleError(err)
    });
  }
}
