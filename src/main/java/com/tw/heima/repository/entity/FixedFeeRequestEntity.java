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
@Table(name = "fixed_fee_request")
public class FixedFeeRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String requestId;
    private BigDecimal fixedFeeAmount;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    @OneToOne
    @JoinColumn(name = "contract_primary_id")
    private TravelContractEntity travelContract;
}
