import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { CountdownHandlerService } from "../service/countdown-handler.service";
import { CommonModule } from "@angular/common";

@Component({
  selector: 'app-payment-success',
  templateUrl: './payment-success.component.html',
  styleUrls: ['./payment-success.component.css'],
  imports: [CommonModule],
  standalone: true
})
export class PaymentSuccessComponent implements OnInit {
  countdown$ = this.countdownHandler.countdown$;
  orderId: number | null = null;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private countdownHandler: CountdownHandlerService) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const orderIdParam = params.get('id');

      if (!orderIdParam || +orderIdParam <= 0) {
        this.router.navigate(['/dashboard/user-orders'])
      } else {
        this.orderId = +orderIdParam;
        this.startCountdown();
      }
    });
  }

  startCountdown(): void {
    this.countdownHandler.handleNavigation(
      5,
      "Successfully Paid.",
      `/dashboard/user-orders/${this.orderId}`
    );
  }

  goToOrder(): void {
    this.router.navigate([`/dashboard/user-orders/${this.orderId}`]);
  }
}
