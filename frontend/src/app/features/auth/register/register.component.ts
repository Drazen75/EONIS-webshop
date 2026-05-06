import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  form: FormGroup;
  loading = false;
  hidePassword = true;

  constructor(fb: FormBuilder, private auth: AuthService, private router: Router, private snack: MatSnackBar) {
    this.form = fb.group({
      firstName: ['', Validators.required],
      lastName:  ['', Validators.required],
      email:     ['', [Validators.required, Validators.email]],
      password:  ['', [Validators.required, Validators.minLength(6)]],
      phone:     [''],
      address:   ['']
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.auth.register(this.form.value).subscribe({
      next: () => {
        this.snack.open('Nalog uspešno kreiran!', 'OK', { duration: 2500, panelClass: 'success-snack' });
        this.router.navigate(['/']);
      },
      error: err => {
        this.loading = false;
        this.snack.open(err.error?.message || 'Greška pri registraciji', 'OK', { duration: 3500, panelClass: 'error-snack' });
      }
    });
  }
}
