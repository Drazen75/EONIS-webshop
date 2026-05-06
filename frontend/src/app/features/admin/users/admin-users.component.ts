import { Component, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { OrderService } from '../../../core/services/order.service';
import { User } from '../../../core/models/order.model';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.scss']
})
export class AdminUsersComponent implements OnInit {
  users: User[] = [];
  total = 0;
  page = 0;
  pageSize = 20;
  loading = false;
  searchCtrl = new FormControl('');
  displayedColumns = ['name', 'email', 'role', 'phone', 'active', 'date', 'actions'];

  constructor(private orderService: OrderService, private dialog: MatDialog, private snack: MatSnackBar) {}

  ngOnInit(): void {
    this.load();
    this.searchCtrl.valueChanges.pipe(debounceTime(400), distinctUntilChanged())
      .subscribe(() => { this.page = 0; this.load(); });
  }

  load(): void {
    this.loading = true;
    this.orderService.getUsers({
      search: this.searchCtrl.value || undefined,
      page: this.page, size: this.pageSize
    }).subscribe({
      next: res => { this.users = res.content; this.total = res.totalElements; this.loading = false; },
      error: () => this.loading = false
    });
  }

  onPage(e: PageEvent): void { this.page = e.pageIndex; this.pageSize = e.pageSize; this.load(); }

  deactivate(user: User): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: { title: 'Deaktiviraj korisnika', message: `Deaktivirajte korisnika ${user.email}?` }
    });
    ref.afterClosed().subscribe(ok => {
      if (!ok) return;
      this.orderService.deactivateUser(user.id).subscribe({
        next: () => { user.active = false; this.snack.open('Korisnik deaktiviran', 'OK', { duration: 2000 }); },
        error: () => this.snack.open('Greška', 'OK', { duration: 2000, panelClass: 'error-snack' })
      });
    });
  }

  activate(user: User): void {
    this.orderService.activateUser(user.id).subscribe({
      next: () => { user.active = true; this.snack.open('Korisnik aktiviran', 'OK', { duration: 2000, panelClass: 'success-snack' }); },
      error: () => this.snack.open('Greška', 'OK', { duration: 2000, panelClass: 'error-snack' })
    });
  }
}
