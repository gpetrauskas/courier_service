import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { map, take, switchMap, filter } from 'rxjs/operators';
import { of } from "rxjs";

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const requiredRole = route.data['role'];

  return authService.authChecked$.pipe(
    filter(checked => checked),
    take(1),
    switchMap(() => authService.isAuthenticated$.pipe(take(1))),
    switchMap(isAuthenticated => {
      if (!isAuthenticated) {
        router.navigate(['/login']);
        return of(false);
      }

      return authService.getRole().pipe(
        map(userRole => {
          if (userRole !== requiredRole) {
            router.navigate(['/']);
            return false;
          }

          return true;
        })
      );
    })
  );
};
