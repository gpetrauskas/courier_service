import { Component, EventEmitter, Output, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PageEventModel } from "../../models/page-event.model";

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.css'
})
export class PaginationComponent {
  @Input() currentPage: number = 0;
  @Input() totalPages: number = 0;
  @Input() totalItems: number = 0;
  @Input() pageSize: number = 10;
  inputPageSize: number = this.pageSize;

  @Output() pageChange = new EventEmitter<PageEventModel>();

  onPageSizeChange() {
    const s = Number(this.inputPageSize);
    console.log('testSize called', s)
    if (!isNaN(s) && s > 0 && s <= this.totalItems) {
      const e: PageEventModel = {
        pageIndex: 0,
        pageSize: s
      };
      this.pageChange.emit(e);
    } else {
      this.inputPageSize = 1;
    }
  }

  previousPage() {
    if (this.currentPage > 0) {

      const e: PageEventModel = {
        pageIndex: this.currentPage - 1,
        pageSize: this.pageSize
      };
      this.pageChange.emit(e);
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      console.log('testNext called', this.currentPage)
      const e: PageEventModel = {
        pageIndex: this.currentPage + 1,
        pageSize: this.pageSize
      };
      this.pageChange.emit(e);
    }
  }
}
