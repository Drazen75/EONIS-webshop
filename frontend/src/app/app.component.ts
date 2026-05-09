import { Component, OnInit } from '@angular/core';
import { ThemeService } from './core/services/theme.service';
import { OrderService } from './core/services/order.service';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  constructor(
    public theme: ThemeService,
    private orderService: OrderService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.theme.init();

    // If user came back from Stripe by pressing the browser BACK button (or
    // closed the Stripe tab and re-entered the app), the cancel page never
    // mounted, so we have to roll the pending order back here. The check
    // covers full reload navigation; the pageshow listener covers bfcache
    // (forward/back) restorations where Angular wouldn't re-init.
    this.rollbackPendingOrderIfNeeded();
    window.addEventListener('pageshow', () => this.rollbackPendingOrderIfNeeded());
  }

  private rollbackPendingOrderIfNeeded(): void {
    const pendingOrderId = sessionStorage.getItem('pendingOrderId');
    if (!pendingOrderId) return;

    // Skip on the success page — payment went through; the success component
    // clears the marker on its own.
    if (window.location.pathname.startsWith('/checkout/success')) return;

    // Clear immediately so we don't loop on transient errors / re-entries.
    sessionStorage.removeItem('pendingOrderId');

    // Only call when authenticated; cancel is a JWT-protected endpoint.
    if (!this.authService.isLoggedIn()) return;

    const id = Number(pendingOrderId);
    if (Number.isNaN(id)) return;

    // Idempotent on backend — silent on any outcome.
    this.orderService.cancelOrder(id).subscribe({ error: () => {} });
  }
}
