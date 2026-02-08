import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { authGuard } from './core/auth/auth.guard';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { AboutUsComponent } from './pages/about-us/about-us.component';


    export const routes: Routes = [
  

  // Main layout routes (protected)
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: 'about-us', component: AboutUsComponent },

      // Lazy load ProgrammeModule
      { 
        path: 'programmes',
        loadChildren: () => import('./features/programme/programme.module')
          .then(m => m.ProgrammeModule)
      }
    ]
  },

  // Auth layout routes (login/register)
  {
    path: 'auth',
    component: AuthLayoutComponent,
    children: [
      { 
        path: '',
        loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
      }
    ]
  },

  // Fallback route → redirect unknown paths to home
  { path: '**', redirectTo: '', pathMatch: 'full' }
];
