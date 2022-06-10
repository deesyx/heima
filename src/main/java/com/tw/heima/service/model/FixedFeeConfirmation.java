package com.tw.heima.service.model;

import com.tw.heima.repository.entity.FixedFeeConfirmationEntity;
import com.tw.heima.repository.entity.FixedFeeRequestEntity;
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
public class FixedFeeConfirmation {
    private Integer id;
    private BigDecimal fixedFeeAmount;
    private LocalDateTime createdAt;

    public FixedFeeConfirmationEntity toEntity() {
        return FixedFeeConfirmationEntity.builder()
                .id(id)
                .fixedFeeAmount(fixedFeeAmount)
                .createdAt(createdAt)
                .build();
    }

    public static FixedFeeConfirmation fromEntity(FixedFeeConfirmationEntity entity) {
        return FixedFeeConfirmation.builder()
                .id(entity.getId())
                .fixedFeeAmount(entity.getFixedFeeAmount())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
