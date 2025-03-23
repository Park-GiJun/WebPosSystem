package com.gijun.backend.repository.pos.receipt;

import com.gijun.backend.domain.pos.receipt.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByReceiptNumber(String receiptNumber);

    Page<Receipt> findByUserUsername(String username, Pageable pageable);

    @Query("SELECT r FROM Receipt r WHERE r.saleDate BETWEEN :startDate AND :endDate")
    Page<Receipt> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT r FROM Receipt r JOIN r.user u WHERE u.username = :username AND r.saleDate BETWEEN :startDate AND :endDate")
    Page<Receipt> findByUserUsernameAndDateRange(String username, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}