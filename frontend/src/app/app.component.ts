import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './header/header.component';
import { NotificationService } from './service/notification.service';
import { AuthService } from './auth/auth.service';
import { AlertBannerComponent } from "./alert-banner/alert-banner.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, AlertBannerComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'gytis courier service';

  constructor(private authService: AuthService, private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.authService.checkAuthToken();
    this.authService.isAuthenticated$.subscribe(status => {
      if (status) {
        this.notificationService.initializeNotifications();
      } else {
        this.notificationService.clearNotifications();
      }
    });
  }
}
