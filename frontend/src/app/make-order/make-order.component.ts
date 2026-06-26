import { Component } from '@angular/core';
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


@Component({
  selector: 'app-make-order',
  standalone: true,
  imports: [RouterModule, CommonModule, DeliveryOptionsComponent,
    SenderAddressComponent, RecipientAddressComponent, OrderReviewComponent],
  templateUrl: './make-order.component.html',
  styleUrl: './make-order.component.css'
})
export class MakeOrderComponent {
  activeTab: string = 'startOrder';
  completedTabs = new Set<string>();

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

  constructor (private orderService: OrderService, private router: Router, private errorHandler: ErrorHandlerService) {}

  isTabEnabled(tab: string): boolean {
    const tabOrder = ['deliveryOptions', 'senderAddress', 'recipientAddress', 'orderReview'];
    const currentIndex = tabOrder.indexOf(this.activeTab);
    const tabIndex = tabOrder.indexOf(tab);
    return tabIndex <= currentIndex;
  }

  isTabVisible(tab: string): boolean {
    return this.isTabEnabled(tab);
  }

  selectTab(event: Event, tab: string) {
    event.preventDefault();
    if (this.isTabEnabled(tab)) {
      this.activeTab = tab;
    }
  }

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

    this.completedTabs.add('deliveryOptions');
    this.activeTab = 'senderAddress';
  }

  handleRecipientAddressButtonClick(senderAddress: Address, savedAddresses: Address[]) {
    this.orderData.senderAddress = senderAddress;
    this.savedAddresses = savedAddresses;

    this.completedTabs.add('senderAddress');
    this.activeTab = 'recipientAddress';
  }

  handleOrderReviewButtonClick(recipientAddress: Address) {
    this.orderData.recipientAddress = recipientAddress;

    this.completedTabs.add('recipientAddress');
    this.activeTab = 'orderReview';

    console.log(this.orderData.senderAddress);
    console.log(this.orderData.recipientAddress);
  }

  handleConfirmOrderButtonClick() {
    this.orderService.submitOrder(this.orderData).subscribe({
      next: (response) => {
        this.router.navigate([`/dashboard/user-orders/`, response]);
      },
      error: (error) => {
        this.errorHandler.showError(error);
      }
    });
  }

  startOrder() {
    this.activeTab = 'deliveryOptions';
  }
}
