import { Component, OnInit } from '@angular/core';
import { AddressService } from '../../service/person/address.service';
import { CommonModule } from '@angular/common';
import { Address } from '../../models/address/address.model';
import { ReactiveFormsModule, Validators, FormBuilder, FormGroup } from '@angular/forms';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog.component';
import { ErrorHandlerService } from "../../service/error-handler.service";

@Component({
  selector: 'app-address',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ConfirmationDialogComponent],
  templateUrl: './address.component.html',
  styleUrl: './address.component.css'
})
export class AddressComponent implements OnInit {
  savedUserAddresses: Address[] = [];
  hasAddress: boolean = false;
  activeDropdownId: number | null = null;
  addressForm: FormGroup;
  alertMessage: string[] = [];
  alertType: 'success' | 'error' | 'info' = 'info';
  showConfirmDialog: boolean = false;
  addressToDeleteId: number | null = null;
  addressToDeleteName: string = '';

  constructor(private addressService: AddressService, private fb: FormBuilder, private errorHandler: ErrorHandlerService) {
    this.addressForm = this.fb.group({
      street: ['', Validators.required],
      houseNumber: ['', Validators.required],
      flatNumber: [''],
      postCode: ['', [Validators.required, Validators.pattern('^[0-5]{5}$')]],
      city: ['', Validators.required],
      name: ['', Validators.required],
      phoneNumber: ['', [Validators.required, Validators.pattern('^[0-8]{8}$')]]
    });
  }

  ngOnInit() {
    this.getUserAddresses();
  }

  getUserAddresses(): void {
    this.addressService.fetchMySavedAddress().subscribe({
      next: (response: Address[]) => {
        this.savedUserAddresses = response;
        this.hasAddress = true;
      },
      error: (error: any) => {
        this.hasAddress = false;
      }
    });
  }

  toggleAddressEdit(addressId: number) {
    this.activeDropdownId = this.activeDropdownId === addressId ? null : addressId;
    if (this.activeDropdownId !== null) {
      const address = this.savedUserAddresses.find(addr => addr.id === addressId);
      if (address) {
        this.addressForm.patchValue(address);
      }
    }
  }

  deleteAddress(addressId: number) {
    if (this.activeDropdownId !== null) {
      const address = this.savedUserAddresses.find(addr => addr.id === addressId);
      if (address) {
        this.errorHandler.handleRequest(this.addressService.deleteAddress(address.id), "Successfully deleted",
          () => {
            this.activeDropdownId = null;
            this.getUserAddresses();
          },
          () => {
            this.activeDropdownId = null;
          }
        )
      }
    }
  }

  saveAddress(addressId: number) {
    if (this.addressForm.valid) {
      const updatedAddress: Address = { id: addressId, ...this.addressForm.value };

      this.errorHandler.handleRequest(this.addressService.updateAddress(addressId, updatedAddress), "Successfully updated",
        () => {
        this.activeDropdownId = null;
        this.getUserAddresses();
        }
      );
    }
  }

  openConfirmDialog(addressId: number) {
    this.showConfirmDialog = true;
    this.addressToDeleteId = addressId;

    const address = this.savedUserAddresses.find(addr => addr.id === addressId);
    if (address) {
      this.addressToDeleteName = `${address.street} ${address.houseNumber}`;
    } else {
      this.addressToDeleteName = 'Unknown address';
    }
  }

  onDeleteConfirmed() {
    if (this.addressToDeleteId !== null) {
      this.deleteAddress(this.addressToDeleteId);
    }
    this.showConfirmDialog = false;
  }

  onDeleteCanceled() {
    this.showConfirmDialog = false;
  }
}
