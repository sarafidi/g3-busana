package com.busana.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.busana.model.Promotion;
import com.busana.repository.PromotionRepository;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;

     
    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    // Admin Creates Promotion
    public Promotion createPromotion(Promotion promotion){
    return promotionRepository.save(promotion);
    };
    
    //Admin Views Promotions
    public List<Promotion> getAllPromotions(){

         List<Promotion> promotions;
            promotions = promotionRepository.findAll();
        

        return promotions.stream()
            .sorted(Comparator.comparing(Promotion::getStartDate))
            .toList();

    };

    // Admin gets promotion by ID
    public Promotion getPromotionByID(String promotionID) {
       return promotionRepository.findById(promotionID.trim()).orElseThrow(() -> new RuntimeException("Order not found with ID: " + promotionID)); //trim removes whitespace

    };
    
    // Update/Edit
    @Transactional
    public Promotion updatePromotion(String promotionID, Promotion updatedPromo){
        Promotion promotion = getPromotionByID(promotionID);
        promotion.setStatus(updatedPromo.getStatus());
        promotion.setStartDate(updatedPromo.getStartDate());
        promotion.setPromotionName(updatedPromo.getPromotionName());
        promotion.setEndDate(updatedPromo.getEndDate());
        promotion.setDiscountValue(updatedPromo.getDiscountValue());
        promotion.setDiscountType(updatedPromo.getDiscountType());
        promotion.setApplicableCategory(updatedPromo.getApplicableCategory());
            return promotionRepository.save(promotion); 

    };
    
    // Delete
    @Transactional
   public void deletePromotion(String promotionID){

    Promotion promotion = getPromotionByID(promotionID);
        promotionRepository.delete(promotion);
   };
}
