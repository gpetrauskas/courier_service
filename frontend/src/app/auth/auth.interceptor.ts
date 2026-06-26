import { HttpInterceptorFn, HttpStatusCode } from '@angular/common/http';
import { catchError } from "rxjs/operators";
import { mergeMap, throwError } from "rxjs";
import { inject } from "@angular/core";
import { AuthService} from "./auth.service";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  const authReq = req.clone({
    withCredentials: true
  });
  return next(authReq).pipe(
    catchError((err) => {
      if (err.status === HttpStatusCode.Unauthorized) {
        return authService.refresh().pipe(
          mergeMap(() => next(authReq)),
          catchError(() => authService.logout())
        )
      } else {
        return throwError(() => err);
      }
    })
  )
}
