import { Injectable } from '@angular/core';
import { RxStomp, RxStompConfig } from "@stomp/rx-stomp";
import { NotificationService } from "./notification.service";
import { AuthService } from "../auth/auth.service";

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private rxStomp: RxStomp = new RxStomp();

  constructor(private notificationService: NotificationService, private auth: AuthService) {
  }

  create() {
    if (this.rxStomp?.active) {
      return;
    }

    this.rxStomp?.deactivate();

    this.rxStomp = new RxStomp();

    const config: RxStompConfig = {
      brokerURL: "ws://localhost:8080/portfolio",
      reconnectDelay: 2000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000
    };

    this.rxStomp.configure(config);
    this.rxStomp.activate();
    console.log(this.auth.getRoleValue())
    this.rxStomp.watch('/user/queue/notifications').subscribe(msg => {
      console.log(msg);
     this.notificationService.addIncomingNotification(JSON.parse(msg.body));
    });
    console.log(this.auth.getRoleValue())
    this.rxStomp.watch(`/topic/notifications/${this.auth.getRoleValue()}`).subscribe(msg => {
      console.log(msg);
      this.notificationService.addIncomingNotification(JSON.parse(msg.body));
    });

  }

  watchTicket(ticketId: number) {
    return this.rxStomp.watch(`/topic/tickets/` + ticketId);
  }

  deactivate() {
    this.rxStomp?.deactivate();
  }
}
