import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { PersonDetailsDTO } from '../../../models/person/person-details.model';
import { PersonService } from '../../../service/person/person.service';
import { MatToolbar } from "@angular/material/toolbar";
import { MatCard } from "@angular/material/card";
import { AdminPersonUpdate } from "../../../models/person/admin-person-update.dto";
import { AppFieldErrorComponent } from "../../../shared/app-field-error/app-field-error.component";
import { Subject, takeUntil } from "rxjs";
import { ErrorHandlerService } from "../../../service/error-handler.service";

const editableFields: (keyof AdminPersonUpdate)[] = ['name', 'email', 'phoneNumber'];

@Component({
  selector: 'app-user-details',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatToolbar, MatCard, AppFieldErrorComponent],
  templateUrl: './user-details.component.html',
  styleUrl: './user-details.component.css'
})
export class UserDetailsComponent implements OnInit {
  profileForm = new FormGroup({
    name: new FormControl('', [Validators.required, Validators.pattern(/^[A-Za-z\s]+$/)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    phoneNumber: new FormControl('', Validators.pattern(/^\+?[0-9]{7}$/))
  });
  originalUser: PersonDetailsDTO | null = null;
  user: PersonDetailsDTO | null = null;
  isEditMode: boolean = false;
  personId: number | null = null;

  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private personServiceImpl: PersonService,
    private errorHandler: ErrorHandlerService
  ) {}

  ngOnInit() {
    this.personId =  Number(this.route.snapshot.paramMap.get('id'));
    this.loadPerson(this.personId);

    this.route.queryParams
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        this.isEditMode = params['edit'] === 'true';
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadPerson(id: number) {
    this.personServiceImpl.findPersonById(id).subscribe(
      user =>  {
        this.user = user;
        this.originalUser = structuredClone(user);
        this.profileForm.patchValue(this.user);
      }
    );
  }

  cancelEditMode() {
    this.toggleEditMode();
    this.revertChanges();
  }

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
  }

  private canSave(): boolean {
    return !!this.user && !!this.originalUser && this.profileForm.valid;
  }

  private syncFormToUser() {
    Object.assign(this.user!, this.profileForm.value);
  }

  private buildUpdatedPayload(): AdminPersonUpdate | null {
    const update: AdminPersonUpdate = {};

    for (const field of editableFields) {
      if (this.user![field] !== this.originalUser![field]) {
        update[field] = this.user![field] ?? undefined;
      }
    }

    return Object.keys(update).length ? update : null;
  }

  private revertChanges() {
    this.user = structuredClone(this.originalUser);
    this.profileForm.patchValue(this.originalUser!);
  }

  private persistUpdate(update: AdminPersonUpdate) {
    this.errorHandler.handleRequest(this.personServiceImpl.updatePersonDetails(this.personId!, update), "Successfully updated",
      () => {
      this.originalUser = structuredClone(this.user);
      this.profileForm.patchValue(this.user!);
      this.toggleEditMode();
      },
      () => {
      this.cancelEditMode();
      }
    );
  }

  saveUser() {
    if (!this.canSave()) {
      this.profileForm.markAllAsTouched();
      return;
    }
    this.syncFormToUser();

    const update = this.buildUpdatedPayload();
    if (!update) return;

    this.persistUpdate(update);
  }

  deleteUser() {
    //logic
  }


}
