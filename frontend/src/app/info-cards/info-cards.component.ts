import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-info-cards',
  standalone: true,
  imports: [],
  templateUrl: './info-cards.component.html',
  styleUrl: './info-cards.component.css'
})
export class InfoCardsComponent {

  @Input() label: string = "";
  @Input() value: number | undefined = 0;
  @Input() color: string = "";
}
