import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { map, take } from 'rxjs/operators';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isAuthenticated$.pipe(
    take(1),
    map(isAuthenticated => {
      const isLoginRoute = state.url.includes('login');

      if (isAuthenticated && isLoginRoute) {
        router.navigate(['/']);
        return false;
      } else if (!isAuthenticated && !isLoginRoute) {
        router.navigate(['/login']);
        return false;
      }
      return true;
    })
  )
};
