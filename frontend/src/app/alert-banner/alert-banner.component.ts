import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ErrorHandlerService } from "../service/error-handler.service";
import { Subject, takeUntil } from "rxjs";

@Component({
  selector: 'app-alert-banner',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alert-banner.component.html',
  styleUrl: './alert-banner.component.css'
})
export class AlertBannerComponent implements OnInit, OnDestroy {
  constructor(private errorHandler: ErrorHandlerService) {
  }

  private timeoutRef: any;
  alert$ = this.errorHandler.alert$;
  destroy$ = new Subject<void>();

  ngOnInit() {

    this.alert$.pipe(takeUntil(this.destroy$)).subscribe({
    next: (data) => {
      if (data.message.length) {
        clearTimeout(this.timeoutRef);
        this.timeoutRef = setTimeout(() => this.errorHandler.clearAlert(), 3000);
      }
    }
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
