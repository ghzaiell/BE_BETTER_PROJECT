import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule,CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  username: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    const credentials = {
      username: this.username,   
      password: this.password
    };

    this.authService.login(credentials).subscribe({
      next: () => {
        this.router.navigate(['/home']);  // redirect after login
      },
      error: () => {
        this.errorMessage = 'Invalid username or password';
      }
    });
  }
}