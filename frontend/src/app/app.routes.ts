import { Routes } from '@angular/router';

export const routes: Routes = [

  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },   

  {
    path: 'dashboard',
    loadComponent: () =>
      import('./views/dashboard/dashboard.component').then((m) => m.DashboardComponent),
  },
  {
    path: 'withdrawal',
    loadComponent: () =>
      import('./views/withdrawal/withdrawal.component').then((m) => m.WithdrawalComponent),
  },
  {
    path: 'history/:id',
    loadComponent: () => 
      import('./views/history/history.component').then((m) => m.HistoryComponent),
  },
  { path: '**', redirectTo: 'dashboard' },
];
