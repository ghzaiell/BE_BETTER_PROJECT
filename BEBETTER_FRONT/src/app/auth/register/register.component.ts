import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {

  username: string = '';
  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.errorMessage = '';

    if (!this.username.trim() || !this.email.trim() || !this.password) {
      this.errorMessage = 'All fields are required';
      return;
    }
    if (this.password.length < 6) {
      this.errorMessage = 'Password must be at least 6 characters';
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }

    this.loading = true;
    this.authService.register({
      username: this.username.trim(),
      email: this.email.trim(),
      password: this.password
    }).subscribe({
      next: () => {
        this.router.navigate(['/home']);  // signed in right after registering
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Registration failed, please try again';
        this.loading = false;
      }
    });
  }
}
