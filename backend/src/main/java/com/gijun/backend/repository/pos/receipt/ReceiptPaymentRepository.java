package com.gijun.backend.repository.pos.receipt;

import com.gijun.backend.domain.pos.Receipt.ReceiptPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReceiptPaymentRepository extends JpaRepository<ReceiptPayment, Long> {
    List<ReceiptPayment> findByReceiptReceiptNumber(String receiptNumber);

    @Query("SELECT rp.paymentType, SUM(rp.paymentAmount) as totalAmount " +
            "FROM ReceiptPayment rp " +
            "WHERE rp.paymentTime BETWEEN :startDate AND :endDate " +
            "GROUP BY rp.paymentType")
    List<Object[]> findPaymentMethodsSummary(LocalDateTime startDate, LocalDateTime endDate);
}