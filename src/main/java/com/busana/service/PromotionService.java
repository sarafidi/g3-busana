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

                if (promotion.getStartDate() != null && promotion.getEndDate() != null) {
    if (promotion.getEndDate().isBefore(promotion.getStartDate())) {
        throw new IllegalArgumentException("The End Date cannot be before the Start Date.");
    }
}
        List<String> existingIds = promotionRepository.findAll().stream()
                .map(Promotion::getPromotionID)
                .toList();
                int nextNum = nextSequence(existingIds, "PROM");
        String generatedId = formatId("PROM", nextNum);

        promotion.setPromotionID(generatedId);
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
       return promotionRepository.findById(promotionID.trim()).orElseThrow(() -> new IllegalArgumentException("Promotion not found with ID: " + promotionID)); //trim removes whitespace

    };
    
    // Update/Edit
    @Transactional
    public Promotion updatePromotion(String promotionID, Promotion updatedPromo){

                        if (updatedPromo.getStartDate() != null && updatedPromo.getEndDate() != null) {
    if (updatedPromo.getEndDate().isBefore(updatedPromo.getStartDate())) {
        throw new IllegalArgumentException("The End Date cannot be before the Start Date.");
    }
}
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

   // Add to your PromotionService.java class

public long getTotalPromotionsCount() {
    List<Promotion> promos = promotionRepository.findAll();
    return promos != null ? promos.size() : 0;
}

public long getPromotionCountByStatus(String status) {
    List<Promotion> promos = promotionRepository.findAll();
    if (promos == null) return 0;
    
    return promos.stream()
        .filter(p -> p.getStatus() != null && status.equalsIgnoreCase(p.getStatus().trim()))
        .count();
}
   //Helper functions
    private static boolean hasText(String value) {
            return value != null && !value.trim().isBlank();
        }

    private String formatId(String prefix, int value) {
        return prefix + String.format("%03d", value);
    }
       private int nextSequence(List<String> existingIds, String prefix) {
        int maxValue = 0;

        for (String id : existingIds) {
            if (!hasText(id) || !id.startsWith(prefix)) {
                continue;
            }

            String numericPart = id.substring(prefix.length()).replaceAll("[^0-9]", "");
            if (!numericPart.isBlank()) {
                maxValue = Math.max(maxValue, Integer.parseInt(numericPart));
            }
        }

        return maxValue + 1;
    }
}
