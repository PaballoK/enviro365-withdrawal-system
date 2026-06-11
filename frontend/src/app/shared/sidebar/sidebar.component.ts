import { Component, Input } from '@angular/core';
import { RouterModule, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../services/AuthService/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, RouterLink, ButtonModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  @Input() activeItem: 'portfolio' | 'withdrawals' | 'deposits' | 'history' = 'portfolio';

  constructor(readonly authService: AuthService) {}
}
