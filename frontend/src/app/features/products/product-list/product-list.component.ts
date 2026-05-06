import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { ProductService } from '../../../core/services/product.service';
import { CategoryService } from '../../../core/services/category.service';
import { Product } from '../../../core/models/product.model';
import { Category } from '../../../core/models/category.model';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  totalElements = 0;
  loading = false;

  searchCtrl  = new FormControl('');
  minPriceCtrl = new FormControl<number | null>(null);
  maxPriceCtrl = new FormControl<number | null>(null);
  selectedCategory: number | null = null;
  sortBy = 'createdAt,desc';
  page = 0;
  pageSize = 12;

  sortOptions = [
    { value: 'createdAt,desc', label: 'Najnoviji' },
    { value: 'price,asc',      label: 'Cena: niža → viša' },
    { value: 'price,desc',     label: 'Cena: viša → niža' },
    { value: 'name,asc',       label: 'Naziv A–Z' },
  ];

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.categoryService.getAll().subscribe(c => this.categories = c);

    this.route.queryParams.subscribe(params => {
      if (params['categoryId']) this.selectedCategory = +params['categoryId'];
      if (params['search'])     this.searchCtrl.setValue(params['search'], { emitEvent: false });
      this.load();
    });

    this.searchCtrl.valueChanges.pipe(debounceTime(380), distinctUntilChanged())
      .subscribe(() => { this.page = 0; this.load(); });

    this.minPriceCtrl.valueChanges.pipe(debounceTime(500))
      .subscribe(() => { this.page = 0; this.load(); });

    this.maxPriceCtrl.valueChanges.pipe(debounceTime(500))
      .subscribe(() => { this.page = 0; this.load(); });
  }

  load(): void {
    this.loading = true;
    this.productService.getAll({
      search:     this.searchCtrl.value || undefined,
      categoryId: this.selectedCategory || undefined,
      minPrice:   this.minPriceCtrl.value ?? undefined,
      maxPrice:   this.maxPriceCtrl.value ?? undefined,
      page:       this.page,
      size:       this.pageSize,
      sort:       this.sortBy
    }).subscribe({
      next: res => {
        this.products = res.content;
        this.totalElements = res.totalElements;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  onPageChange(e: PageEvent): void { this.page = e.pageIndex; this.pageSize = e.pageSize; this.load(); }
  onCategoryChange(): void { this.page = 0; this.load(); }
  onSortChange():    void { this.page = 0; this.load(); }

  selectCategory(id: number | null): void {
    this.selectedCategory = id;
    this.page = 0;
    this.load();
  }

  clearFilters(): void {
    this.searchCtrl.setValue('');
    this.minPriceCtrl.setValue(null);
    this.maxPriceCtrl.setValue(null);
    this.selectedCategory = null;
    this.sortBy = 'createdAt,desc';
    this.page = 0;
    this.load();
  }

  hasActiveFilters(): boolean {
    return !!(this.searchCtrl.value || this.selectedCategory ||
              this.minPriceCtrl.value || this.maxPriceCtrl.value);
  }

  goToProduct(id: number): void { this.router.navigate(['/products', id]); }
}
