import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'enumLabel',
  standalone: true
})
export class EnumLabelPipe implements PipeTransform {

  transform(value: string): string {
    if (!value) return '';
    const formatted = value.toLowerCase().replace(/_/g, ' ');

    return formatted.split(' ').map(w => w[0].toUpperCase() + w.slice(1)).join(' ');
  }
}
