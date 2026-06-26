import { Component, OnDestroy, OnInit, viewChild } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule, AsyncPipe } from '@angular/common';
import { AuthService } from '../auth/auth.service';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTabsModule } from '@angular/material/tabs';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule, MatAccordion } from '@angular/material/expansion';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MenuItem, Role } from '../models/menu-item.model';
import { MenuService } from '../service/menu.service';
import { MatBadgeModule } from '@angular/material/badge';
import { NotificationService } from '../service/notification.service';
import { Subject, takeUntil } from "rxjs";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, MatToolbarModule, MatSidenavModule,
    MatIconModule, MatExpansionModule, RouterModule, MatTabsModule, MatListModule, MatBadgeModule,
    MatMenuModule, MatMenuTrigger, AsyncPipe],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  accordion = viewChild.required(MatAccordion);
  isAuthenticated = false;
  userRole: string | null = null;
  menuItems: MenuItem[] = [];
  notificationsTrigger = viewChild.required<MatMenuTrigger>('notificationsTrigger');

  constructor(
    private router: Router,
    private authService: AuthService,
    private menuService: MenuService,
    public notificationService: NotificationService
    ) {}

  ngOnInit(): void {
    this.authService.isAuthenticated$
      .pipe(takeUntil(this.destroy$))
      .subscribe(isAuthenticated => {
      this.isAuthenticated = isAuthenticated;
    });

    this.authService.userRole$
      .pipe(takeUntil(this.destroy$))
      .subscribe(role => {
      this.userRole = role;
      if (role) {
        this.menuItems = this.menuService.getMenuItems(role as Role);
      }
    });
  }

  markAllAsRead() {
    this.notificationService.markAllAsRead();
  }

  viewAllNotifications(notificationId?: number) {
    this.notificationsTrigger().closeMenu();

    const queryParams = notificationId ? { highlight: notificationId } : {};
    this.router.navigate(['/shared/notifications'], { queryParams })
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => this.navigateTo('/')
    });
  }
}
