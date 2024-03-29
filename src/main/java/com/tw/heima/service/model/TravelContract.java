package com.tw.heima.service.model;

import com.tw.heima.repository.entity.TravelContractEntity;
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
public class TravelContract {
    private Integer id;
    private String cid;
    private BigDecimal fixedFeeAmount;
    private FixedFeeRequest fixedFeeRequest;
    private FixedFeeInvoiceRequest fixedFeeInvoiceRequest;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    public TravelContractEntity toEntity() {
        return TravelContractEntity.builder()
                .id(id)
                .cid(cid)
                .fixedFeeAmount(fixedFeeAmount)
                .fixedFeeRequest(fixedFeeRequest.toEntity())
                .fixedFeeInvoiceRequest(fixedFeeInvoiceRequest.toEntity())
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .build();
    }

    public static TravelContract fromEntity(TravelContractEntity entity) {
        return TravelContract.builder()
                .id(entity.getId())
                .cid(entity.getCid())
                .fixedFeeAmount(entity.getFixedFeeAmount())
                .fixedFeeRequest(FixedFeeRequest.fromEntity(entity.getFixedFeeRequest()))
                .fixedFeeInvoiceRequest(Optional.ofNullable(entity.getFixedFeeInvoiceRequest())
                        .map(FixedFeeInvoiceRequest::fromEntity).orElse(null))
                .createdAt(entity.getCreatedAt())
                .expiredAt(entity.getExpiredAt())
                .build();
    }

    public void requestFixedFeeInvoice(FixedFeeInvoiceRequest fixedFeeInvoiceRequest) {
        this.fixedFeeInvoiceRequest = fixedFeeInvoiceRequest;
    }
}
