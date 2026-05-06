import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models/order.model';
import { PagedResponse } from '../../../core/models/product.model';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss']
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];
  total = 0;
  page = 0;
  pageSize = 10;
  loading = true;

  statusColors: Record<string, string> = {
    PENDING:    '#ff8f00',
    PAID:       '#1565c0',
    PROCESSING: '#6a1b9a',
    SHIPPED:    '#00838f',
    DELIVERED:  '#2e7d32',
    CANCELLED:  '#c62828',
    FAILED:     '#b71c1c'
  };

  constructor(private orderService: OrderService, private router: Router) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.orderService.getMyOrders(this.page, this.pageSize).subscribe({
      next: res => { this.orders = res.content; this.total = res.totalElements; this.loading = false; },
      error: () => this.loading = false
    });
  }

  onPage(e: any): void { this.page = e.pageIndex; this.pageSize = e.pageSize; this.load(); }
  viewOrder(id: number): void { this.router.navigate(['/orders', id]); }

  statusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'Na čekanju', PAID: 'Plaćeno', PROCESSING: 'U obradi',
      SHIPPED: 'Poslato', DELIVERED: 'Dostavljeno', CANCELLED: 'Otkazano', FAILED: 'Neuspešno'
    };
    return map[status] ?? status;
  }
}
