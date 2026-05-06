import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CategoryService } from '../../../core/services/category.service';
import { Category } from '../../../core/models/category.model';

@Component({
  selector: 'app-admin-categories',
  templateUrl: './admin-categories.component.html',
  styleUrls: ['./admin-categories.component.scss']
})
export class AdminCategoriesComponent implements OnInit {
  categories: Category[] = [];
  loading = false;
  saving = false;

  // Inline edit/create state
  editingId: number | null = null;   // null = creating new
  formVisible = false;
  form: FormGroup;

  displayedColumns = ['id', 'name', 'description', 'actions'];

  constructor(
    private categoryService: CategoryService,
    private fb: FormBuilder,
    private snack: MatSnackBar
  ) {
    this.form = this.fb.group({
      name:        ['', [Validators.required, Validators.minLength(2)]],
      description: ['', Validators.required]
    });
  }

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.categoryService.getAll().subscribe({
      next: cats => { this.categories = cats; this.loading = false; },
      error: () => this.loading = false
    });
  }

  openCreate(): void {
    this.editingId = null;
    this.form.reset();
    this.formVisible = true;
  }

  openEdit(cat: Category): void {
    this.editingId = cat.id;
    this.form.setValue({ name: cat.name, description: cat.description });
    this.formVisible = true;
  }

  cancel(): void {
    this.formVisible = false;
    this.editingId = null;
    this.form.reset();
  }

  save(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true;
    const req = this.form.value;

    const obs = this.editingId != null
      ? this.categoryService.update(this.editingId, req)
      : this.categoryService.create(req);

    obs.subscribe({
      next: () => {
        this.snack.open(this.editingId ? 'Kategorija ažurirana.' : 'Kategorija kreirana.', 'OK', { duration: 3000, panelClass: 'success-snack' });
        this.cancel();
        this.load();
        this.saving = false;
      },
      error: err => {
        const msg = err?.error?.message || 'Greška pri čuvanju kategorije.';
        this.snack.open(msg, 'OK', { duration: 4000, panelClass: 'error-snack' });
        this.saving = false;
      }
    });
  }

  delete(cat: Category): void {
    if (!confirm(`Obrisati kategoriju "${cat.name}"? Svi proizvodi u ovoj kategoriji moraju biti premešteni ili obrisani pre brisanja.`)) return;
    this.categoryService.delete(cat.id).subscribe({
      next: () => {
        this.snack.open('Kategorija obrisana.', 'OK', { duration: 3000, panelClass: 'success-snack' });
        this.load();
      },
      error: err => {
        const msg = err?.error?.message || 'Nije moguće obrisati kategoriju sa postojećim proizvodima.';
        this.snack.open(msg, 'OK', { duration: 4000, panelClass: 'error-snack' });
      }
    });
  }
}
