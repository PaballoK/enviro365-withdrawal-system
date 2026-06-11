import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { InvestorService } from '../../services/InvestorService/investor.service';
import { WithdrawalService } from '../../services/WithdrawalService/withdrawal.service';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { InvestorPortfolioDTO } from '../../core/models/InvestorPortfolioDTO';
import { ProductDTO } from '../../core/models/ProductDTO';
import { TransactionResponseDTO } from '../../core/models/TransactionResponseDTO';

@Component({
  selector: 'app-deposit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ButtonModule, InputNumberModule, DropdownModule, RouterModule, RouterLink, SidebarComponent],
  templateUrl: './deposit.component.html',
  styleUrl: './deposit.component.css'
})
export class DepositComponent implements OnInit {

  portfolio: InvestorPortfolioDTO | null = null;
  products: ProductDTO[] = [];

  depositForm!: FormGroup;

  isLoadingProducts = false;
  isSubmitting = false;
  loadError: string | null = null;
  submitError: string | null = null;
  successResponse: TransactionResponseDTO | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private investorService: InvestorService,
    private withdrawalService: WithdrawalService
  ) {
    this.depositForm = this.formBuilder.group({
      product: [null, Validators.required],
      depositAmount: [null, [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit(): void {
    this.loadPortfolio();
  }

  private loadPortfolio(): void {
    this.isLoadingProducts = true;
    this.loadError = null;
    this.successResponse = null;
    this.submitError = null;
    this.depositForm.reset();

    this.investorService.getPortfolio().subscribe({
      next: (portfolio) => {
        this.portfolio = portfolio;
        this.products = portfolio.products;
        this.isLoadingProducts = false;
      },
      error: (err) => {
        this.loadError = err.error?.message ?? 'Failed to load products.';
        this.isLoadingProducts = false;
      }
    });
  }

  get investorName(): string {
    if (!this.portfolio) return '';
    return `${this.portfolio.firstName} ${this.portfolio.lastName}`;
  }

  get isFormValid(): boolean {
    const amount = this.depositForm.get('depositAmount')?.value;
    return this.depositForm.valid && !!amount && amount > 0;
  }

  submitDeposit(): void {
    if (!this.isFormValid) return;

    this.isSubmitting = true;
    this.submitError = null;
    this.successResponse = null;

    const { product, depositAmount } = this.depositForm.value;

    this.withdrawalService.deposit({
      productId: product.id,
      depositAmount
    }).subscribe({
      next: (response) => {
        this.successResponse = response;
        const p = this.products.find(p => p.id === response.productId);
        if (p) p.balance = response.balanceAfter;
        this.depositForm.reset();
        this.isSubmitting = false;
      },
      error: (err) => {
        this.submitError = err.error?.message ?? 'Deposit failed. Please try again.';
        this.isSubmitting = false;
      }
    });
  }
}
