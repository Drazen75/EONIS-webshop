import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';

// Angular Material
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatBadgeModule } from '@angular/material/badge';
import { MatMenuModule } from '@angular/material/menu';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSliderModule } from '@angular/material/slider';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

// Core
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';

// Components
import { AppComponent } from './app.component';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { FooterComponent } from './shared/footer/footer.component';
import { HomeComponent } from './features/home/home.component';
import { ProductListComponent } from './features/products/product-list/product-list.component';
import { ProductDetailComponent } from './features/products/product-detail/product-detail.component';
import { CartComponent } from './features/cart/cart.component';
import { CheckoutSuccessComponent } from './features/checkout/checkout-success/checkout-success.component';
import { CheckoutCancelComponent } from './features/checkout/checkout-cancel/checkout-cancel.component';
import { OrderListComponent } from './features/orders/order-list/order-list.component';
import { OrderDetailComponent } from './features/orders/order-detail/order-detail.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { AdminDashboardComponent } from './features/admin/dashboard/admin-dashboard.component';
import { AdminProductsComponent } from './features/admin/products/admin-products.component';
import { AdminProductFormComponent } from './features/admin/product-form/admin-product-form.component';
import { AdminOrdersComponent } from './features/admin/orders/admin-orders.component';
import { AdminUsersComponent } from './features/admin/users/admin-users.component';
import { AdminTransactionsComponent } from './features/admin/transactions/admin-transactions.component';
import { AdminCategoriesComponent } from './features/admin/categories/admin-categories.component';
import { ConfirmDialogComponent } from './shared/confirm-dialog/confirm-dialog.component';

const MATERIAL = [
  MatToolbarModule, MatButtonModule, MatIconModule, MatCardModule,
  MatInputModule, MatFormFieldModule, MatSelectModule, MatTableModule,
  MatPaginatorModule, MatSortModule, MatSnackBarModule, MatDialogModule,
  MatProgressSpinnerModule, MatBadgeModule, MatMenuModule, MatChipsModule,
  MatTooltipModule, MatDividerModule, MatListModule, MatSidenavModule,
  MatSliderModule, MatSlideToggleModule
];

@NgModule({
  declarations: [
    AppComponent, NavbarComponent, FooterComponent,
    HomeComponent,
    ProductListComponent, ProductDetailComponent,
    CartComponent,
    CheckoutSuccessComponent, CheckoutCancelComponent,
    OrderListComponent, OrderDetailComponent,
    LoginComponent, RegisterComponent,
    AdminDashboardComponent, AdminProductsComponent, AdminProductFormComponent,
    AdminOrdersComponent, AdminUsersComponent, AdminTransactionsComponent,
    AdminCategoriesComponent,
    ConfirmDialogComponent
  ],
  imports: [
    BrowserModule, BrowserAnimationsModule,
    HttpClientModule, FormsModule, ReactiveFormsModule,
    AppRoutingModule,
    ...MATERIAL
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
