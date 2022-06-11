package com.tw.heima.service.model;

import com.tw.heima.repository.entity.FixedFeeInvoiceRequestEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixedFeeInvoiceRequest {
    private Integer id;
    private String requestId;
    private String taxIdentifier;
    private BigDecimal fixedFeeAmount;
    private FixedFeeInvoiceConfirmation fixedFeeInvoiceConfirmation;
    private List<InvoiceRequestRetryRecord> invoiceRequestRetryRecords;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    public FixedFeeInvoiceRequest(String taxIdentifier, BigDecimal fixedFeeAmount) {
        this.requestId = UUID.randomUUID().toString();
        this.taxIdentifier = taxIdentifier;
        this.fixedFeeAmount = fixedFeeAmount;
        this.createdAt = LocalDateTime.now();
        this.expiredAt = LocalDateTime.now().plusHours(24);
    }

    public FixedFeeInvoiceRequestEntity toEntity() {
        return FixedFeeInvoiceRequestEntity.builder()
                .id(id)
                .requestId(requestId)
                .taxIdentifier(taxIdentifier)
                .fixedFeeAmount(fixedFeeAmount)
                .fixedFeeInvoiceConfirmation(Optional.ofNullable(fixedFeeInvoiceConfirmation)
                        .map(FixedFeeInvoiceConfirmation::toEntity).orElse(null))
                .invoiceRequestRetryRecords(Optional.ofNullable(invoiceRequestRetryRecords).orElse(Collections.emptyList())
                        .stream()
                        .map(InvoiceRequestRetryRecord::toEntity)
                        .collect(Collectors.toList()))
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .build();
    }

    public static FixedFeeInvoiceRequest fromEntity(FixedFeeInvoiceRequestEntity entity) {
        return FixedFeeInvoiceRequest.builder()
                .id(entity.getId())
                .requestId(entity.getRequestId())
                .taxIdentifier(entity.getTaxIdentifier())
                .fixedFeeAmount(entity.getFixedFeeAmount())
                .fixedFeeInvoiceConfirmation(Optional.ofNullable(entity.getFixedFeeInvoiceConfirmation())
                        .map(FixedFeeInvoiceConfirmation::fromEntity).orElse(null))
                .invoiceRequestRetryRecords(Optional.ofNullable(entity.getInvoiceRequestRetryRecords()).orElse(Collections.emptyList())
                        .stream()
                        .map(InvoiceRequestRetryRecord::fromEntity)
                        .collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt())
                .expiredAt(entity.getExpiredAt())
                .build();
    }

    public FixedFeeInvoiceRequestStatus status() {
        if (fixedFeeInvoiceConfirmation == null) {
            if (invoiceRequestRetryRecords == null || invoiceRequestRetryRecords.size() < 12) {
                return FixedFeeInvoiceRequestStatus.PROCESSING;
            } else {
                return FixedFeeInvoiceRequestStatus.FAILED;
            }
        } else {
            return FixedFeeInvoiceRequestStatus.COMPLETED;
        }
    }

    public void increaseRetryTime() {
        this.invoiceRequestRetryRecords.add(new InvoiceRequestRetryRecord());
    }
}
