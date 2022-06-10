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
public class TravelContractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String cid;
    private BigDecimal fixedFeeAmount;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "travelContract")
    private FixedFeeRequestEntity fixedFeeRequest;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
}
