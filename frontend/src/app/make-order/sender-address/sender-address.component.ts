import { Component, EventEmitter, Output, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, FormGroup, FormBuilder, Validators, ReactiveFormsModule, FormControl } from '@angular/forms';
import { Address } from '../../models/address/address.model';
import { AddressService } from '../../service/person/address.service';

@Component({
  selector: 'app-sender-address',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule, ReactiveFormsModule],
  templateUrl: './sender-address.component.html',
  styleUrl: './sender-address.component.css'
})
export class SenderAddressComponent {
  @Input() senderAddress: Address = {
    id: 0,
    street: '',
    houseNumber: '',
    flatNumber: '',
    postCode: '',
    city: '',
    name: '',
    phoneNumber: ''
  };
  savedAddresses: Address[] = [];
  selectedOption: string = '';
  addressForm: FormGroup;

  @Output() recipientAddressButtonClicked = new EventEmitter<{ senderAddress: Address, savedAddresses: Address[] }>();

  constructor(private addressCommandUseCase: AddressService, private fb: FormBuilder) {
    this.loadSavedAddresses();
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

  loadSavedAddresses() {
    this.addressCommandUseCase.fetchMySavedAddress().subscribe({
      next: (addresses) => this.savedAddresses = addresses,
      error: (error) => console.error('Error while fetching addresses', error)
    });
  }

  toggleSenderAddressForm(selectedAddressOption: string) {
    this.selectedOption = selectedAddressOption;
    if (this.selectedOption === 'new') {
      this.addressForm.reset();
    } else if (this.savedAddresses.length > 0) {
      const firstAddress = this.savedAddresses[0];
      this.senderAddress = firstAddress;
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

    this.senderAddress = selectedAddress;
    this.addressForm.patchValue(selectedAddress);
  }

  enterRecipientAddress() {
    if (!this.addressForm.valid) {
      this.addressForm.markAllAsTouched();
      return;
    }

    if (this.selectedOption === 'saved') {
      this.senderAddress = { ...this.senderAddress, ...this.addressForm.value };
    } else if (this.selectedOption === 'new') {
      this.senderAddress = this.addressForm.value;
    }

    this.recipientAddressButtonClicked.emit({
      senderAddress: this.senderAddress,
      savedAddresses: this.savedAddresses
    });
  }
}
