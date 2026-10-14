import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'dateLabel',
  standalone: true
})

export class DateLabelPipe implements PipeTransform {
  private readonly units = {"day": 86400, "hour": 3600, "minute": 60,};

  transform(date: string): string {
    if (date == null) {
      return "";
    }

    const convertedDate = new Date(date);

    const currentDate = Date.now();
    const result = (currentDate - convertedDate.getTime()) / 1000;

    if (result < 60) return "Just now";
    if (result >= 3 * 86400) return convertedDate.toLocaleString();
    for (const [name, seconds] of Object.entries(this.units)) {
      if (result >= seconds) {
        const count = Math.floor(result / seconds);
        const s = "s ago";
        return count + " " + name + ((count > 1) ? s : " ago");
      }
    }
    return "";
  }
}
