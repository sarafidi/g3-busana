package com.busana.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "Promotion")
public class Promotion {
    
    @Id
    @Column(name = "promotionID", length = 20, nullable = false)
    private String promotionID;
    
    @Column(name = "promotionName", length = 100, nullable = false)
    private String promotionName;
    
    @Column(name = "discountType", length = 20, nullable = false)    
    private String  discountType;

    @Column(name = "discountValue", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountValue;
    
    @Column(name = "applicableCategory", length = 100)    
    private String  applicableCategory;

    @Column(name = "startDate", nullable = false)    
    private LocalDate  startDate;

    @Column(name = "endDate", nullable = false)    
    private LocalDate endDate;

    @Column(name = "status", length = 20, nullable = false)    
    private String  status = "active";

    public Promotion() {}

    public String getPromotionID() { return promotionID; }
    public void setPromotionID(String promotionID) { this.promotionID = promotionID; }

    public String getPromotionName() { return promotionName; }
    public void setPromotionName(String promotionName) { this.promotionName = promotionName; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public BigDecimal getDiscountValue() { return discountValue;}
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue;}

    public String getApplicableCategory() { return applicableCategory; }
    public void setApplicableCategory(String applicableCategory) { this.applicableCategory = applicableCategory; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}