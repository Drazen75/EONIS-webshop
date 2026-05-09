import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-checkout-cancel',
  templateUrl: './checkout-cancel.component.html',
  styleUrls: ['./checkout-cancel.component.scss']
})
export class CheckoutCancelComponent implements OnInit {
  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    // If the user came back from Stripe without paying, roll back the
    // pending order so its stock is returned to inventory.
    const idStr = sessionStorage.getItem('pendingOrderId');
    if (idStr) {
      const id = Number(idStr);
      // Always clear, regardless of cancel call result
      sessionStorage.removeItem('pendingOrderId');
      if (!Number.isNaN(id)) {
        this.orderService.cancelOrder(id).subscribe({
          // Idempotent on the backend — silent both on success and on error
          // (e.g. if the order is already CANCELLED or PAID).
          error: () => {}
        });
      }
    }
  }
}
