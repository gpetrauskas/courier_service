import { Component } from '@angular/core';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { NotificationTargetType } from '../../../enums/notification-target-type.enum';
import { NotificationService } from '../../../service/notification.service';
import { ErrorHandlerService } from "../../../service/error-handler.service";

@Component({
  selector: 'app-create',
  standalone: true,
  imports: [MatFormFieldModule, MatInputModule, MatSelectModule, FormsModule, CommonModule],
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent {
  targetTypes = [...Object.values(NotificationTargetType), 'SPECIFIC_USER'];
  selectedTarget: string = '';
  specificUserId: string = '';
  titleRequest: string = '';
  messageRequest: string = '';

  constructor(private notificationService: NotificationService, private errorHandlerService: ErrorHandlerService) {
  }

  create() {
    console.log('test ', this.selectedTarget);

    const notificationToSend = {
    title: this.titleRequest,
    message: this.messageRequest,
    target: this.selectedTarget === 'SPECIFIC_USER'
      ? {
        kind: 'INDIVIDUAL',
        personId: Number(this.specificUserId)
        }
      : {
          kind: 'BROADCAST',
          type: this.selectedTarget as NotificationTargetType
        }
    };

    this.errorHandlerService.handleRequest(this.notificationService.send(notificationToSend), "Notification successfully sent");
  }

}
