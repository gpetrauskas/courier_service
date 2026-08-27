import { Component } from '@angular/core';
import { ActivityLogComponent } from "../activity-log/activity-log.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ActivityLogComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  constructor() {}


}
