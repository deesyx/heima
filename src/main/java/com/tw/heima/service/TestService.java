package com.tw.heima.service;

import com.tw.heima.repository.TestRepository;
import com.tw.heima.repository.entity.TestEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;

    @Async
    @Transactional
    public void test(TestEntity testEntity) {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("wake up");
        TestEntity saveTestEntity = testRepository.save(testEntity);
        List.of(1, 2, 3, 4, 5).parallelStream().forEach(it -> {
                    Optional<TestEntity> queryTestEntity = testRepository.findById(saveTestEntity.getId());
                    if (queryTestEntity.isPresent()) {
                        log.info("query success {}, current {}", queryTestEntity.get().getId(), it);
                    } else {
                        log.info("query fail, current {}", it);
                    }
                }
        );
    }
}
