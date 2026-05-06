import { Component, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { OrderService } from '../../../core/services/order.service';
import { Transaction } from '../../../core/models/order.model';

@Component({
  selector: 'app-admin-transactions',
  templateUrl: './admin-transactions.component.html',
  styleUrls: ['./admin-transactions.component.scss']
})
export class AdminTransactionsComponent implements OnInit {
  transactions: Transaction[] = [];
  total = 0;
  page = 0;
  pageSize = 20;
  loading = false;
  searchCtrl = new FormControl('');
  displayedColumns = ['id', 'order', 'customer', 'product_info', 'amount', 'status', 'date'];

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.load();
    this.searchCtrl.valueChanges.pipe(debounceTime(400), distinctUntilChanged())
      .subscribe(() => { this.page = 0; this.load(); });
  }

  load(): void {
    this.loading = true;
    this.orderService.getTransactions({
      search: this.searchCtrl.value || undefined,
      page: this.page, size: this.pageSize
    }).subscribe({
      next: res => { this.transactions = res.content; this.total = res.totalElements; this.loading = false; },
      error: () => this.loading = false
    });
  }

  onPage(e: PageEvent): void { this.page = e.pageIndex; this.pageSize = e.pageSize; this.load(); }
}
