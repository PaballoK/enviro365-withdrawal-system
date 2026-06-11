export interface TransactionResponseDTO {
  id: number;
  investorId: number;
  productId: number;
  productName: string;
  type: 'WITHDRAW' | 'DEPOSIT';
  amount: number;
  balanceAfter: number;
  processedAt: string;
}
