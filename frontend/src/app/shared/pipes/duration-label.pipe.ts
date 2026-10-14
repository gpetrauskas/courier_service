import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: "durationLabel",
  standalone: true
})

export class DurationLabelPipe implements PipeTransform {
  transform(raw: number): string {
    if (raw === null || raw === undefined) {
      return "no data";
    }

    console.log(raw);

      const days = Math.floor(raw / 60 / 24);
      console.log(days);

      const leftover = raw % (24 * 60);
      console.log(leftover);

      const hours = Math.floor(leftover / 60);
      console.log(hours);

      const minutes = Math.floor(leftover - (hours * 60));
      console.log(minutes);

    return (days > 0 ? days + "d" : "") +
            (hours > 0 ? " " + hours + "h" : "") +
            (minutes > 0 ? " " +minutes + "m" : "");
  }
}
