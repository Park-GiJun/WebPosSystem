package com.gijun.backend.service.pos.receipt;

import com.gijun.backend.domain.pos.receipt.Receipt;
import com.gijun.backend.domain.pos.receipt.ReceiptItem;
import com.gijun.backend.domain.pos.receipt.ReceiptPayment;
import com.gijun.backend.domain.pos.receipt.ReceiptStatus;
import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.domain.user.User;
import com.gijun.backend.dto.receipt.ReceiptDTO;
import com.gijun.backend.repository.pos.receipt.ReceiptRepository;
import com.gijun.backend.repository.sis.product.ProductRepository;
import com.gijun.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReceiptDTO.ReceiptResponse createReceipt(String username, ReceiptDTO.ReceiptRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        Receipt receipt = new Receipt();
        receipt.setUser(user);
        receipt.setReceiptNumber(generateReceiptNumber());
        receipt.setSaleDate(LocalDateTime.now());
        receipt.setStatus(ReceiptStatus.COMPLETED);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (int i = 0; i < request.getItems().size(); i++) {
            ReceiptDTO.ReceiptItemRequest itemRequest = request.getItems().get(i);
            Product product = productRepository.findById(itemRequest.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));

            BigDecimal unitPrice = itemRequest.getUnitPrice() != null ? itemRequest.getUnitPrice() : product.getPrice();

            BigDecimal lineAmount = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            BigDecimal taxAmount = calculateTax(lineAmount, product.isTaxable());

            ReceiptItem item = new ReceiptItem();
            item.setReceipt(receipt);
            item.setLineNo(i + 1);
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setLineAmount(lineAmount);
            item.setTaxAmount(taxAmount);
            item.setDiscountAmount(BigDecimal.ZERO);

            receipt.getItems().add(item);

            totalAmount = totalAmount.add(lineAmount);
            totalTax = totalTax.add(taxAmount);

            product.updateStock(-itemRequest.getQuantity());
        }

        receipt.setTotalAmount(totalAmount);
        receipt.setTaxAmount(totalTax);
        receipt.setDiscountAmount(BigDecimal.ZERO);

        if (request.getPayments() != null) {
            for (ReceiptDTO.ReceiptPaymentRequest paymentRequest : request.getPayments()) {
                ReceiptPayment payment = new ReceiptPayment();
                payment.setReceipt(receipt);
                payment.setPaymentType(paymentRequest.getPaymentType());
                payment.setPaymentAmount(paymentRequest.getPaymentAmount());
                payment.setPaymentTime(LocalDateTime.now());
                payment.setApprovalNumber(paymentRequest.getApprovalNumber());
                payment.setCardInfo(paymentRequest.getCardInfo());

                receipt.getPayments().add(payment);
            }
        }

        receiptRepository.save(receipt);
        return ReceiptDTO.ReceiptResponse.from(receipt);
    }

    /**
     * 영수증 번호로 영수증 조회
     */
    @Transactional(readOnly = true)
    public ReceiptDTO.ReceiptResponse getReceipt(String receiptNumber) {
        Receipt receipt = receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new RuntimeException("Receipt not found with number: " + receiptNumber));
        return ReceiptDTO.ReceiptResponse.from(receipt);
    }

    /**
     * 영수증 취소
     * - 사용자 인증 검사
     * - 이미 취소된 영수증인지 확인
     * - 재고 복원
     * - 상태 변경
     */
    @Transactional
    public ReceiptDTO.ReceiptResponse cancelReceipt(String username, String receiptNumber) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Receipt receipt = receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new RuntimeException("Receipt not found with number: " + receiptNumber));

        if (!receipt.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You don't have permission to cancel this receipt");
        }

        if (receipt.getStatus() == ReceiptStatus.CANCELED) {
            throw new RuntimeException("Receipt is already canceled");
        }

        for (ReceiptItem item : receipt.getItems()) {
            Product product = item.getProduct();
            product.updateStock(item.getQuantity()); // 재고를 다시 더해줌
        }

        receipt.setStatus(ReceiptStatus.CANCELED);

        receiptRepository.save(receipt);

        return ReceiptDTO.ReceiptResponse.from(receipt);
    }

    /**
     * 사용자별 영수증 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ReceiptDTO.ReceiptResponse> getUserReceipts(String username, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findByUserUsername(username, pageable);
        return receipts.map(ReceiptDTO.ReceiptResponse::from);
    }

    /**
     * 날짜 범위별 영수증 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ReceiptDTO.ReceiptResponse> getReceiptsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findByDateRange(startDate, endDate, pageable);
        return receipts.map(ReceiptDTO.ReceiptResponse::from);
    }

    /**
     * 사용자 및 날짜 범위별 영수증 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ReceiptDTO.ReceiptResponse> getUserReceiptsByDateRange(String username, LocalDateTime startDate,
                                                                       LocalDateTime endDate, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findByUserUsernameAndDateRange(username, startDate, endDate, pageable);
        return receipts.map(ReceiptDTO.ReceiptResponse::from);
    }

    private String generateReceiptNumber() {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeStr = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));

        return "RCPT-" + dateStr + "-" + timeStr + "-" + random;
    }

    private BigDecimal calculateTax(BigDecimal amount, boolean isTaxable) {
        if (!isTaxable) {
            return BigDecimal.ZERO;
        }

        return amount.multiply(new BigDecimal("0.1")).setScale(0, RoundingMode.HALF_UP);
    }
}