package com.gijun.backend.service.pos.receipt;

import com.gijun.backend.domain.pos.Receipt.Receipt;
import com.gijun.backend.domain.pos.Receipt.ReceiptItem;
import com.gijun.backend.domain.pos.Receipt.ReceiptPayment;
import com.gijun.backend.domain.pos.Receipt.ReceiptStatus;
import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.domain.user.User;
import com.gijun.backend.dto.receipt.ReceiptDTO;
import com.gijun.backend.repository.pos.receipt.ReceiptRepository;
import com.gijun.backend.repository.sis.product.ProductRepository;
import com.gijun.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
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