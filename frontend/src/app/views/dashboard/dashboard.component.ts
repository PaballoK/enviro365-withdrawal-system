import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { AvatarModule } from 'primeng/avatar';
import { AvatarGroupModule } from 'primeng/avatargroup';
import { MenuModule } from 'primeng/menu';
import { MenuItem } from 'primeng/api';

import { InvestorPortfolioDTO } from '../../core/models/InvestorPortfolioDTO';
import { ProductType } from '../../enums/ProductType';
import { InvestorService } from '../../services/InvestorService/investor.service';
import { InvestorContextService } from '../../services/InvestorContextService/investor-context.service';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterLink, ButtonModule, AvatarGroupModule, AvatarModule, MenuModule, LoaderComponent, SidebarComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, OnDestroy {

  readonly ProductType = ProductType;

  investors = [1, 2, 3];
  investorMenuItems: MenuItem[] = [];

  isLoading = false;
  portfolio: InvestorPortfolioDTO | null = null;
  error: string | null = null;

  private sub!: Subscription;

  constructor(
    private investorService: InvestorService,
    private investorContext: InvestorContextService,
    private router: Router
  ) {}

  ngOnInit(): void {

    this.investorMenuItems = this.investors.map(id => ({
      label: `Investor ${id}`,
      command: () => this.investorContext.selectInvestor(id)
    }));

    this.subscribeToInvestor();

  }

  private subscribeToInvestor(): void {
    this.sub = this.investorContext.investorId$.subscribe(id => {
      this.loadPortfolio(id);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  private loadPortfolio(investorId: number): void {
    this.isLoading = true;
    this.error = null;

    this.investorService.getPortfolio(investorId).subscribe({
      next: (response) => {
        this.portfolio = response;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err.error?.message ?? 'Could not load portfolio.';
        this.isLoading = false;
      }
    });
  }

  get currentInvestorId(): number {
    return this.investorContext.currentInvestorId;
  }

  get investorInitials(): string {
    if (!this.portfolio) return `${this.investorContext.currentInvestorId}`;
    return `${this.portfolio.firstName[0]}${this.portfolio.lastName[0]}`.toUpperCase();
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(value);
  }


  goToWithdrawal(productId: number): void {
    this.router.navigate(['/withdrawal'], { queryParams: { productId } });
  }

}
