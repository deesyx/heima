package com.tw.heima.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoice_request_retry_record")
public class InvoiceRequestRetryRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "request_primary_id")
    private FixedFeeInvoiceRequestEntity fixedFeeInvoiceRequest;
}
