import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { ProductService } from '../../../core/services/product.service';
import { CategoryService } from '../../../core/services/category.service';
import { Product } from '../../../core/models/product.model';
import { Category } from '../../../core/models/category.model';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-admin-products',
  templateUrl: './admin-products.component.html',
  styleUrls: ['./admin-products.component.scss']
})
export class AdminProductsComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  total = 0;
  page = 0;
  pageSize = 20;
  loading = false;
  selectedCategory: number | null = null;
  searchCtrl = new FormControl('');
  displayedColumns = ['image', 'name', 'category', 'price', 'stock', 'active', 'actions'];

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private dialog: MatDialog,
    private snack: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.categoryService.getAll().subscribe(c => this.categories = c);
    this.load();
    this.searchCtrl.valueChanges.pipe(debounceTime(400), distinctUntilChanged())
      .subscribe(() => { this.page = 0; this.load(); });
  }

  load(): void {
    this.loading = true;
    this.productService.getAllAdmin({
      search: this.searchCtrl.value || undefined,
      categoryId: this.selectedCategory || undefined,
      page: this.page,
      size: this.pageSize
    }).subscribe({
      next: res => { this.products = res.content; this.total = res.totalElements; this.loading = false; },
      error: () => this.loading = false
    });
  }

  onPage(e: PageEvent): void { this.page = e.pageIndex; this.pageSize = e.pageSize; this.load(); }

  edit(id: number): void { this.router.navigate(['/admin/products', id, 'edit']); }

  delete(product: Product): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: { title: 'Deaktiviraj proizvod', message: `Da li ste sigurni da želite da deaktivirate "${product.name}"?` }
    });
    ref.afterClosed().subscribe(confirmed => {
      if (!confirmed) return;
      this.productService.delete(product.id).subscribe({
        next: () => { this.snack.open('Proizvod deaktiviran', 'OK', { duration: 2500 }); this.load(); },
        error: () => this.snack.open('Greška', 'OK', { duration: 2500, panelClass: 'error-snack' })
      });
    });
  }
}
