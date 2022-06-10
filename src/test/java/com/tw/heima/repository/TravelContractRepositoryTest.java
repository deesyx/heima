package com.tw.heima.repository;

import com.tw.heima.repository.entity.FixedFeeRequestEntity;
import com.tw.heima.repository.entity.TravelContractEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@ActiveProfiles("test")
class TravelContractRepositoryTest {

    @Autowired
    private TravelContractRepository repository;

    @Test
    void should_find_contract_by_cid() {
        TravelContractEntity contractEntity = TravelContractEntity.builder()
                .cid("123")
                .fixedFeeAmount(BigDecimal.valueOf(1000))
                .fixedFeeRequest(FixedFeeRequestEntity.builder()
                        .requestId("1-2-3")
                        .fixedFeeAmount(BigDecimal.valueOf(1000))
                        .build())
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusYears(1))
                .build();
        repository.save(contractEntity);

        TravelContractEntity contract = repository.findByCid("123");

        assertThat(contract.getCid(), is("123"));
        assertThat(contract.getFixedFeeRequest().getRequestId(), is("1-2-3"));
    }
}