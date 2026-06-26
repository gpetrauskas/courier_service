import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { PaymentFormComponent } from '../payment-form/payment-form.component';
import { UserOrderDetailComponent } from './user-orders/user-order-detail/user-order-detail.component';
import { PaymentInfo } from '../models/payment/payment-info.model';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterModule, PaymentFormComponent, CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  activeTabGroup: 'myAccount' | 'orders' | 'settings' | 'orderDetails' | 'ticketManage' | null = null;
  isAccountSettingsActive: boolean = false;
  showPaymentSection: boolean = false;
  paymentInfo: PaymentInfo | null = null;
  orderId!: number;

  constructor(private router: Router) {
  this.router.events.pipe(
    filter(event => event instanceof NavigationEnd))
    .subscribe(() => {
      this.showPaymentSection = false;
      this.updateActiveTabGroup();
    });
  }

  updateActiveTabGroup() {
    const url = this.router.url;

    if (url.includes('/test') || url.includes('/address')) {
      this.activeTabGroup = 'myAccount';
    } else if (url.includes('/account-settings')) {
      this.activeTabGroup = 'settings';
    } else if (url.includes('/user-orders/')) {
      this.activeTabGroup = 'orderDetails';
    } else if (url.includes('/ticket')) {
      this.activeTabGroup = 'ticketManage';
    } else {
      this.activeTabGroup = null;
    }
  }

  handlePayClick({ paymentInfo, orderId }: { paymentInfo: PaymentInfo; orderId: number}): void {
    this.paymentInfo = paymentInfo;
    this.orderId = orderId;
    this.showPaymentSection = !this.showPaymentSection;
  }

  handleActivate(event: any): void {
    if (event instanceof UserOrderDetailComponent) {
      event.payClicked.subscribe(({ paymentInfo, orderId }: { paymentInfo: PaymentInfo; orderId: number }) =>
        this.handlePayClick({ paymentInfo, orderId })
      );
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (this.showPaymentSection && !target.closest('.payment-section') && !target.closest('.pay-button')) {
      this.showPaymentSection = false;
    }
  }
}
