import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { InvestorService } from '../../services/InvestorService/investor.service';
import { InvestorContextService } from '../../services/InvestorContextService/investor-context.service';
import { InvestorPortfolioDTO } from '../../core/models/InvestorPortfolioDTO';
import { ProductDTO } from '../../core/models/ProductDTO';

@Component({
  selector: 'app-withdrawal',
  standalone: true,
  imports: [CommonModule, FormsModule, ButtonModule, InputNumberModule, DropdownModule, RouterModule, RouterLink],
  templateUrl: './withdrawal.component.html',
  styleUrl: './withdrawal.component.css'
})
export class WithdrawalComponent implements OnInit, OnDestroy {

  portfolio: InvestorPortfolioDTO | null = null;
  products: ProductDTO[] = [];
  selectedProduct: ProductDTO | null = null;
  isLoadingProducts = false;
  loadError: string | null = null;

  private sub!: Subscription;

  constructor(
    private investorService: InvestorService,
    private investorContext: InvestorContextService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const productId = this.route.snapshot.queryParams['productId'];

    this.sub = this.investorContext.investorId$.subscribe(id => {
      this.loadPortfolio(id, productId ? +productId : null);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  private loadPortfolio(investorId: number, preSelectProductId?: number | null): void {
    this.isLoadingProducts = true;
    this.loadError = null;
    this.selectedProduct = null;

    this.investorService.getPortfolio(investorId).subscribe({
      next: (portfolio) => {
        this.portfolio = portfolio;
        this.products = portfolio.products;
        if (preSelectProductId) {
          this.selectedProduct = this.products.find(p => p.id === preSelectProductId) ?? null;
        }
        this.isLoadingProducts = false;
      },
      error: (err) => {
        this.loadError = err.error?.message ?? 'Failed to load products.';
        this.isLoadingProducts = false;
      }
    });
  }

  get investorName(): string {
    if (!this.portfolio) return `Investor ${this.investorContext.currentInvestorId}`;
    return `${this.portfolio.firstName} ${this.portfolio.lastName}`;
  }

}
