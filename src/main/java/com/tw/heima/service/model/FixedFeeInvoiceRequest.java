package com.tw.heima.service.model;

import com.tw.heima.repository.entity.FixedFeeInvoiceRequestEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixedFeeInvoiceRequest {
    private Integer id;
    private String requestId;
    private String taxIdentifier;
    private BigDecimal fixedFeeAmount;
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
                .createdAt(entity.getCreatedAt())
                .expiredAt(entity.getExpiredAt())
                .build();
    }

    public FixedFeeInvoiceRequestStatus status() {
        return FixedFeeInvoiceRequestStatus.PROCESSING;
    }
}
