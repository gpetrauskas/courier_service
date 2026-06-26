import { Component, EventEmitter, Output, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Address } from '../../models/address/address.model';

@Component({
  selector: 'app-recipient-address',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule, ReactiveFormsModule],
  templateUrl: './recipient-address.component.html',
  styleUrl: './recipient-address.component.css'
})
export class RecipientAddressComponent {
  @Input() recipientAddress: Address = {
    id: 0,
    street: '',
    houseNumber: '',
    flatNumber: '',
    postCode: '',
    city: '',
    name: '',
    phoneNumber: ''
  };
  @Input() savedAddresses: Address[] = [];
  selectedOption: string = '';
  addressForm: FormGroup;

  @Output() orderReviewButtonClicked = new EventEmitter<Address>();

  constructor(private fb: FormBuilder) {
    this.addressForm = this.fb.group({
      street: ['', Validators.required],
      houseNumber: ['', Validators.required],
      flatNumber: [''],
      postCode: ['', Validators.required],
      city: ['', Validators.required],
      name: ['', Validators.required],
      phoneNumber: ['', Validators.required]
    });
  }

  toggleRecipientAddressForm(selectedAddressOption: string) {
    this.selectedOption = selectedAddressOption;
    if (this.selectedOption === 'new') {
      this.addressForm.reset();
    } else if (this.savedAddresses.length > 0) {
      this.addressForm.reset();
      const firstAddress = this.savedAddresses[0];
      this.recipientAddress = firstAddress;
      this.addressForm.patchValue(firstAddress);
    }
  }

  onAddressSelect(event: Event) {
    const selectElement = event.target as HTMLSelectElement;
    if (!selectElement) {
      return;
    }

      const selectedAddressId = selectElement.value;
      const selectedAddress = this.savedAddresses.find(addr => addr.id.toString() === selectedAddressId);
      if (!selectedAddress) {
        return;
      }

      this.recipientAddress = selectedAddress;
      this.addressForm.patchValue(this.recipientAddress);
  }

  reviewOrder() {
    if (!this.addressForm.valid) {
      this.addressForm.markAllAsTouched();
      return;
    }

    if (this.selectedOption === 'saved') {
      this.recipientAddress = { ...this.recipientAddress, ...this.addressForm.value };
    } else if (this.selectedOption === 'new') {
      this.recipientAddress = this.addressForm.value;
    }

    this.orderReviewButtonClicked.emit(this.recipientAddress);
  }

}
