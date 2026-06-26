import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AlertBannerComponent } from "../../alert-banner/alert-banner.component";

@Component({
  selector: 'app-manage-delivery-option',
  standalone: true,
  imports: [RouterModule, MatToolbarModule, MatSidenavModule, AlertBannerComponent],
  templateUrl: './manage-delivery-option.component.html',
  styleUrl: './manage-delivery-option.component.css'
})
export class ManageDeliveryOptionComponent {


}
