import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PagedResponse, Product, ProductRequest } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly api = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {}

  getAll(params: {
    search?: string;
    categoryId?: number;
    minPrice?: number;
    maxPrice?: number;
    page?: number;
    size?: number;
    sort?: string;
  }): Observable<PagedResponse<Product>> {
    let p = new HttpParams();
    if (params.search)     p = p.set('search', params.search);
    if (params.categoryId) p = p.set('categoryId', params.categoryId);
    if (params.minPrice != null) p = p.set('minPrice', params.minPrice);
    if (params.maxPrice != null) p = p.set('maxPrice', params.maxPrice);
    if (params.page != null) p = p.set('page', params.page);
    if (params.size != null) p = p.set('size', params.size);
    if (params.sort)        p = p.set('sort', params.sort);
    return this.http.get<PagedResponse<Product>>(this.api, { params: p });
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.api}/${id}`);
  }

  getAllAdmin(params: { search?: string; categoryId?: number; page?: number; size?: number }): Observable<PagedResponse<Product>> {
    let p = new HttpParams();
    if (params.search)     p = p.set('search', params.search);
    if (params.categoryId) p = p.set('categoryId', params.categoryId);
    if (params.page != null) p = p.set('page', params.page);
    if (params.size != null) p = p.set('size', params.size);
    return this.http.get<PagedResponse<Product>>(`${this.api}/admin/all`, { params: p });
  }

  create(req: ProductRequest): Observable<Product> {
    return this.http.post<Product>(this.api, req);
  }

  update(id: number, req: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.api}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
