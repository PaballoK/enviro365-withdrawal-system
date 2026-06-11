import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { WithdrawalRequestDTO } from '../../core/models/WithdrawalRequestDTO';
import { DepositRequestDTO } from '../../core/models/DepositRequestDTO';
import { TransactionResponseDTO } from '../../core/models/TransactionResponseDTO';

@Injectable({
  providedIn: 'root'
})
export class WithdrawalService {

  private readonly withdrawalsUrl = 'http://localhost:8080/api/withdrawals';
  private readonly depositsUrl = 'http://localhost:8080/api/deposits';

  constructor(private http: HttpClient) { }

  withdraw(request: WithdrawalRequestDTO): Observable<TransactionResponseDTO> {
    return this.http.post<TransactionResponseDTO>(this.withdrawalsUrl, request);
  }

  deposit(request: DepositRequestDTO): Observable<TransactionResponseDTO> {
    return this.http.post<TransactionResponseDTO>(this.depositsUrl, request);
  }

  getTransactionHistory(type?: 'WITHDRAW' | 'DEPOSIT'): Observable<TransactionResponseDTO[]> {
    let params = new HttpParams();
    if (type) params = params.set('type', type);
    return this.http.get<TransactionResponseDTO[]>(`${this.withdrawalsUrl}/history`, { params });
  }

  exportCsv(startDate?: string, endDate?: string): Observable<Blob> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http.get(`${this.withdrawalsUrl}/export`, {
      params,
      responseType: 'blob',
    });
  }
}
