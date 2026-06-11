import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Table, TableModule } from 'primeng/table';
import { CalendarModule } from 'primeng/calendar';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { WithdrawalService } from '../../services/WithdrawalService/withdrawal.service';
import { TransactionResponseDTO } from '../../core/models/TransactionResponseDTO';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, CalendarModule, ButtonModule, DropdownModule, SidebarComponent, LoaderComponent],
  templateUrl: './history.component.html',
  styleUrl: './history.component.css'
})
export class HistoryComponent implements OnInit {

  @ViewChild('dt') dt!: Table;

  records: TransactionResponseDTO[] = [];
  filteredRecords: TransactionResponseDTO[] = [];
  fromDate: Date | null = null;
  toDate: Date | null = null;
  selectedType: 'WITHDRAW' | 'DEPOSIT' | null = null;
  isLoading = false;
  error: string | null = null;

  typeOptions = [
    { label: 'All', value: null },
    { label: 'Withdrawals', value: 'WITHDRAW' },
    { label: 'Deposits', value: 'DEPOSIT' },
  ];

  constructor(private withdrawalService: WithdrawalService) {}

  ngOnInit(): void {
    this.loadHistory();
  }

  private loadHistory(): void {
    this.isLoading = true;
    this.error = null;

    this.withdrawalService.getTransactionHistory(this.selectedType ?? undefined).subscribe({
      next: (data) => {
        this.records = data;
        this.applyDateFilter();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err.error?.message ?? 'Could not load transaction history.';
        this.isLoading = false;
      }
    });
  }

  onTypeChange(): void {
    this.loadHistory();
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
    const endDate = this.toDate ? toIso(this.toDate) : undefined;

    this.withdrawalService.exportCsv(startDate, endDate).subscribe(blob => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `statement.csv`;
      a.click();
      URL.revokeObjectURL(url);
    });
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(value);
  }
}
