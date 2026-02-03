import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { User } from '../../shared/models/user/user.model';
import { HttpClient } from '@angular/common/http';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
    private urlAuth =  'http://localhost:8080/auth';
   private currentUserSubject = new BehaviorSubject<User | null>(null);  
  currentUser$ = this.currentUserSubject.asObservable();


  constructor(private http: HttpClient) {
    this.restoreUserFromToken();
  }

  // LOGIN
  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post<{ token: string }>(`${this.urlAuth}/login`, credentials).pipe(
      tap((res) => {
        localStorage.setItem('token', res.token);
        this.decodeAndStoreUser(res.token);
      })
    );
  }

  // LOGOUT
  logout() {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
  }

  //  GET TOKEN
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  //  CHECK LOGIN
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // Decode JWT and update BehaviorSubject
  private decodeAndStoreUser(token: string) {
    try {
      const decoded: any = jwtDecode(token);
      const user: User = {
        username: decoded.sub, // your backend sets "sub" = username
        id: "",
        email: "",
        role: ""
      };
      this.currentUserSubject.next(user);
    } catch (error) {
      console.error('Invalid JWT Token');
      this.currentUserSubject.next(null);
    }
  }

  // 🔄 Restore user on refresh
  private restoreUserFromToken() {
    const token = this.getToken();
    if (token) {
      this.decodeAndStoreUser(token);
    }
  }
  get currentUser() {
  return this.currentUserSubject.asObservable();
}
}


