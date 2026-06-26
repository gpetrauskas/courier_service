import { Injectable } from '@angular/core';
import { HttpErrorResponse } from "@angular/common/http";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {
  constructor() { }

  private alertFieldsBehaviorSubject = new BehaviorSubject<AlertFields>({
    type: 'info',
    message: '',
    isLoading: false
  });

  alert$ = this.alertFieldsBehaviorSubject.asObservable();

  handleError(err: HttpErrorResponse) {
    return (err.status === 0) ? err.message : err.error.message;
  }

  handleRequest(obs: Observable<any>, message: string, onSuccess?: () => void, onFailure?: () => void) {
    this.alertFieldsBehaviorSubject.next({ isLoading: true, message: '', type: 'info' });

    obs.subscribe({
      next: () => {
        this.alertFieldsBehaviorSubject.next({ isLoading: false, message: message, type: 'success' });
        onSuccess?.();
      },
      error: (err) => {
        this.alertFieldsBehaviorSubject.next({ isLoading: false, message: this.handleError(err), type: 'error' });
        onFailure?.();
      }
    })
  }

  showSuccess(message: string) {
    this.alertFieldsBehaviorSubject.next({ isLoading: false, message: message, type: 'info' });
  }


  showError(err: HttpErrorResponse | string) {
    typeof err === 'string'
      ? this.alertFieldsBehaviorSubject.next({ isLoading: false, message: err, type: 'error' })
      : this.alertFieldsBehaviorSubject.next({ isLoading: false, message: this.handleError(err), type: 'error' });
  }

  clearAlert() {
    this.alertFieldsBehaviorSubject.next({ isLoading: false, message: '', type: 'info' })
  }
}

export interface AlertFields {
  type: 'error' | 'success' | 'info';
  message: string;
  isLoading: boolean;
}
