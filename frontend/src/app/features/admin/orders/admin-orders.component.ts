import { Component, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-admin-orders',
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.scss']
})
export class AdminOrdersComponent implements OnInit {
  orders: Order[] = [];
  total = 0;
  page = 0;
  pageSize = 20;
  loading = false;
  selectedStatus = '';
  searchCtrl = new FormControl('');
  displayedColumns = ['id', 'customer', 'status', 'total', 'items', 'date', 'actions'];

  statuses = ['', 'PENDING', 'PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'FAILED'];
  statusLabels: Record<string, string> = {
    '': 'Sve', PENDING: 'Na čekanju', PAID: 'Plaćeno', PROCESSING: 'U obradi',
    SHIPPED: 'Poslato', DELIVERED: 'Dostavljeno', CANCELLED: 'Otkazano', FAILED: 'Neuspešno'
  };
  statusColors: Record<string, string> = {
    PENDING: '#ff8f00', PAID: '#1565c0', PROCESSING: '#6a1b9a',
    SHIPPED: '#00838f', DELIVERED: '#2e7d32', CANCELLED: '#c62828', FAILED: '#b71c1c'
  };

  constructor(private orderService: OrderService, private snack: MatSnackBar) {}

  ngOnInit(): void {
    this.load();
    this.searchCtrl.valueChanges.pipe(debounceTime(400), distinctUntilChanged())
      .subscribe(() => { this.page = 0; this.load(); });
  }

  load(): void {
    this.loading = true;
    this.orderService.getAllOrders({
      status: this.selectedStatus || undefined,
      search: this.searchCtrl.value || undefined,
      page: this.page, size: this.pageSize
    }).subscribe({
      next: res => { this.orders = res.content; this.total = res.totalElements; this.loading = false; },
      error: () => this.loading = false
    });
  }

  onPage(e: PageEvent): void { this.page = e.pageIndex; this.pageSize = e.pageSize; this.load(); }

  updateStatus(order: Order, status: string): void {
    this.orderService.updateOrderStatus(order.id, status).subscribe({
      next: updated => {
        order.status = updated.status;
        this.snack.open('Status ažuriran', 'OK', { duration: 2000, panelClass: 'success-snack' });
      },
      error: () => this.snack.open('Greška', 'OK', { duration: 2000, panelClass: 'error-snack' })
    });
  }
}
