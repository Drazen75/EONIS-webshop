import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order, Transaction, User } from '../models/order.model';
import { PagedResponse } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly api = `${environment.apiUrl}/orders`;
  private readonly adminApi = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  checkout(shippingAddress: string): Observable<{ checkoutUrl: string; orderId: number }> {
    return this.http.post<{ checkoutUrl: string; orderId: number }>(
      `${this.api}/checkout`, { shippingAddress }
    );
  }

  getMyOrders(page = 0, size = 10): Observable<PagedResponse<Order>> {
    const params = new HttpParams().set('page', page).set('size', size).set('sort', 'createdAt,desc');
    return this.http.get<PagedResponse<Order>>(this.api, { params });
  }

  getMyOrder(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.api}/${id}`);
  }

  getOrderBySession(sessionId: string): Observable<Order> {
    return this.http.get<Order>(`${this.api}/by-session`, {
      params: new HttpParams().set('sessionId', sessionId)
    });
  }

  cancelOrder(id: number): Observable<Order> {
    return this.http.post<Order>(`${this.api}/${id}/cancel`, {});
  }

  // Admin
  getAllOrders(params: { status?: string; search?: string; page?: number; size?: number }): Observable<PagedResponse<Order>> {
    let p = new HttpParams().set('sort', 'createdAt,desc');
    if (params.status)  p = p.set('status', params.status);
    if (params.search)  p = p.set('search', params.search);
    if (params.page != null) p = p.set('page', params.page);
    if (params.size != null) p = p.set('size', params.size);
    return this.http.get<PagedResponse<Order>>(`${this.api}/admin/all`, { params: p });
  }

  updateOrderStatus(id: number, status: string): Observable<Order> {
    return this.http.put<Order>(`${this.api}/admin/${id}/status`, null, {
      params: new HttpParams().set('status', status)
    });
  }

  getTransactions(params: { search?: string; page?: number; size?: number }): Observable<PagedResponse<Transaction>> {
    let p = new HttpParams().set('sort', 'createdAt,desc');
    if (params.search)  p = p.set('search', params.search);
    if (params.page != null) p = p.set('page', params.page);
    if (params.size != null) p = p.set('size', params.size);
    return this.http.get<PagedResponse<Transaction>>(`${this.adminApi}/transactions`, { params: p });
  }

  getUsers(params: { search?: string; page?: number; size?: number }): Observable<PagedResponse<User>> {
    let p = new HttpParams();
    if (params.search)  p = p.set('search', params.search);
    if (params.page != null) p = p.set('page', params.page);
    if (params.size != null) p = p.set('size', params.size);
    return this.http.get<PagedResponse<User>>(`${this.adminApi}/users`, { params: p });
  }

  deactivateUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminApi}/users/${id}`);
  }

  activateUser(id: number): Observable<User> {
    return this.http.patch<User>(`${this.adminApi}/users/${id}/activate`, {});
  }
}
