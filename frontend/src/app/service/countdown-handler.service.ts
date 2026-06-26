import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class CountdownHandlerService {
  constructor(private router: Router) { }
  private intervalRef: any;

  private countdownBehavioralSubject = new BehaviorSubject<CountdownFields>({
    message: '',
    timer: 5,
    active: false
  });

  countdown$ = this.countdownBehavioralSubject.asObservable();

  handleNavigation(time: number, message: string, navigateTo: string) {
    clearInterval(this.intervalRef);
    this.countdownBehavioralSubject.next({ message: message, timer: time, active: true });
    let timer: number = this.countdownBehavioralSubject.value.timer;

    this.intervalRef = setInterval(() => {
      timer--;
      this.countdownBehavioralSubject.next({ message: message, timer: timer, active: true });
      if (timer <= 0) {
        clearInterval(this.intervalRef);
        this.countdownBehavioralSubject.next({ message: '', timer: 5, active: false });
        this.router.navigate([navigateTo]);
      }
    }, 1000);
  }
}

export interface CountdownFields {
  message: string;
  timer: number;
  active: boolean;
}
