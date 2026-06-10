import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { InvestorPortfolioDTO } from '../../core/models/InvestorPortfolioDTO';
import { ProductType } from '../../enums/ProductType';
import { InvestorService } from '../../services/InvestorService/investor.service';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterLink, ButtonModule, LoaderComponent, SidebarComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  readonly ProductType = ProductType;

  isLoading = false;
  portfolio: InvestorPortfolioDTO | null = null;
  error: string | null = null;

  constructor(
    private investorService: InvestorService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPortfolio();
  }

  private loadPortfolio(): void {
    this.isLoading = true;
    this.error = null;

    this.investorService.getPortfolio().subscribe({
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

  get investorInitials(): string {
    if (!this.portfolio) return '?';
    return `${this.portfolio.firstName[0]}${this.portfolio.lastName[0]}`.toUpperCase();
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(value);
  }

  goToWithdrawal(productId: number): void {
    this.router.navigate(['/withdrawal'], { queryParams: { productId } });
  }
}
