import { HttpClient } from '@angular/common/http';
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
}
