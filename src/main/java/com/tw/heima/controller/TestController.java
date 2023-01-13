package com.tw.heima.controller;

import com.tw.heima.repository.entity.TestEntity;
import com.tw.heima.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/test")
    public void test() {
        TestEntity testEntity = TestEntity.builder().createdAt(LocalDateTime.now()).build();
        testService.test(testEntity);
        log.info("finish");
    }
}
