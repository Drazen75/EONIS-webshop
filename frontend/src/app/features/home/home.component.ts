import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProductService } from '../../core/services/product.service';
import { CategoryService } from '../../core/services/category.service';
import { Product } from '../../core/models/product.model';
import { Category } from '../../core/models/category.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  featured: Product[] = [];
  categories: Category[] = [];

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.productService.getAll({ size: 6, sort: 'createdAt,desc' })
      .subscribe(p => this.featured = p.content);
    this.categoryService.getAll().subscribe(c => this.categories = c);
  }

  goToCategory(id: number): void {
    this.router.navigate(['/products'], { queryParams: { categoryId: id } });
  }

  goToProduct(id: number): void {
    this.router.navigate(['/products', id]);
  }
}
