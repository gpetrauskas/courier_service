import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PaymentMethodService } from '../service/person/payment-method.service';
import { PaymentService } from '../service/payment.service';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { PaymentInfo } from "../models/payment/payment-info.model";
import { PaymentMethod } from "../models/person/payment-method.model";
import { CreditCard } from "../models/person/credit-card.model";
import { PaymentRequest } from "../models/payment/payment-request.model";
import { NewCreditCardRequest } from "../models/person/payment-method/new-credit-card-request.model";
import { ErrorHandlerService } from "../service/error-handler.service";

  @Component({
    selector: 'app-payment-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './payment-form.component.html',
    styleUrl: './payment-form.component.css'
  })
  export class PaymentFormComponent implements OnInit {
    @Input() paymentInfo: PaymentInfo | null = null;
    @Input() orderId!: number;
    paymentForm!: FormGroup;
    savedPaymentMethods: PaymentMethod[] = [];
    showNewCardFields = false;

  constructor(
    private fb: FormBuilder,
    private paymentService: PaymentService,
    private paymentMethodService: PaymentMethodService,
    private router: Router,
    private errorHandler: ErrorHandlerService
  ) {}

  ngOnInit(): void {
    this.paymentForm = this.fb.group({
      paymentMethod: ['new', Validators.required],
      savedPaymentMethodId: [null],
      cardHolderName: [''],
      cardNumber: [''],
      expiryDate: [''],
      cvc: ['', Validators.required],
      saveCard: [false]
    });

    this.loadSavedPaymentMethods();
  }

  loadSavedPaymentMethods(): void {
    this.paymentMethodService.fetchUserSavedPaymentMethods().subscribe({
      next: (response: PaymentMethod[]) => {
        this.savedPaymentMethods = response;
        if (this.savedPaymentMethods.length > 0) {
          this.paymentForm.get('paymentMethod')?.setValue('saved');
          this.showNewCardFields = false;
          this.paymentForm.get("savedPaymentMethodId")?.setValue(this.savedPaymentMethods[0].id);
          this.onPaymentMethodChange('saved');
        } else {
          this.paymentForm.get('paymentMethod')?.setValue('new');
          this.showNewCardFields = true;
          this.onPaymentMethodChange('new');
        }
      }
    });
  }

  onPaymentMethodChange(method: string): void {
    this.showNewCardFields = method === 'new';

    if (method === 'new') {
      this.paymentForm.get('cardHolderName')?.setValidators([Validators.required]);
      this.paymentForm.get('cardNumber')?.setValidators([Validators.required, Validators.pattern(/^\d{16}$/)]);
      this.paymentForm.get('expiryDate')?.setValidators([Validators.required, Validators.pattern(/^\d{2}\/\d{2}$/)]);
      this.paymentForm.get('saveCard')?.setValidators([]);
    } else {
      this.paymentForm.get('cardHolderName')?.clearValidators();
      this.paymentForm.get('cardNumber')?.clearValidators();
      this.paymentForm.get('expiryDate')?.clearValidators();
      this.paymentForm.get('saveCard')?.clearValidators();
    }

    this.paymentForm.get('cardHolderName')?.updateValueAndValidity();
    this.paymentForm.get('cardNumber')?.updateValueAndValidity();
    this.paymentForm.get('expiryDate')?.updateValueAndValidity();
  }

  isCreditCard(method: PaymentMethod) {
    return method.type === 'CREDIT_CARD';
  }

  asCreditCard(method: PaymentMethod) {
    return method as CreditCard;
  }

  onSubmit(): void {
    if (this.paymentForm.valid) {
      const formValue = this.paymentForm.value;

      if (formValue.paymentMethod === 'saved' && !formValue.savedPaymentMethodId) {
        this.paymentForm.get('savedPaymentMethodId')?.setErrors({ required: true });
        this.paymentForm.get('savedPaymentMethodId')?.markAsTouched();
        return;
      }

      let paymentRequest: PaymentRequest;
      if (formValue.paymentMethod === 'saved') {
        paymentRequest = {
          paymentMethodId: formValue.savedPaymentMethodId,
          cvc: formValue.cvc
        };
      } else {
        paymentRequest = {
          newPaymentMethod: {
            type: 'creditCard',
            cardNumber: formValue.cardNumber,
            expiryDate: formValue.expiryDate,
            cardHolderName: formValue.cardHolderName,
            saveCard: formValue.saveCard
          } as NewCreditCardRequest,
          cvc: formValue.cvc,
        };
      }

      this.paymentService.processPayment(this.orderId, paymentRequest).subscribe({
        next: (response: any) => {
          if (response.success === true && this.orderId) {
            void this.router.navigate(['/dashboard/user-orders', this.orderId, 'success']);
          } else {
            this.errorHandler.showError(response.failureReason);
          }
        }
      });
    }
  }
}
