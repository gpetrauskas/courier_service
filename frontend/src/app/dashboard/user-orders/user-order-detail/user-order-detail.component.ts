import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { mergeMap } from 'rxjs';
import { CommonModule } from '@angular/common';
import { PaymentService } from '../../../service/payment.service';
import { OrderService } from '../../../service/order/order.service'
import { PaymentInfo } from '../../../models/payment/payment-info.model';
import { UserOrderDetails } from "../../../models/order/user-order-details.model";

@Component({
  selector: 'app-user-order-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-order-detail.component.html',
  styleUrl: './user-order-detail.component.css'
})
export class UserOrderDetailComponent implements OnInit {
  @Input() order: UserOrderDetails | null = null;
  @Output() payClicked = new EventEmitter<any>();
  selectedInfo: string = '';
  paymentInfo: PaymentInfo | null = null;

  constructor(
    private router: Router,
    private paymentService: PaymentService,
    private orderService: OrderService,
    private route: ActivatedRoute,
  ) {
      const navigation = this.router.getCurrentNavigation();
      this.order = navigation?.extras.state?.['order'];
  }

  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('id');

    if (orderId) {
      this.fetchOrderDetails(Number(orderId));
    } else {
      void this.router.navigate(['/dashboard/user-orders']);
    }
  }

    fetchOrderDetails(orderId: number): void {
      this.orderService.getUserOrderById(orderId).pipe(
        mergeMap((order) => {
          this.order = order;
          return this.paymentService.getPaymentInfoByOrderId(order.id);
        })
      ).subscribe({
        next: (response) => this.paymentInfo = response,
        error: () => this.router.navigate(['/dashboard/user-orders'])
      });
    }

  showPaymentInfo() {
    if (this.selectedInfo === 'payment') {
      this.selectedInfo = '';
    } else {
      this.selectedInfo = 'payment';
    }
  }

  showPackageInfo(): void {
    if (this.selectedInfo !== "package") {
      this.selectedInfo = "package";
    } else {
      this.selectedInfo = "";
    }
  }

  onPay(orderId: number): void {
    this.payClicked.emit({ paymentInfo: this.paymentInfo, orderId });
  }
}
