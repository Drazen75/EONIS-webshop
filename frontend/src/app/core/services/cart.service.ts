import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cart } from '../models/cart.model';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly api = `${environment.apiUrl}/cart`;
  private cartSubject = new BehaviorSubject<Cart | null>(null);
  cart$ = this.cartSubject.asObservable();

  constructor(private http: HttpClient) {}

  load(): Observable<Cart> {
    return this.http.get<Cart>(this.api).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  addItem(productId: number, quantity: number): Observable<Cart> {
    return this.http.post<Cart>(`${this.api}/items`, { productId, quantity }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  updateItem(productId: number, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(`${this.api}/items/${productId}`, { quantity }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  removeItem(productId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.api}/items/${productId}`).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  clear(): Observable<void> {
    return this.http.delete<void>(this.api).pipe(
      tap(() => this.cartSubject.next(null))
    );
  }

  reset(): void {
    this.cartSubject.next(null);
  }

  get itemCount(): number {
    return this.cartSubject.value?.totalItems ?? 0;
  }
}
