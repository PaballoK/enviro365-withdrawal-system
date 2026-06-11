import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { InvestorPortfolioDTO } from '../../core/models/InvestorPortfolioDTO';

@Injectable({
  providedIn: 'root'
})
export class InvestorService {

  private readonly baseUrl = 'http://localhost:8080/api/portfolio';

  constructor(private http: HttpClient) { }

  getPortfolio(): Observable<InvestorPortfolioDTO> {
    return this.http.get<InvestorPortfolioDTO>(this.baseUrl);
  }
}
