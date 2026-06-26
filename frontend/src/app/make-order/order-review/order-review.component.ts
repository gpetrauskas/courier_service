import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderDTO } from '../../models/order/order.model';
import { DeliveryOption } from '../../models/delivery-option/delivery-option.model';
import { ErrorHandlerService } from "../../service/error-handler.service";

@Component({
  selector: 'app-order-review',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-review.component.html',
  styleUrl: './order-review.component.css'
})
export class OrderReviewComponent {

  constructor(private errorHandler: ErrorHandlerService) {
  }
/*
  @Input() isSubmitting: boolean = false;
*/
  alert$ = this.errorHandler.alert$;
  @Input() orderData!: OrderDTO;
  @Input() selectedWeightOption?: DeliveryOption;
  @Input() selectedSizeOption?: DeliveryOption;
  @Input() selectedPreferenceOption?: DeliveryOption;
  @Output() confirmOrderButtonClicked = new EventEmitter<void>();

  confirmOrder() {
    this.confirmOrderButtonClicked.emit();
  }
}
