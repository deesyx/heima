package com.tw.heima.service.model;

import com.tw.heima.repository.entity.InvoiceRequestRetryRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class InvoiceRequestRetryRecord {
    private Integer id;
    private LocalDateTime createdAt;

    public InvoiceRequestRetryRecord() {
        this.createdAt = LocalDateTime.now();
    }

    public InvoiceRequestRetryRecordEntity toEntity() {
        return InvoiceRequestRetryRecordEntity.builder()
                .id(id)
                .createdAt(createdAt)
                .build();
    }

    public static InvoiceRequestRetryRecord fromEntity(InvoiceRequestRetryRecordEntity entity) {
        return InvoiceRequestRetryRecord.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
