import { Component, signal, OnInit } from '@angular/core';
import { AddressService } from "../../../service/person/address.service";
import { PersonEditDTO } from '../../../models/person/person-edit.dto';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatSelectModule, MatSelectChange } from '@angular/material/select';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormControl, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatInputModule } from '@angular/material/input';
import { UserService } from "../../../service/person/user.service";
import { Address } from "../../../models/address/address.model";
import { MyInfo } from "../../../models/person/my-info.model";
import { AuthService } from "../../../auth/auth.service";
import { MyInfoAdmin } from "../../../models/person/my-info-admin.model";
import { MyInfoCourier } from "../../../models/person/my-info-courier.model";
import { MyInfoUser } from "../../../models/person/my-info-user.model";
import { ErrorHandlerService } from "../../../service/error-handler.service";

@Component({
  selector: 'app-edit-info',
  standalone: true,
  imports: [
    MatCardModule,
    MatDividerModule,
    ReactiveFormsModule,
    MatListModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatSlideToggleModule,
    FormsModule,
    MatButtonModule
  ],
  templateUrl: './edit-info.component.html',
  styleUrl: './edit-info.component.css'
})
export class EditInfoComponent implements OnInit {
  role: string | null = null;
  changeTriggered: boolean = false;
  myInfo: MyInfoUser | MyInfoCourier | MyInfoAdmin | null = null;
  addresses: Address[] | null = null;
  readonly phone = new FormControl('', [Validators.required, Validators.pattern(/^[0-9]{8}$/)]);
  selectedAddressId: number | null = null;
  subscribed: boolean | undefined = undefined;
  errorMessage = signal('');

  constructor(
    private userService: UserService,
    private addressService: AddressService,
    private authService: AuthService,
    private errorHandler: ErrorHandlerService
  ) {}


  ngOnInit() {
    this.role = this.authService.getRoleValue();
    if (this.role === null ) this.authService.checkAuthToken();

    this.userService.me().subscribe({
      next: (data) => {
        this.myInfo = this.getRoleModelByRole(data);
        if (this.role === "USER") {
          this.phone.setValue(this.getUser().phoneNumber);
          this.subscribed = this.getUser().subscribed;
        }
      }
    })
  }

  private getRoleModelByRole(data: MyInfo) {
    switch (this.role) {
      case "USER": return data as MyInfoUser;
      case "COURIER": return data as MyInfoCourier;
      case "ADMIN": return data as MyInfoAdmin;
      default: return null;
    }
  }

  getUser() {
    return this.myInfo as MyInfoUser;
  }

  save() {
    if (!this.getUser() || this.phone.invalid) return;

    const updatedData: PersonEditDTO = {};

    const adr = this.addresses?.find(a => a.id === this.selectedAddressId);
    const userDefAddress = this.getUser().defaultAddress;

    if (adr && (!userDefAddress || !userDefAddress.includes(adr.street))) {
      updatedData.defaultAddressId = this.selectedAddressId;
    }

    if (this.phone.value !== this.getUser().phoneNumber?.replace(/^\+?370/, '')) {
      updatedData.phoneNumber = this.phone.value;
    }

    if (this.subscribed !== this.getUser().subscribed) {
      updatedData.subscribed = this.subscribed;
    }

    if (Object.keys(updatedData).length === 0) {
      return;
    }

    this.errorHandler.handleRequest(this.userService.updateMyInfo(updatedData), "Info successfully updated");
  }

  cancelChange() {
    this.changeTriggered = false;
    this.selectedAddressId = null;
    this.addresses = null;
  }

  fetchAddresses() {
    this.addressService.fetchMySavedAddress().subscribe({
      next: (data) => {
        this.addresses = data;
        this.changeTriggered = true;
      }
    });
  }

  onAddressSelect(event: MatSelectChange) {
    this.selectedAddressId = event.value;
  }

  updateErrorMessage() {
    if (this.phone.hasError('required')) {
      this.errorMessage.set('You must enter a value');
    } else if (this.phone.invalid) {
      this.errorMessage.set('Not a valid number');
    } else {
      this.errorMessage.set('');
    }
  }
}
