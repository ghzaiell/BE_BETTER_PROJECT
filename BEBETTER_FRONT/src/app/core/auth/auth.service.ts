import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap, switchMap, of } from 'rxjs';
import { User } from '../../shared/models/user/user.model';
import { HttpClient } from '@angular/common/http';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private urlAuth = 'http://localhost:8080/auth';
  private urlUser = 'http://localhost:8080/user';
  
  private currentUserSubject = new BehaviorSubject<User | null>(null);  
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.restoreUserFromToken();
  }

  // ========================================
  // AUTH METHODS
  // ========================================

  // LOGIN
  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post<{ token: string }>(`${this.urlAuth}/login`, credentials).pipe(
      tap((res) => {
        localStorage.setItem('token', res.token);
      }),
      switchMap((res) => {
        // Extract username from token
        const username = this.getUsernameFromToken(res.token);
        if (username) {
          // Fetch full user data from backend
          return this.fetchUserData(username);
        }
        return of(null);
      })
    );
  }

  // LOGOUT
  logout() {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
  }

  // GET TOKEN
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // CHECK LOGIN
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // ========================================
  // USER DATA METHODS
  // ========================================

  // FETCH USER DATA FROM BACKEND
  private fetchUserData(username: string): Observable<User> {
    return this.http.get<User>(`${this.urlUser}/${username}`).pipe(
      tap((user) => {
        this.currentUserSubject.next(user);
        console.log('User data loaded:', user);
      })
    );
  }

  // GET USER BY ID (public method if needed elsewhere)
  getUserById(userId: string): Observable<User> {
    return this.http.get<User>(`${this.urlUser}/${userId}`);
  }

  // UPDATE USER
  updateUser(userId: string, user: User): Observable<string> {
    return this.http.put(`${this.urlUser}/update/${userId}`, user, {
      responseType: 'text'
    }).pipe(
      tap(() => {
        // Refresh user data after update
        this.refreshUserData();
      })
    );
  }

  // DELETE USER
  deleteUser(userId: string): Observable<string> {
    return this.http.delete(`${this.urlUser}/delete/${userId}`, {
      responseType: 'text'
    });
  }

  

  // GET USERNAME FROM TOKEN
  private getUsernameFromToken(token?: string): string | null {
    try {
      const tokenToUse = token || this.getToken();
      if (!tokenToUse) return null;
      
      const decoded: any = jwtDecode(tokenToUse);
      return decoded.sub || null; // "sub" contains username
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  // GET CURRENT USERNAME (synchronous)
  getCurrentUsername(): string {
    const user = this.currentUserSubject.value;
    return user?.username || '';
  }

  // GET CURRENT USER ID (synchronous)
  getCurrentUserId(): string {
    const user = this.currentUserSubject.value;
    return user?.id || '';
  }

  // GET CURRENT USER VALUE (synchronous)
  get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  // GET CURRENT USER OBSERVABLE
  get currentUser(): Observable<User | null> {
    return this.currentUserSubject.asObservable();
  }

  // ========================================
  // RESTORE & REFRESH METHODS
  // ========================================

  // RESTORE USER ON REFRESH
  private restoreUserFromToken() {
    const token = this.getToken();
    if (token) {
      const username = this.getUsernameFromToken(token);
      if (username) {
        // Fetch full user data from backend
        this.fetchUserData(username).subscribe({
          next: (user) => {
            console.log('User restored from token:', user);
          },
          error: (error) => {
            console.error('Error restoring user:', error);
            // If user fetch fails, clear invalid token
            this.logout();
          }
        });
      }
    }
  }

  // REFRESH USER DATA
  refreshUserData(): void {
    const username = this.getCurrentUsername();
    if (username) {
      this.fetchUserData(username).subscribe({
        next: (user) => {
          console.log('User data refreshed:', user);
        },
        error: (error) => {
          console.error('Error refreshing user data:', error);
        }
      });
    }
  }
}