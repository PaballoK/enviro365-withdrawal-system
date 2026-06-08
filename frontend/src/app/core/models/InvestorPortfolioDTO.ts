import { ProductDTO } from "./ProductDTO";

export interface InvestorPortfolioDTO {
  investorId: number;
  firstName: string;
  lastName: string;
  email: string;
  products: ProductDTO[];
  totalValue: number;
}