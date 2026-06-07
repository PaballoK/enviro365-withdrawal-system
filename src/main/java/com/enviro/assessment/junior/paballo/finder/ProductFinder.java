package com.enviro.assessment.junior.paballo.finder;

import com.enviro.assessment.junior.paballo.entity.Product;
import com.enviro.assessment.junior.paballo.exception.ProductNotFoundException;
import com.enviro.assessment.junior.paballo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFinder {

    private final ProductRepository productRepository;

    public Product getProductByIdOrThrow(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(()-> new ProductNotFoundException("Product not found with id " + productId));
    }
}
