import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DeliveryOptionsService } from '../../../service/delivery-options.service';
import { DeliveryOption } from '../../../models/delivery-option/delivery-option.model';
import { OrderSectionRequest } from '../../../models/order/order-section-request.dto';
import { PaymentSectionRequest } from '../../../models/payment/payment-section-request.dto';
import { ParcelSectionRequest } from '../../../models/order/parcel-section-request.dto';
import { AddressSectionRequest } from '../../../models/address/address-section-request.dto';
import { OrderService } from '../../../service/order/order.service';
import { ErrorHandlerService } from "../../../service/error-handler.service";
import { AdminOrder } from "../../../models/order/admin-order.model";
import { PaymentService } from "../../../service/payment.service";
import { OrderStatus } from "../../../enums/order-status.enum";
import { PaymentStatus } from "../../../enums/payment-status.enum";
import { ParcelStatus } from "../../../enums/parcel-status.enum";

@Component({
  selector: 'app-order-details',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css']
})
export class OrderDetailsComponent implements OnInit {
  order: AdminOrder | null = null;
  orderBackup: AdminOrder | null = null;
  preferenceOptions: DeliveryOption[] = [];
  weightOptions: DeliveryOption[] = [];
  sizeOptions: DeliveryOption[] = [];
  sectionToEdit: string = "";
  orderStatuses: OrderStatus[] = Object.values(OrderStatus).filter(s => s !== '');
  paymentStatuses: PaymentStatus[] = Object.values(PaymentStatus);
  packageStatuses: ParcelStatus[] = Object.values(ParcelStatus);

  constructor(
    private route: ActivatedRoute,
    private POService: DeliveryOptionsService,
    private orderService: OrderService,
    private paymentService: PaymentService,
    private errorHandler: ErrorHandlerService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.params['id'];
    this.fetchOrderDetails(id);
  }

  transform(status: string) {
    return status.replaceAll("_", " ");
  }

  toggleEditButton(section: string) {
    if (section === 'orderSection') {
      this.loadDeliveryOptions();
    }

    this.sectionToEdit = section;
    this.orderBackup = JSON.parse(JSON.stringify(this.order));
  }

  private loadDeliveryOptions() {
    this.POService.getDeliveryOptions().subscribe({
    next: (data) => {
      this.weightOptions = data.WEIGHT;
      this.sizeOptions = data.SIZE;
      this.preferenceOptions = data.PREFERENCE;
    }});
  }

  saveSection(section: string) {
    if (this.order) {
      switch (section) {
        case 'orderSection':
          this.saveOrderSection();
          break;
        case 'paymentSection':
          this.savePaymentSection();
          break;
        case 'parcelSection':
          this.saveParcelSection();
          break;
        case 'senderSection':
        case 'recipientSection':
          this.saveAddressSection(section);
          break;
      }
    }
  }

private savePaymentSection(): void {
    const changes: PaymentSectionRequest = {};
    if (this.order && this.order.payment.status !== this.orderBackup?.payment.status) {
      changes.status = this.order.payment.status;

      this.errorHandler.handleRequest(this.paymentService.updatePaymentSection(this.order.id, changes), "Successfully updated",
        () => this.saveOrderAsBackup(),
        () => this.cancelSection()
      );
    }
}

  private saveParcelSection(): void {
    const changes: Partial<ParcelSectionRequest> = {};

    if (this.order) {
      if (this.order.parcel.status !== this.orderBackup?.parcel.status) {
        changes.status = this.order.parcel.status;
      }
      if (this.order.parcel.contents !== this.orderBackup?.parcel.contents) {
        changes.contents = this.order.parcel.contents;
      }

      if (Object.keys(changes).length === 0) return;

      this.errorHandler.handleRequest(this.orderService.updateParcelSection(this.order.id, changes), "Successfully updated parcel",
        () => this.saveOrderAsBackup(),
        () => this.cancelSection()
      );
    }
  }

  private saveAddressSection(section: string): void {
    const changes: Partial<AddressSectionRequest> = {};

    const currentAddress = (section === 'senderSection') ? this.order?.sender : this.order?.recipient;
    const backupAddress = (section === 'recipientSection') ? this.orderBackup?.sender : this.orderBackup?.recipient;

    if (currentAddress && backupAddress) {
      if (currentAddress.name !== backupAddress.name) {
        changes.name = currentAddress.name;
      }
      if (currentAddress.street !== backupAddress.street) {
        changes.street = currentAddress.street;
      }
      if (currentAddress.houseNumber !== backupAddress.houseNumber) {
        changes.houseNumber = currentAddress.houseNumber;
      }
      if (currentAddress.flatNumber !== backupAddress.flatNumber) {
        changes.flatNumber = currentAddress.flatNumber;
      }
      if (currentAddress.city !== backupAddress.city) {
        changes.city = currentAddress.city;
      }
      if (currentAddress.postCode !== backupAddress.postCode) {
        changes.postCode = currentAddress.postCode;
      }
      if (currentAddress.phoneNumber !== backupAddress.phoneNumber) {
        changes.phoneNumber = currentAddress.phoneNumber;
      }

      if (Object.keys(changes).length < 2) return;
      changes.id = currentAddress.id;
      changes.selectedAddress = section;

      this.errorHandler.handleRequest(this.orderService.updateAddressSection(this.order!.id, changes), "Address section saved",
        () => this.saveOrderAsBackup(),
        () => this.cancelSection()
      );
    }
  }

  private saveOrderSection(): void {
    const changes: Partial<OrderSectionRequest> = {};

    if (this.order) {
      if (this.order.status !== this.orderBackup?.status) {
        changes.status = this.order.status;
      }
      if (this.order.deliveryMethodName !== this.orderBackup?.deliveryMethodName) {
        changes.deliveryMethodName = this.order.deliveryMethodName;
      }

      if (Object.keys(changes).length === 0) return;

      this.errorHandler.handleRequest(this.orderService.updateOrderSection(this.order.id, changes), "Order section updated",
        () => this.saveOrderAsBackup(),
        () => this.cancelSection()
      );
    }
  }

  private saveOrderAsBackup() {
    this.orderBackup = JSON.parse(JSON.stringify(this.order));
    this.sectionToEdit = "";
  }

  cancelSection() {
    this.order = JSON.parse(JSON.stringify(this.orderBackup));
    this.sectionToEdit = "";
  }

  fetchOrderDetails(orderId: number) {
    this.orderService.getAdminOrderById(orderId).subscribe(
      (response) => {
        this.order = response;
      }
    );
  }
}
