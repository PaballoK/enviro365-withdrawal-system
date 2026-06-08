export interface WithdrawalResponseDTO {
  id: number;
  investorId: number;
  productId: number;
  productName: string;
  amount: number;
  remainingBalance: number;
  processedAt: string;
}