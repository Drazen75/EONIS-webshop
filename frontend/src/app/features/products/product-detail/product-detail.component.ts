import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  loading = true;
  quantity = 1;
  addingToCart = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService,
    private auth: AuthService,
    private snack: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productService.getById(id).subscribe({
      next: p => { this.product = p; this.loading = false; },
      error: () => { this.loading = false; this.router.navigate(['/products']); }
    });
  }

  increaseQty(): void {
    if (this.product && this.quantity < this.product.stockQuantity) this.quantity++;
  }

  decreaseQty(): void {
    if (this.quantity > 1) this.quantity--;
  }

  addToCart(): void {
    if (!this.auth.isLoggedIn()) { this.router.navigate(['/login']); return; }
    if (!this.product) return;

    this.addingToCart = true;
    this.cartService.addItem(this.product.id, this.quantity).subscribe({
      next: () => {
        this.addingToCart = false;
        this.snack.open('Dodato u korpu!', 'OK', { duration: 2500, panelClass: 'success-snack' });
      },
      error: err => {
        this.addingToCart = false;
        const msg = err.error?.message || 'Greška pri dodavanju u korpu';
        this.snack.open(msg, 'OK', { duration: 3500, panelClass: 'error-snack' });
      }
    });
  }
}
