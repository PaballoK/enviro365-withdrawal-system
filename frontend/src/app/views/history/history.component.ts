import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { Table, TableModule } from 'primeng/table';
import { CalendarModule } from 'primeng/calendar';
import { ButtonModule } from 'primeng/button';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { WithdrawalService } from '../../services/WithdrawalService/withdrawal.service';
import { InvestorContextService } from '../../services/InvestorContextService/investor-context.service';
import { WithdrawalResponseDTO } from '../../core/models/WithdrawalResponseDTO';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, CalendarModule, ButtonModule, SidebarComponent,RouterLink],
  templateUrl: './history.component.html',
  styleUrl: './history.component.css'
})
export class HistoryComponent implements OnInit, OnDestroy {

  @ViewChild('dt') dt!: Table;

  records: WithdrawalResponseDTO[] = [];
  filteredRecords: WithdrawalResponseDTO[] = [];
  fromDate: Date | null = null;
  toDate: Date | null = null;
  isLoading = false;
  error: string | null = null;

  private sub!: Subscription;

  constructor(
    private router: Router,
    private withdrawalService: WithdrawalService,
    private investorContext: InvestorContextService
  ) {}

  ngOnInit(): void {
    this.sub = this.investorContext.investorId$.subscribe(id => {
      this.loadHistory(id);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  private loadHistory(investorId: number): void {
    this.isLoading = true;
    this.error = null;

    this.withdrawalService.getWithdrawalHistory(investorId).subscribe({
      next: (data) => {
        this.records = data;
        this.applyDateFilter();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err.error?.message ?? 'Could not load withdrawal history.';
        this.isLoading = false;
      }
    });
  }

  applyDateFilter(): void {
    this.filteredRecords = this.records.filter(r => {
      const date = new Date(r.processedAt);
      if (this.fromDate && date < this.fromDate) return false;
      if (this.toDate) {
        const end = new Date(this.toDate);
        end.setHours(23, 59, 59, 999);
        if (date > end) return false;
      }
      return true;
    });
  }

  exportCSV(): void {
    const toIso = (d: Date) => d.toISOString().split('T')[0];
    const startDate = this.fromDate ? toIso(this.fromDate) : undefined;
    const endDate   = this.toDate   ? toIso(this.toDate)   : undefined;

    this.withdrawalService.exportCsv(this.investorContext.currentInvestorId, startDate, endDate)
      .subscribe(blob => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `withdrawals-investor-${this.investorContext.currentInvestorId}.csv`;
        a.click();
        URL.revokeObjectURL(url);
      });
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(value);
  }

  
 
}
