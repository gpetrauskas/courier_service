import { ReplaySubject } from "rxjs";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root"
})
export class MediaServiceService {
  private matches = new ReplaySubject<boolean>(1);
  isDesktop$ = this.matches.asObservable();

  constructor() {
    const mediaQueryList = window.matchMedia('(min-width: 768px)');
    const listener = (event: MediaQueryListEvent) => this.matches.next(event.matches);

    this.matches.next(mediaQueryList.matches);
    mediaQueryList.addEventListener('change', listener);
  }
}
