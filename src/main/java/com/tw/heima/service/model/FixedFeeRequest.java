package com.tw.heima.service.model;

import com.tw.heima.repository.entity.FixedFeeRequestEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixedFeeRequest {
    private Integer id;
    private String requestId;
    private BigDecimal fixedFeeAmount;

    private FixedFeeConfirmation fixedFeeConfirmation;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    public FixedFeeRequestEntity toEntity() {
        return FixedFeeRequestEntity.builder()
                .id(id)
                .requestId(requestId)
                .fixedFeeAmount(fixedFeeAmount)
                .fixedFeeConfirmation(fixedFeeConfirmation.toEntity())
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .build();
    }

    public static FixedFeeRequest fromEntity(FixedFeeRequestEntity entity) {
        return FixedFeeRequest.builder()
                .id(entity.getId())
                .requestId(entity.getRequestId())
                .fixedFeeAmount(entity.getFixedFeeAmount())
                .fixedFeeConfirmation(Optional.ofNullable(entity.getFixedFeeConfirmation())
                        .map(FixedFeeConfirmation::fromEntity).orElse(null))
                .createdAt(entity.getCreatedAt())
                .expiredAt(entity.getExpiredAt())
                .build();
    }

    public boolean hasConfirm() {
        return fixedFeeConfirmation != null;
    }
}
