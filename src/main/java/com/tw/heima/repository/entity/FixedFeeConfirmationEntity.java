package com.tw.heima.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fixed_fee_confirmation")
public class FixedFeeConfirmationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private BigDecimal fixedFeeAmount;
    private LocalDateTime createdAt;
    @OneToOne
    @JoinColumn(name = "request_primary_id")
    private FixedFeeRequestEntity fixedFeeRequest;
}
