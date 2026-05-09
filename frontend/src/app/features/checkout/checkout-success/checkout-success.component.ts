import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CartService } from '../../../core/services/cart.service';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-checkout-success',
  templateUrl: './checkout-success.component.html',
  styleUrls: ['./checkout-success.component.scss']
})
export class CheckoutSuccessComponent implements OnInit {
  sessionId: string | null = null;
  order: Order | null = null;
  loading = true;
  // Webhook can arrive slightly after redirect — retry a few times
  private pollAttempts = 0;
  private readonly MAX_POLL = 6;

  constructor(
    private route: ActivatedRoute,
    private cartService: CartService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.sessionId = this.route.snapshot.queryParamMap.get('session_id');

    // Payment succeeded — discard the pending-order marker so /checkout/cancel
    // wouldn't attempt to roll back this order if visited later.
    sessionStorage.removeItem('pendingOrderId');

    // Clear cart on both frontend state and backend
    this.cartService.clear().subscribe({ error: () => this.cartService.reset() });

    if (this.sessionId) {
      this.loadOrder();
    } else {
      this.loading = false;
    }
  }

  private loadOrder(): void {
    this.orderService.getOrderBySession(this.sessionId!).subscribe({
      next: order => {
        this.order = order;
        this.loading = false;
        // If webhook hasn't fired yet the order is still PENDING — poll briefly
        if (order.status === 'PENDING' && this.pollAttempts < this.MAX_POLL) {
          this.pollAttempts++;
          setTimeout(() => this.loadOrder(), 2000);
        }
      },
      error: () => { this.loading = false; }
    });
  }

  formatPrice(amount: number): string {
    return new Intl.NumberFormat('sr-RS', {
      style: 'currency', currency: 'RSD', maximumFractionDigits: 0
    }).format(amount);
  }
}
