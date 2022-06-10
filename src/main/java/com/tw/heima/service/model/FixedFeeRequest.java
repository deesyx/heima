package com.tw.heima.service.model;

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
public class FixedFeeRequest {
    private Integer id;
    private String requestId;
    private BigDecimal fixedFeeAmount;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    public FixedFeeRequestEntity toEntity() {
        return FixedFeeRequestEntity.builder()
                .id(id)
                .requestId(requestId)
                .fixedFeeAmount(fixedFeeAmount)
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .build();
    }

    public static FixedFeeRequest fromEntity(FixedFeeRequestEntity entity) {
        return FixedFeeRequest.builder()
                .id(entity.getId())
                .requestId(entity.getRequestId())
                .fixedFeeAmount(entity.getFixedFeeAmount())
                .createdAt(entity.getCreatedAt())
                .expiredAt(entity.getExpiredAt())
                .build();
    }
}
