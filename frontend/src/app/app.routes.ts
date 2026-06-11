import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [

  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

  {
    path: 'login',
    loadComponent: () =>
      import('./views/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./views/dashboard/dashboard.component').then((m) => m.DashboardComponent),
  },
  {
    path: 'withdrawal',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./views/withdrawal/withdrawal.component').then((m) => m.WithdrawalComponent),
  },
  {
    path: 'deposit',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./views/deposit/deposit.component').then((m) => m.DepositComponent),
  },
  {
    path: 'history',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./views/history/history.component').then((m) => m.HistoryComponent),
  },
  { path: '**', redirectTo: 'dashboard' },
];
