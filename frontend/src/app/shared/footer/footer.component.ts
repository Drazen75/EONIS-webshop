import { Component, OnInit } from '@angular/core';
import { CategoryService } from '../../core/services/category.service';
import { Category } from '../../core/models/category.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {
  categories: Category[] = [];
  year = new Date().getFullYear();

  constructor(private categoryService: CategoryService, private router: Router) {}

  ngOnInit(): void {
    this.categoryService.getAll().subscribe(c => this.categories = c.slice(0, 6));
  }

  goToCategory(id: number): void {
    this.router.navigate(['/products'], { queryParams: { categoryId: id } });
  }
}
