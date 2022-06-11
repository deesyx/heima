package com.tw.heima.repository;

import com.tw.heima.repository.entity.FixedFeeConfirmationEntity;
import com.tw.heima.repository.entity.FixedFeeInvoiceRequestEntity;
import com.tw.heima.repository.entity.FixedFeeRequestEntity;
import com.tw.heima.repository.entity.TravelContractEntity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@ActiveProfiles("test")
class TravelContractRepositoryTest {

    @Autowired
    private TravelContractRepository repository;

    @Nested
    class FindByCid {
        @Test
        void should_find_contract_by_cid_when_contract_exist() {
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

            TravelContractEntity contract = repository.findByCid("123").orElseThrow();

            assertThat(contract.getCid(), is("123"));
            assertThat(contract.getFixedFeeRequest().getRequestId(), is("1-2-3"));
        }

        @Test
        void should_return_null_when_contract_not_found() {
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

            Optional<TravelContractEntity> contract = repository.findByCid("321");

            assertThat(contract.isEmpty(), is(true));
        }

        @Test
        void should_find_contract_by_cid_when_contract_exist_and_has_finish_payment() {
            FixedFeeConfirmationEntity fixedFeeConfirmation = FixedFeeConfirmationEntity.builder()
                    .fixedFeeAmount(BigDecimal.valueOf(1000))
                    .createdAt(LocalDateTime.now())
                    .build();
            TravelContractEntity contractEntity = TravelContractEntity.builder()
                    .cid("123")
                    .fixedFeeAmount(BigDecimal.valueOf(1000))
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("1-2-3")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .fixedFeeConfirmation(fixedFeeConfirmation)
                            .build())
                    .createdAt(LocalDateTime.now())
                    .expiredAt(LocalDateTime.now().plusYears(1))
                    .build();
            repository.save(contractEntity);

            TravelContractEntity contract = repository.findByCid("123").orElseThrow();

            assertThat(contract.getCid(), is("123"));
            assertThat(contract.getFixedFeeRequest().getRequestId(), is("1-2-3"));
            assertThat(contract.getFixedFeeRequest().getFixedFeeConfirmation(), is(notNullValue()));
        }
    }

    @Nested
    class Save {
        @Test
        void should_save_contract_with_fixedFeeInvoiceRequest() {
            FixedFeeConfirmationEntity fixedFeeConfirmation = FixedFeeConfirmationEntity.builder()
                    .fixedFeeAmount(BigDecimal.valueOf(1000))
                    .createdAt(LocalDateTime.now())
                    .build();
            FixedFeeInvoiceRequestEntity fixedFeeInvoiceRequest = FixedFeeInvoiceRequestEntity.builder()
                    .requestId("1-2-3")
                    .taxIdentifier("tax123")
                    .fixedFeeAmount(BigDecimal.valueOf(1000))
                    .createdAt(LocalDateTime.now())
                    .expiredAt(LocalDateTime.now().plusHours(24))
                    .build();
            TravelContractEntity contractEntity = TravelContractEntity.builder()
                    .cid("123")
                    .fixedFeeAmount(BigDecimal.valueOf(1000))
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("1-2-3")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .fixedFeeConfirmation(fixedFeeConfirmation)
                            .build())
                    .fixedFeeInvoiceRequest(fixedFeeInvoiceRequest)
                    .createdAt(LocalDateTime.now())
                    .expiredAt(LocalDateTime.now().plusYears(1))
                    .build();

            TravelContractEntity persistedContract = repository.save(contractEntity);

            assertThat(persistedContract.getCid(), is("123"));
        }
    }

    @Nested
    class FindAll {
        @Test
        void should_find_all_contract() {
            TravelContractEntity contractEntity1 = TravelContractEntity.builder()
                    .cid("123")
                    .fixedFeeAmount(BigDecimal.valueOf(123))
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("1-2-3")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .build())
                    .createdAt(LocalDateTime.now())
                    .expiredAt(LocalDateTime.now().plusYears(1))
                    .build();
            TravelContractEntity contractEntity2 = TravelContractEntity.builder()
                    .cid("456")
                    .fixedFeeAmount(BigDecimal.valueOf(456))
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("4-5-6")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .build())
                    .createdAt(LocalDateTime.now())
                    .expiredAt(LocalDateTime.now().plusYears(1))
                    .build();
            repository.saveAll(List.of(contractEntity1, contractEntity2));

            List<TravelContractEntity> contracts = repository.findAll()
                    .stream().filter(it -> it.getCid() != null).collect(Collectors.toList());

            assertThat(contracts.get(0).getCid(), is("123"));
            assertThat(contracts.get(1).getCid(), is("456"));
        }
    }
}