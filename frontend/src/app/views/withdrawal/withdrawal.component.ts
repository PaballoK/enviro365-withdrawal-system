import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { InvestorService } from '../../services/InvestorService/investor.service';
import { InvestorContextService } from '../../services/InvestorContextService/investor-context.service';
import { WithdrawalService } from '../../services/WithdrawalService/withdrawal.service';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { InvestorPortfolioDTO } from '../../core/models/InvestorPortfolioDTO';
import { ProductDTO } from '../../core/models/ProductDTO';
import { WithdrawalResponseDTO } from '../../core/models/WithdrawalResponseDTO';

@Component({
  selector: 'app-withdrawal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ButtonModule, InputNumberModule, DropdownModule, RouterModule, RouterLink, SidebarComponent],
  templateUrl: './withdrawal.component.html',
  styleUrl: './withdrawal.component.css'
})
export class WithdrawalComponent implements OnInit, OnDestroy {

  portfolio: InvestorPortfolioDTO | null = null;
  products: ProductDTO[] = [];

  withdrawalForm!: FormGroup;

  isLoadingProducts = false;
  isSubmitting = false;
  loadError: string | null = null;
  submitError: string | null = null;
  successResponse: WithdrawalResponseDTO | null = null;

  private sub!: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private investorService: InvestorService,
    private withdrawalService: WithdrawalService,
    readonly investorContext: InvestorContextService,
    private route: ActivatedRoute
  ) {
    this.initiateWithdrawalForm();
  }

  ngOnInit(): void {
    const productId = this.route.snapshot.queryParams['productId'];

    this.sub = this.investorContext.investorId$.subscribe(id => {
      this.loadPortfolio(id, productId ? +productId : null);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  initiateWithdrawalForm(): void {
    this.withdrawalForm = this.formBuilder.group({
      product: [null, Validators.required],
      withdrawalAmount: [null, [Validators.required, Validators.min(0.01)]]
    });
  }

  private loadPortfolio(investorId: number, preSelectProductId?: number | null): void {
    this.isLoadingProducts = true;
    this.loadError = null;
    this.successResponse = null;
    this.submitError = null;
    this.withdrawalForm.reset();

    this.investorService.getPortfolio(investorId).subscribe({
      next: (portfolio) => {
        this.portfolio = portfolio;
        this.products = portfolio.products;
        if (preSelectProductId) {
          const preSelected = this.products.find(p => p.id === preSelectProductId) ?? null;
          this.withdrawalForm.patchValue({ product: preSelected });
        }
        this.isLoadingProducts = false;
      },
      error: (err) => {
        this.loadError = err.error?.message ?? 'Failed to load products.';
        this.isLoadingProducts = false;
      }
    });
  }

  get selectedProduct(): ProductDTO | null {
    return this.withdrawalForm?.get('product')?.value ?? null;
  }

  get maxAllowed(): number {
    if (!this.selectedProduct) return 0;
    return this.selectedProduct.balance * 0.9;
  }

  get isFormValid(): boolean {
    const amount = this.withdrawalForm.get('withdrawalAmount')?.value;
    return this.withdrawalForm.valid && !!amount && amount <= this.maxAllowed;
  }

  submitWithdrawal(): void {
    if (!this.isFormValid) return;

    this.isSubmitting = true;
    this.submitError = null;
    this.successResponse = null;

    const { product, withdrawalAmount } = this.withdrawalForm.value;

    this.withdrawalService.withdraw({
      investorId: this.investorContext.currentInvestorId,
      productId: product.id,
      withdrawalAmount
    }).subscribe({
      next: (response) => {
        this.successResponse = response;
        const p = this.products.find(p => p.id === response.productId);
        if (p) p.balance = response.remainingBalance;
        this.withdrawalForm.reset();
        this.isSubmitting = false;
      },
      error: (err) => {
        this.submitError = err.error?.message ?? 'Withdrawal failed. Please try again.';
        this.isSubmitting = false;
      }
    });
  }

  get investorName(): string {
    if (!this.portfolio) return `Investor ${this.investorContext.currentInvestorId}`;
    return `${this.portfolio.firstName} ${this.portfolio.lastName}`;
  }

}
