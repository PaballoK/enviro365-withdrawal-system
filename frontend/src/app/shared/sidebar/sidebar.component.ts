import { Component, Input } from '@angular/core';
import { RouterModule, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InvestorContextService } from '../../services/InvestorContextService/investor-context.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, RouterLink, ButtonModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  @Input() activeItem: 'portfolio' | 'withdrawals' | 'history' = 'portfolio';

  constructor(readonly investorContext: InvestorContextService) {}
}
