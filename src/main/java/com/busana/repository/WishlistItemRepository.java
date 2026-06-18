package com.busana.repository;

import com.busana.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, String> {
    List<WishlistItem> findByWishlist_WishlistID(String wishlistID);
}