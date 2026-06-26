import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, FormGroup, Validators, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { DeliveryOptionsService } from '../../service/delivery-options.service';
import { DeliveryOption } from '../../models/delivery-option/delivery-option.model';
import { PackageDetails } from '../../models/order/package-details.model';

@Component({
  selector: 'app-delivery-options',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './delivery-options.component.html',
  styleUrls: ['./delivery-options.component.css']
})
export class DeliveryOptionsComponent implements OnInit {
  @Input() parcelDetails: PackageDetails = { weightId: null, dimensionsId: null, contents: '' };
  @Input() preferenceId: number | null = null;
  @Input() selectedWeightOption?: DeliveryOption;
  @Input() selectedSizeOption?: DeliveryOption;
  @Input() selectedPreferenceOption?: DeliveryOption;

  @Output() senderAddressButtonClicked = new EventEmitter<{
    parcelDetails: PackageDetails;
    preferenceId: number | null;
    selectedWeightOption?: DeliveryOption;
    selectedSizeOption?: DeliveryOption;
    selectedPreferenceOption?: DeliveryOption;
  }>();

  deliveryOptionsForm: FormGroup;
  weightOptions: DeliveryOption[] = [];
  sizeOptions: DeliveryOption[] = [];
  preferenceOptions: DeliveryOption[] = [];
  description: string = '';

  constructor(private deliveryService: DeliveryOptionsService, private fb: FormBuilder) {
    this.deliveryOptionsForm = this.fb.group({
      weight: [null, Validators.required],
      size: [null, Validators.required],
      description: [''],
      preference: [null, Validators.required]
    });
  }

  ngOnInit() {
    this.deliveryService.getDeliveryOptions().subscribe(
      (options) => {
        this.weightOptions = options.WEIGHT;
        this.sizeOptions = options.SIZE;
        this.preferenceOptions = options.PREFERENCE;

        this.updateFormValues();
      }
    );
  }

  updateFormValues() {
      this.deliveryOptionsForm.patchValue({
      weight: this.weightOptions.find(option => option.id === this.parcelDetails.weightId) || null,
      size: this.sizeOptions.find(option => option.id === this.parcelDetails.dimensionsId) || null,
      description: this.parcelDetails.contents || '',
      preference: this.preferenceOptions.find(option => option.id === this.preferenceId) || null,
    });
  }

  enterSenderAddress() {
    if (this.deliveryOptionsForm.valid) {
      const formValues = this.deliveryOptionsForm.value;

      this.senderAddressButtonClicked.emit({
        parcelDetails: {
          weightId: formValues.weight?.id || '',
          dimensionsId: formValues.size?.id || '',
          contents: formValues.description
        },
        preferenceId: formValues.preference?.id || '',
        selectedWeightOption: formValues.weight,
        selectedSizeOption: formValues.size,
        selectedPreferenceOption: formValues.preference
      });
    } else {
      this.deliveryOptionsForm.markAllAsTouched();
    }
  }
}
