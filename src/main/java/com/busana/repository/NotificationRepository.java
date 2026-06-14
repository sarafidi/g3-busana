package com.busana.repository;

import com.busana.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List <Notification> findByCustomer_CustomerID(String customerID);

}
