import { ProductType } from "../../enums/ProductType";

export interface ProductDTO {
  id: number;
  productName: string;
  productType: ProductType;
  balance: number;
}