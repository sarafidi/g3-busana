package com.busana.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    public Promotion createPromotion(Promotion promotion){

        if (promotion.getStartDate() != null && promotion.getEndDate() != null) {
            if (promotion.getEndDate().isBefore(promotion.getStartDate())) {
                throw new IllegalArgumentException("The End Date cannot be before the Start Date.");
            }
        }

        // Check promotion name uniqueness
        if (promotion.getPromotionName() != null && !promotion.getPromotionName().trim().isEmpty()) {
            String cleanName = promotion.getPromotionName().trim();
            Optional<Promotion> existingByName = promotionRepository.findByPromotionNameIgnoreCase(cleanName);
            if (existingByName.isPresent()) {
                throw new IllegalArgumentException("Promotion Name / Code '" + cleanName + "' already exists.");
            }
        }

        if (promotion.getPromotionID() != null && !promotion.getPromotionID().trim().isEmpty()) {
            String cleanId = promotion.getPromotionID().trim();
            if (promotionRepository.existsById(cleanId)) {
                throw new IllegalArgumentException("Promotion Code / ID '" + cleanId + "' already exists.");
            }
            promotion.setPromotionID(cleanId);
        } else {
            List<String> existingIds = promotionRepository.findAll().stream()
                    .map(Promotion::getPromotionID)
                    .toList();
            int nextNum = nextSequence(existingIds, "PROM");
            String generatedId = formatId("PROM", nextNum);
            promotion.setPromotionID(generatedId);
        }

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

        // Check promotion name uniqueness
        if (updatedPromo.getPromotionName() != null && !updatedPromo.getPromotionName().trim().isEmpty()) {
            String cleanName = updatedPromo.getPromotionName().trim();
            Optional<Promotion> existingByName = promotionRepository.findByPromotionNameIgnoreCase(cleanName);
            if (existingByName.isPresent() && !existingByName.get().getPromotionID().equals(promotionID)) {
                throw new IllegalArgumentException("Promotion Name / Code '" + cleanName + "' already exists.");
            }
        }

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