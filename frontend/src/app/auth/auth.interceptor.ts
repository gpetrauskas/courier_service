import { HttpInterceptorFn, HttpStatusCode } from '@angular/common/http';
import { catchError } from "rxjs/operators";
import { mergeMap, throwError } from "rxjs";
import { inject } from "@angular/core";
import { AuthService} from "./auth.service";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  const token = document.cookie.split("; ").find(c => c.startsWith('XSRF-TOKEN'))?.split('=')[1];

  console.log("yra csrf tokenas? " + token);

  const allowCalls = ['POST', "PUT", "DELETE", 'PATCH'].includes(req.method);

  console.log("allowed calls? " + allowCalls);

  const authReq = req.clone({
    withCredentials: true,
    setHeaders: allowCalls && token ? { 'X-XSRF-TOKEN': token } : {}
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
