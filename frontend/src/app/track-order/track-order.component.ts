import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TrackOrderService } from '../service/track-order.service';

@Component({
  selector: 'app-track-order',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './track-order.component.html',
  styleUrls: ['./track-order.component.css']
})
export class TrackOrderComponent {
  trackingNumber: string = '';
  parcelStatus: string | null = null;
  errorMessage: string | null = null;

  constructor(private trackOrderService: TrackOrderService) {}

  onSubmit() {
    this.parcelStatus = null;
    this.errorMessage = null;

    this.trackOrderService.getPackageStatus(this.trackingNumber).subscribe({
      next: (status) => {
        this.parcelStatus = status;
      },
      error: (error) => {
        this.errorMessage = error;
      }
    });
  }
}
