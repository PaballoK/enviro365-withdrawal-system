import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { WithdrawalRequestDTO } from '../../core/models/WithdrawalRequestDTO';
import { Observable } from 'rxjs';
import { WithdrawalResponseDTO } from '../../core/models/WithdrawalResponseDTO';

@Injectable({
  providedIn: 'root'
})
export class WithdrawalService {
 private readonly baseUrl = 'http://localhost:8080/api/withdrawals'; 

  constructor(private http:HttpClient) { }

    withdraw(request: WithdrawalRequestDTO):Observable<WithdrawalResponseDTO>{
      return this.http.post<WithdrawalResponseDTO>(`${this.baseUrl}`,request);
    }

   getWithdrawalHistory(investorId: number):Observable<WithdrawalResponseDTO[]>{
    return this.http.get<WithdrawalResponseDTO[]>(`${this.baseUrl}/investor/${investorId}`)
   } 

    exportCsv(investorId: number, startDate?: string, endDate?: string): Observable<Blob> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http.get(`${this.baseUrl}/investor/${investorId}/export`, {
      params,
      responseType: 'blob',
    });
  }
}
