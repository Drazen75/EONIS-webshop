import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.scss']
})
export class OrderDetailComponent implements OnInit {
  order: Order | null = null;
  loading = true;

  statusColors: Record<string, string> = {
    PENDING: '#ff8f00', PAID: '#1565c0', PROCESSING: '#6a1b9a',
    SHIPPED: '#00838f', DELIVERED: '#2e7d32', CANCELLED: '#c62828', FAILED: '#b71c1c'
  };

  constructor(private route: ActivatedRoute, private router: Router, private orderService: OrderService) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.orderService.getMyOrder(id).subscribe({
      next: o => { this.order = o; this.loading = false; },
      error: () => { this.loading = false; this.router.navigate(['/orders']); }
    });
  }

  statusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'Na čekanju', PAID: 'Plaćeno', PROCESSING: 'U obradi',
      SHIPPED: 'Poslato', DELIVERED: 'Dostavljeno', CANCELLED: 'Otkazano', FAILED: 'Neuspešno'
    };
    return map[status] ?? status;
  }
}
