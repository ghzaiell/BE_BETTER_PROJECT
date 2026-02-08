import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';


export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  
  const token = localStorage.getItem('token');

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(req);
};
