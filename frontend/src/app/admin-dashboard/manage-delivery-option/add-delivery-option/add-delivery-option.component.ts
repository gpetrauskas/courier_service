import { Component } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { DeliveryOptionsService } from '../../../service/delivery-options.service';
import { CreateDeliveryOption } from '../../../models/delivery-option/create-delivery-option.dto';
import { Router } from '@angular/router';
import { ErrorHandlerService } from "../../../service/error-handler.service";

@Component({
  selector: 'app-add-delivery-option',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './add-delivery-option.component.html',
  styleUrl: './add-delivery-option.component.css'
})
export class AddDeliveryOptionComponent {
  constructor(
    private deliveryOptionsService: DeliveryOptionsService,
    private router: Router,
    private alertService: ErrorHandlerService
  ) {}

  alert$ = this.alertService.alert$;

  newOption = new FormGroup({
    name: new FormControl('', [Validators.required, Validators.minLength(3)]),
    description: new FormControl('', [Validators.required, Validators.maxLength(100)]),
    price: new FormControl('', [Validators.required, Validators.pattern('^[0-9]+(\\.[0-9]{1,2})?$')]),
  });

  onSubmit() {
    if (this.newOption.valid) {
      const createDeliveryOpt: CreateDeliveryOption = this.newOption.value as CreateDeliveryOption;
      this.alertService.handleRequest(this.deliveryOptionsService.add(createDeliveryOpt), "Successfully added",
        () => this.cancel()
      );
    }
  }

  cancel() {
    this.newOption.reset();
    this.router.navigate(['admin-dashboard/manage-delivery-option/all-delivery-options/']);
  }
}
