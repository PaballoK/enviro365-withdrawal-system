import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class InvestorContextService {

  private _investorId = new BehaviorSubject<number>(1);

  readonly investorId$ = this._investorId.asObservable();

  get currentInvestorId(): number {
    return this._investorId.getValue();
  }

  selectInvestor(id: number): void {
    this._investorId.next(id);
  }

}
