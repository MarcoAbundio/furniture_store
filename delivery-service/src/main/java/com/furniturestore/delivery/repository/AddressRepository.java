package com.furniturestore.delivery.repository;

import com.furniturestore.delivery.model.Address;
import org.springframework.data.jpa.repository.*;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomerId(Long customerId);
    List<Address> findByCustomerIdAndIsDefaultTrue(Long customerId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.customer.id = :customerId")
    void clearDefaultForCustomer(Long customerId);
}
