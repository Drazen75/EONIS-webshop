import { Component, OnInit, HostListener, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';
import { CategoryService } from '../../core/services/category.service';
import { ThemeService } from '../../core/services/theme.service';
import { AuthResponse } from '../../core/models/auth.model';
import { Category } from '../../core/models/category.model';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  currentUser: AuthResponse | null = null;
  cartCount = 0;
  categories: Category[] = [];
  catMenuOpen = false;

  constructor(
    private auth: AuthService,
    private cartService: CartService,
    private categoryService: CategoryService,
    public theme: ThemeService,
    private router: Router,
    private elRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.auth.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) this.cartService.load().subscribe();
      else       this.cartService.reset();
    });
    this.cartService.cart$.subscribe(cart => {
      this.cartCount = cart?.totalItems ?? 0;
    });
    this.categoryService.getAll().subscribe(cats => this.categories = cats);
  }

  logout(): void { this.auth.logout(); }
  get isAdmin(): boolean { return this.auth.isAdmin(); }

  toggleCatMenu(): void { this.catMenuOpen = !this.catMenuOpen; }

  goToCategory(id: number): void {
    this.catMenuOpen = false;
    this.router.navigate(['/products'], { queryParams: { categoryId: id } });
  }

  goToAllProducts(): void {
    this.catMenuOpen = false;
    this.router.navigate(['/products']);
  }

  @HostListener('document:click', ['$event'])
  onDocClick(e: MouseEvent): void {
    if (!this.elRef.nativeElement.contains(e.target)) {
      this.catMenuOpen = false;
    }
  }
}
