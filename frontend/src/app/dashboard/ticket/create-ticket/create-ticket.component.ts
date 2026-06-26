import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatStepperModule } from '@angular/material/stepper';
import { MatSelectModule } from '@angular/material/select';
import { TicketPriority } from '../../../enums/ticket-priority.enum';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatCardModule } from '@angular/material/card';
import { TicketService } from '../../../service/ticket.service';
import { TicketCreate } from '../../../models/ticket/ticket-create.dto';
import { CountdownHandlerService } from "../../../service/countdown-handler.service";
import { ErrorHandlerService } from "../../../service/error-handler.service";

@Component({
  selector: 'app-create-ticket',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatStepperModule,
    MatInputModule,
    MatFormFieldModule,
    MatButtonModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatCardModule
  ],
  templateUrl: './create-ticket.component.html',
  styleUrl: './create-ticket.component.css'
})
export class CreateTicketComponent {
  private formBuilder = inject(FormBuilder);
  priorities = Object.values(TicketPriority);
  countdown$ = this.countdownHandler.countdown$;

  coreForm: FormGroup = this.formBuilder.group({
    title: ['', [Validators.required, Validators.maxLength(100)]],
    description: ['', Validators.required]
  });

  configForm: FormGroup = this.formBuilder.group({
    priority: ['', Validators.required]
  });

  constructor(
    private ticketService: TicketService,
    private countdownHandler: CountdownHandlerService,
    private errorHandler: ErrorHandlerService
  ) {}

  submitTicket() {
    if (this.coreForm.invalid || this.configForm.invalid) {
      return;
    }

    const title = this.coreForm.get('title')?.value;
    const description = this.coreForm.get('description')?.value;
    const priority = this.configForm.get('priority')?.value;

    if (!title || !description || !priority) {
      return;
    }

    const newTicket: TicketCreate = { title, description, priority };

    this.errorHandler.handleRequest(this.ticketService.create(newTicket), "Successfully created ticket",
      () => {
      this.coreForm.reset();
      this.configForm.reset();
      this.countdownHandler.handleNavigation(
        3,
        "You will be redirected to 'My Tickets' page.",
        '/dashboard/ticket/my-tickets'
      )}
    );
  }
}
