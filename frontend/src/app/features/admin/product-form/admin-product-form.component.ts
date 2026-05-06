import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProductService } from '../../../core/services/product.service';
import { CategoryService } from '../../../core/services/category.service';
import { Category } from '../../../core/models/category.model';

@Component({
  selector: 'app-admin-product-form',
  templateUrl: './admin-product-form.component.html',
  styleUrls: ['./admin-product-form.component.scss']
})
export class AdminProductFormComponent implements OnInit {
  form: FormGroup;
  categories: Category[] = [];
  isEdit = false;
  productId: number | null = null;
  loading = false;
  saving = false;

  constructor(
    fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private categoryService: CategoryService,
    private snack: MatSnackBar
  ) {
    this.form = fb.group({
      name:          ['', Validators.required],
      description:   [''],
      price:         [null, [Validators.required, Validators.min(0.01)]],
      stockQuantity: [null, [Validators.required, Validators.min(0)]],
      imageUrl:      [''],
      categoryId:    [null, Validators.required],
      active:        [true]
    });
  }

  ngOnInit(): void {
    this.categoryService.getAll().subscribe(c => this.categories = c);

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.productId = +id;
      this.loading = true;
      this.productService.getById(this.productId).subscribe({
        next: p => {
          this.form.patchValue({
            name: p.name, description: p.description, price: p.price,
            stockQuantity: p.stockQuantity, imageUrl: p.imageUrl,
            categoryId: p.category?.id, active: p.active
          });
          this.loading = false;
        },
        error: () => { this.loading = false; this.router.navigate(['/admin/products']); }
      });
    }
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true;
    const req = this.form.value;
    const call = this.isEdit
      ? this.productService.update(this.productId!, req)
      : this.productService.create(req);

    call.subscribe({
      next: () => {
        this.snack.open(this.isEdit ? 'Proizvod ažuriran' : 'Proizvod kreiran', 'OK', { duration: 2500, panelClass: 'success-snack' });
        this.router.navigate(['/admin/products']);
      },
      error: err => {
        this.saving = false;
        this.snack.open(err.error?.message || 'Greška', 'OK', { duration: 3000, panelClass: 'error-snack' });
      }
    });
  }
}
