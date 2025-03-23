package com.gijun.backend.repository.pos.receipt;

import com.gijun.backend.domain.pos.receipt.ReceiptItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {
    List<ReceiptItem> findByReceiptReceiptNumber(String receiptNumber);

    @Query("SELECT ri FROM ReceiptItem ri JOIN ri.receipt r WHERE r.saleDate BETWEEN :startDate AND :endDate")
    List<ReceiptItem> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT ri.product.id, SUM(ri.quantity) as totalQuantity " +
            "FROM ReceiptItem ri " +
            "GROUP BY ri.product.id " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProducts(Pageable pageable);
}