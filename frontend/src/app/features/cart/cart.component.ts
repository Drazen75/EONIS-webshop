import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormControl, Validators } from '@angular/forms';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { AuthService } from '../../core/services/auth.service';
import { Cart } from '../../core/models/cart.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {
  cart: Cart | null = null;
  loading = true;
  checkingOut = false;
  addressPrefilled = false;
  shippingCtrl = new FormControl('', Validators.required);

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router,
    private snack: MatSnackBar
  ) {}

  ngOnInit(): void {
    forkJoin({
      cart:    this.cartService.load(),
      profile: this.authService.getMe()
    }).subscribe({
      next: ({ cart, profile }) => {
        this.cart = cart;
        if (profile.address?.trim()) {
          this.shippingCtrl.setValue(profile.address.trim());
          this.addressPrefilled = true;
        }
        this.loading = false;
      },
      error: () => {
        // Ako profil ne uspe, učitaj samo korpu
        this.cartService.load().subscribe({
          next: c => { this.cart = c; this.loading = false; },
          error: () => this.loading = false
        });
      }
    });
  }

  updateQty(productId: number, quantity: number): void {
    this.cartService.updateItem(productId, quantity).subscribe({
      next: c => this.cart = c,
      error: err => this.snack.open(err.error?.message || 'Greška', 'OK', { duration: 3000, panelClass: 'error-snack' })
    });
  }

  removeItem(productId: number): void {
    this.cartService.removeItem(productId).subscribe(c => this.cart = c);
  }

  clearCart(): void {
    this.cartService.clear().subscribe(() => { this.cart = null; });
  }

  checkout(): void {
    if (!this.shippingCtrl.value?.trim()) {
      this.shippingCtrl.markAsTouched();
      return;
    }
    this.checkingOut = true;
    this.orderService.checkout(this.shippingCtrl.value.trim()).subscribe({
      next: res => {
        window.location.href = res.checkoutUrl;
      },
      error: err => {
        this.checkingOut = false;
        this.snack.open(err.error?.message || 'Greška pri plaćanju', 'OK', { duration: 4000, panelClass: 'error-snack' });
      }
    });
  }
}
