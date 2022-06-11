package com.tw.heima.service.model;

import com.tw.heima.repository.entity.FixedFeeConfirmationEntity;
import com.tw.heima.repository.entity.FixedFeeInvoiceConfirmationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixedFeeInvoiceConfirmation {
    private Integer id;
    private BigDecimal fixedFeeAmount;
    private LocalDateTime createdAt;

    public FixedFeeInvoiceConfirmationEntity toEntity() {
        return FixedFeeInvoiceConfirmationEntity.builder()
                .id(id)
                .fixedFeeAmount(fixedFeeAmount)
                .createdAt(createdAt)
                .build();
    }

    public static FixedFeeInvoiceConfirmation fromEntity(FixedFeeInvoiceConfirmationEntity entity) {
        return FixedFeeInvoiceConfirmation.builder()
                .id(entity.getId())
                .fixedFeeAmount(entity.getFixedFeeAmount())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
